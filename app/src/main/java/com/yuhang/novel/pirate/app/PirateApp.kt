package com.yuhang.novel.pirate.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.github.moduth.blockcanary.BlockCanary
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.yuhang.novel.pirate.BuildConfig
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.repository.DataRepository
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.utils.AppManagerUtils






@SuppressLint("Registered")
open class PirateApp : Application(), Application.ActivityLifecycleCallbacks {

    private var mDataRepository: DataRepository? = null

    private var imgServer:String = ""
    private var userid :String = ""
    private var userAvatar : String = ""
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
        mInstance = this
        super.onCreate()
        initAppcation()
        this.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 初始化Application
     */
    fun initAppcation() {
        PreferenceUtil.init(this)
        initRefreshLayout()
        initLog()
        BlockCanary.install(this, AppContext()).start()

        // 分别为MainThread和VM设置Strict Mode
//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectAll()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build())
//
//            StrictMode.setVmPolicy(VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build())
//
//        }
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
            .methodCount(0)         // (Optional) How many method line to show. Default 2
            .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
            .tag("live")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
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
            //                layout.setPrimaryColorsId(R.color.primary, android.R.color.white)//全局设置主题颜色
            MaterialHeader(context).setColorSchemeResources(R.color.primary)//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        }
    }


    fun getDataRepository(): DataRepository {
        if (mDataRepository == null) {
            mDataRepository = DataRepository(this)
        }
        return mDataRepository!!
    }



    /**
     * 获取图片域名
     */
    fun getImgServer() :String{
        if (TextUtils.isEmpty(imgServer)) {
            imgServer = PreferenceUtil.getString("imgServer","")
        }
        return imgServer
    }



    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        AppManagerUtils.getAppManager().finishActivity(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        AppManagerUtils.getAppManager().addActivity(activity)
    }
}