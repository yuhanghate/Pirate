package com.yuhang.novel.pirate.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.StrictMode
import android.text.TextUtils
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection
import com.meituan.android.walle.WalleChannelReader
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.tamsiree.rxkit.crash.TCrashTool
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.yuhang.novel.pirate.utils.initContext
import com.yuhang.novel.pirate.BuildConfig
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.constant.ConfigConstant
import com.yuhang.novel.pirate.push.PushUmengMessageHandler
import com.yuhang.novel.pirate.repository.DataRepository
import com.yuhang.novel.pirate.repository.network.Http
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.utils.AppManagerUtils
import com.yuhang.novel.pirate.utils.application
import kotlinx.coroutines.*
import me.yokeyword.fragmentation.Fragmentation
import kotlin.concurrent.thread


@SuppressLint("Registered")
open class PirateApp : Application(), Application.ActivityLifecycleCallbacks {


    private var mDataRepository: DataRepository? = null

    private var imgServer: String = ""

    /**
     * 全局token
     */
    private var token = ""

    private var pageType = ConfigConstant.PAGE_TYPE_HORIZONTAL

    companion object {
        var mInstance: PirateApp? = null

        /**
         * 获取Application对象
         */
        fun getInstance(): PirateApp {
            return mInstance!!
        }
    }


    override fun onCreate() {
//        initStrictModel()
        mInstance = this
        super.onCreate()
        this.registerActivityLifecycleCallbacks(this)
        initContext(this)
        initAppcation()

    }

    /**
     * 初始化Application
     */
    @SuppressLint("RestrictedApi")
    private fun initAppcation() {
        PreferenceUtil.init(this)
        pageType =
            PreferenceUtil.getInt(ConfigConstant.PAGE_TYPE, ConfigConstant.PAGE_TYPE_HORIZONTAL)
        TCrashTool.getConfig().setEnabled(false)
        initRefreshLayout()
        initFragmentManger()
        initLog()
        initYouMent()
        initToken()
        initDownload()
    }

    /**
     * 下载配置
     */
    private fun initDownload() {
        FileDownloader.setupOnApplicationOnCreate(this)
            .connectionCreator(
                FileDownloadUrlConnection.Creator(
                    FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15000) // set connection timeout.
                        .readTimeout(15000) // set read timeout.
                )
            )
            .commit()
    }

    /**
     * 严格模式
     */
    private fun initStrictModel() {
        // 分别为MainThread和VM设置Strict Mode
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()// 检测在UI线程读磁盘操作
                    .detectDiskWrites()// 检测在UI线程写磁盘操作
                    .detectCustomSlowCalls()//监测自定义运行缓慢函数
                    .detectAll()   // or .detectAll() for all detectable problems
                    .penaltyLog()//写入日志
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectActivityLeaks()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )

        }
    }

    /**
     * Fragment初始化
     */
    private fun initFragmentManger() {
        Fragmentation.builder()
            // show stack view. Mode: BUBBLE, SHAKE, NONE
            .stackViewMode(Fragmentation.NONE)
            .debug(BuildConfig.DEBUG)
            .install()
    }

    /**
     * 友盟统计初始化
     */
    private fun initYouMent() {
        GlobalScope.launch(start = CoroutineStart.UNDISPATCHED) {
            coroutineScope {

                /**
                 * 设置组件化的Log开关
                 * 参数: boolean 默认为false，如需查看LOG设置为true
                 */
                if (BuildConfig.DEBUG) {
                    UMConfigure.setLogEnabled(true)
                }

                /**
                 * 设置日志加密
                 * 参数：boolean 默认为false（不加密）
                 */
                UMConfigure.setEncryptEnabled(true)


                //获取渠道
                val channel = WalleChannelReader.getChannelInfo(application)?.channel ?: "debug"
                /**
                 * 初始化common库
                 * 参数1:上下文，不能为空
                 * 参数2:【友盟+】 AppKey
                 * 参数3:【友盟+】 Channel
                 * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
                 * 参数5:Push推送业务的secret
                 */
                UMConfigure.init(
                    application,
                    ConfigConstant.YOUMENT_KEY,
                    channel,
                    UMConfigure.DEVICE_TYPE_PHONE,
                    ConfigConstant.YOUMENT_PUSH
                )


                // 选用LEGACY_AUTO页面采集模式
                MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL)

                // 支持在子进程中统计自定义事件
                UMConfigure.setProcessEvent(true)

                val mPushAgent = PushAgent.getInstance(application)
                //注册推送服务，每次调用register方法都会回调该接口
                mPushAgent.register(object : IUmengRegisterCallback {
                    override fun onSuccess(deviceToken: String) {
                        //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                        Logger.t("UMLog").i("注册成功：deviceToken：-------->  $deviceToken")
                    }

                    override fun onFailure(s: String, s1: String) {
                        Logger.t("UMLog").i("注册失败：-------->  s:$s,s1:$s1")
                    }
                })

                mPushAgent.messageHandler = PushUmengMessageHandler()
            }
        }


    }


    /**
     * 初始化Token
     */
    private fun initToken() {

        GlobalScope.launch(start = CoroutineStart.UNDISPATCHED) {
            coroutineScope {
                val user = getDataRepository().getLastUser() ?: return@coroutineScope
                setToken(user.token)
            }
        }
    }

    /**
     * devicecode00001    uid=114 imId=uid_114 imToken=C6TbOXwI4RpPG/C8PaSZsg==
     */
    /**
     * 初始化日志文件
     */
    private fun initLog() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
            .methodCount(2)         // (Optional) How many method line to show. Default 2
            .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
            .tag("noval")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()

        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    private fun initRefreshLayout() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            MaterialHeader(context).setColorSchemeResources(R.color.primary)//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        }
    }


    fun getDataRepository(): DataRepository {
        if (mDataRepository == null) {
            mDataRepository = DataRepository()
        }
        return mDataRepository!!
    }


    /**
     * 获取图片域名
     */
    fun getImgServer(): String {
        if (TextUtils.isEmpty(imgServer)) {
            imgServer = PreferenceUtil.getString("imgServer", "")
        }
        return imgServer
    }


    fun getToken(): String {
        if (TextUtils.isEmpty(token)) {
            token = PreferenceUtil.getString("token", "")
        }
        return token
    }

    fun setToken(token: String) {
        this.token = token
        PreferenceUtil.commitString("token", token)
    }

    /**
     * 左右滑动/上下滑动
     */
    fun getPageType(): Int {
        return pageType
    }

    fun setPageType(pageType: Int) {
        this.pageType = pageType
    }


    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        AppManagerUtils.getAppManager().finishActivity(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        AppManagerUtils.getAppManager().addActivity(activity)
    }

}