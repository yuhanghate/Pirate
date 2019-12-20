package com.yuhang.novel.pirate.ui.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Progress
import com.orhanobut.logger.Logger
import com.vondear.rxtool.RxAppTool
import com.vondear.rxtool.RxEncryptTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.DialogVersionUpdateBinding
import com.yuhang.novel.pirate.databinding.FragmentMeBinding
import com.yuhang.novel.pirate.eventbus.LoginEvent
import com.yuhang.novel.pirate.eventbus.LogoutEvent
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.network.NetURL
import com.yuhang.novel.pirate.repository.network.data.pirate.result.UserResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.VersionResult
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.service.UsersService
import com.yuhang.novel.pirate.service.impl.UsersServiceImpl
import com.yuhang.novel.pirate.ui.ad.activity.GameActivity
import com.yuhang.novel.pirate.ui.book.activity.ReadHistoryActivity
import com.yuhang.novel.pirate.ui.download.activity.BookDownloadActivity
import com.yuhang.novel.pirate.ui.main.activity.MainActivity
import com.yuhang.novel.pirate.ui.main.viewmodel.MeViewModel
import com.yuhang.novel.pirate.ui.settings.activity.ProblemActivity
import com.yuhang.novel.pirate.ui.settings.activity.SettingsActivity
import com.yuhang.novel.pirate.ui.user.activity.LoginActivity
import com.yuhang.novel.pirate.utils.DownloadUtil
import com.yuhang.novel.pirate.utils.ImageUtils
import com.yuhang.novel.pirate.utils.ThemeHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File


/**
 * 我的
 */
@Suppress("IMPLICIT_CAST_TO_ANY")
@RuntimePermissions
class MeFragment : BaseFragment<FragmentMeBinding, MeViewModel>() {


    private val mUsersService: UsersService by lazy { UsersServiceImpl() }

    /**
     * 第一次加载
     */
    private var isInitView = false

    var binding: DialogVersionUpdateBinding? = null

    var dialog: AlertDialog? = null


    companion object {
        fun newInstance(): MeFragment {
            return MeFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_me
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        mViewModel.onPageStart("我的页面")
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        mViewModel.onPageEnd("我的页面")
    }


    override fun initView() {
        super.initView()
//        FileDownloader.setup(mActivity)
        EventBus.getDefault().register(this)
        onClick()
        initUserInfoView()
//        checkVersionWithPermissionCheck()
    }

    /**
     * 初始化用户信息
     */
    @SuppressLint("CheckResult")
    private fun initUserInfoView() {

        //初始化模式
        if (PreferenceUtil.getString(
                "themePref",
                ThemeHelper.LIGHT_MODE
            ) == ThemeHelper.DARK_MODE
        ) {
            mBinding.subjectModeIv.setImageResource(R.drawable.ic_sun)
        } else {
            mBinding.subjectModeIv.setImageResource(R.drawable.ic_moon)
        }

        //控制游戏显示或隐藏
        mViewModel.queryConfig()
            .compose(bindToLifecycle())
            .subscribe({
                if (it.showGameRecommended) {
                    mBinding.gamesCl.visibility = View.VISIBLE
                } else {
                    mBinding.gamesCl.visibility = View.GONE
                }
            }, {})

        mViewModel.getUserInfo()
            .compose(bindToLifecycle())
            .subscribe({
                if (it == null) {
                    onClick()
                    mBinding.btnLogin.text = "立即登录"
                    mBinding.btnLogin.textSize = 24f
                    mBinding.loginDescTv.visibility = View.VISIBLE
                } else {
                    mBinding.btnLogin.text = "随友:${it?.username}"
                    mBinding.loginDescTv.visibility = View.GONE
                    mBinding.btnLogin.textSize = 18f
                    mBinding.avatarIv.setImageResource(R.drawable.ic_default_login_avatar)
                    //登录界面
                    mBinding.btnLogin.setOnClickListener { }
                    //登录
                    mBinding.avatarCiv.setOnClickListener { }
                }

            }, {
                onClick()
                mBinding.btnLogin.text = "立即登录"
                mBinding.btnLogin.textSize = 24f
            })
    }

    private fun onClick() {
        //分享
        mBinding.shareCl.setOnClickListener {
            mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_ME_CLICK_APP_SHARE, "我的 -> 分享应用")
            showShareDialog()
        }
        //最新浏览
        mBinding.historyCl.setOnClickListener {
            mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_ME_CLICK_HISTORY, "我的 -> 最近浏览")
            if (PirateApp.getInstance().getToken().isEmpty()) {
                showHistoryDialog()
            } else {
                ReadHistoryActivity.start(mActivity!!)
            }
        }
        //登录界面
        mBinding.btnLogin.setOnClickListener {
            LoginActivity.start(mActivity!!)
        }
        //登录
        mBinding.avatarCiv.setOnClickListener { LoginActivity.start(mActivity!!) }
        //检测版本更新
        mBinding.checkVersionCl.setOnClickListener {
            mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_ME_CLICK_VERSION_CHECK, "我的 -> 检测升级")
            isInitView = true
            if (mViewModel.installProcess()) {
                checkVersionWithPermissionCheck()
            }

        }
        //设置
        mBinding.settingsCl.setOnClickListener {
            mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_ME_CLICK_SETTINGS, "我的 -> 设置")
            SettingsActivity.start(mActivity!!)
        }

        mBinding.modelCl.setOnClickListener {
            resetModel()
        }

        //微信公众号
        mBinding.wechatCl.setOnClickListener {

            showWechatDialog()
        }

        //发起添加QQ群
        mBinding.qqCl.setOnClickListener { joinQQGroup("mzgZcP9d4kxXSalbfHSTyn89Q2grCtE9") }

        //帮助与问题
        mBinding.problemCl.setOnClickListener { ProblemActivity.start(mActivity!!) }

        //缓存管理
        mBinding.downloadCl.setOnClickListener { BookDownloadActivity.start(mActivity!!) }

        //游戏推荐
        mBinding.gamesCl.setOnClickListener { GameActivity.start(mActivity!!) }

    }

    /****************
     *
     * 发起添加群流程。群号：随便看书交流群(764214448) 的 key 为： mzgZcP9d4kxXSalbfHSTyn89Q2grCtE9
     * 调用 joinQQGroup(mzgZcP9d4kxXSalbfHSTyn89Q2grCtE9) 即可发起手Q客户端申请加群 随便看书交流群(764214448)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    fun joinQQGroup(key: String): Boolean {
        val intent = Intent();
        intent.data =
            Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key);
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true;
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            niceToast("未安装手机QQ或安装的版本不支持")
            false
        }
    }


    /**
     * 日间/夜间模式
     */
    private fun resetModel() {
        val model = PreferenceUtil.getString("themePref", ThemeHelper.LIGHT_MODE)
        if (model == ThemeHelper.DARK_MODE) {
            PreferenceUtil.commitString("themePref", ThemeHelper.LIGHT_MODE)
            ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)
            mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_ME_CLICK_MODEL, "夜间模式")
        } else {
            mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_ME_CLICK_MODEL, "日间模式")
            PreferenceUtil.commitString("themePref", ThemeHelper.DARK_MODE)
            ThemeHelper.applyTheme(ThemeHelper.DARK_MODE)
        }
        val newIntent = Intent(mActivity, MainActivity::class.java)
        newIntent.putExtra("tab_index", 2)
        startActivity(newIntent)
        mActivity?.finish()
        mActivity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    /**
     * 分享Dialog
     */
    private fun showWechatDialog() {
        MaterialDialog(mActivity!!).show {
            customView(viewRes = R.layout.dialog_wechat_group)
//            title(text = "温馨提示")
//            message(text = "链接复制成功,请分享给您的好友.发送给好友的复制内容是:\n\r \n\r我正在用随便看书APP看免费百万本小说。下载地址 https://fir.im/a9u7")
            negativeButton(text = "取消", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                }
            })
            positiveButton(text = "保存", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {

                    val dView = p1.getCustomView()
                    dView.setDrawingCacheEnabled(true)
                    dView.buildDrawingCache();
                    val bitmap = Bitmap.createBitmap(dView.getDrawingCache());


//                    val res = getResources();
//                    val bmp = BitmapFactory.decodeResource(res, R.drawable.ic_wechat_qrcode)
                    ImageUtils.saveImageToGallery(mActivity!!, bitmap)

                    ImageUtils.getWechatApi(mActivity!!)
                    niceToast("保存成功,可以分享给朋友了")
                }
            })
        }
    }


    /**
     * 分享Dialog
     */
    private fun showShareDialog() {
        MaterialDialog(mActivity!!).show {
            title(text = "温馨提示")
            message(text = "链接复制成功,请分享给您的好友.发送给好友的复制内容是:\n\r \n\r我正在用随便看书APP看免费百万本小说。下载地址 https://fir.im/a9u7")
            negativeButton(text = "取消", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_SHARE_APP_NO, "分享应用 -> 点击取消")
                }
            })
            positiveButton(text = "分享", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_SHARE_APP_YES, "分享应用 -> 点击取消")
                    //获取剪贴板管理器：
                    val cm =
                        mActivity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                    // 创建普通字符型ClipData
                    val mClipData = ClipData.newPlainText(
                        "Label",
                        "我正在用随便看书APP看免费百万本小说。下载地址 https://fir.im/a9u7"
                    )
                    // 将ClipData内容放到系统剪贴板里。
                    cm!!.setPrimaryClip(mClipData)

                    val textIntent = Intent(Intent.ACTION_SEND)
                    textIntent.type = "text/plain"
                    textIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "我正在用随便看书APP看免费百万本小说。下载地址 https://fir.im/a9u7"
                    )
                    startActivity(Intent.createChooser(textIntent, "温馨提示"))

                    niceToast("复制成功,可以分享给朋友了")
                }
            })
        }
    }


    /**
     * 版本升级
     */
    @SuppressLint("CheckResult")
    @NeedsPermission(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun checkVersion() {
//        mActivity?.showProgressbar()
        mViewModel.checkVersion()
            .compose(bindToLifecycle())
            .subscribe({
                mActivity?.closeProgressbar()
                if (it.update == "Yes") {
                    mBinding.versionNameTv.text = "可升级"

                    if (isInitView) {
                        showVersionUpdateDialog(it)
                        isInitView = false
                    }

                } else {
                    if (isInitView) {
                        niceToast("当前已是最新版本")
                        isInitView = false
                    }

                    mBinding.versionNameTv.text = ""
                }
            }, {
            })
    }


    /**
     * 新版本Dialog
     */
    private fun showVersionUpdateDialog(result: VersionResult) {

        // 构建 OkHttpClient 时,将 OkHttpClient.Builder() 传入 with() 方法,进行初始化配置
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setTitle("检测到新版本")
        builder.setMessage(mViewModel.getMessage(result))
        //点击对话框以外的区域是否让对话框消失
        builder.setCancelable(true)
        builder.setNegativeButton("更新") { p0, p1 ->
            mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_VERSION_UPDATE_YES, "版本更新 -> 点击更新")

            showVersionUpdateProgress(result)
        }
        builder.setPositiveButton("取消") { p0, p1 ->
            mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_VERSION_UPDATE_NO, "分享应用 -> 点击取消")
        }
        builder.show()
    }

    /**
     * 更新进度条显示
     */
    @SuppressLint("CheckResult", "SetTextI18n")
    private fun showVersionUpdateProgress(result: VersionResult) {
        binding = DialogVersionUpdateBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setView(binding?.root)
        builder.setCancelable(false)
        dialog = builder.show()


        /**
         * 下载APK文件
         */
        val url = "${NetURL.HOST_RESOUCE}${result.apkFileUrl}"
        val outputApk =
            DownloadUtil.PATH_CHALLENGE_VIDEO + File.separator + RxEncryptTool.encryptMD5File2String(
                url
            ) + ".apk"
        OkGo.get<File>(url).execute(object :
            com.lzy.okgo.callback.FileCallback(
                DownloadUtil.PATH_CHALLENGE_VIDEO,
                RxEncryptTool.encryptMD5File2String(url) + ".apk"
            ) {
            override fun onSuccess(response: com.lzy.okgo.model.Response<File>) {
                dialog?.dismiss()
                RxAppTool.installApp(mActivity, outputApk)
            }

            override fun onStart(request: com.lzy.okgo.request.base.Request<File, out com.lzy.okgo.request.base.Request<*, *>>?) {
                super.onStart(request)

            }

            override fun onError(response: com.lzy.okgo.model.Response<File>) {
                super.onError(response)
                dialog?.dismiss()
                niceToast("下载异常,请重试")
            }

            override fun downloadProgress(progress: Progress?) {
                super.downloadProgress(progress)

                val currentProgress = (progress?.fraction!! * 100).toInt()
                Logger.i("fraction = $currentProgress")
                binding?.progressTv?.text = "$currentProgress%"
                binding?.progressHorizontal?.progress = currentProgress
            }
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }

    /**
     * 弹出同步收藏数据
     */
    @SuppressLint("CheckResult")
    private fun showUpdateCollectDialog() {
        showProgressbar(message = "正在同步大量数据\n请不要切换出APP并耐心等待..")

        //从服务器下载收藏
        mUsersService.updateCollectionToLocal()
//            .flatMap { mUsersService.updateChapterListToLocal(it) }
            .flatMap { mUsersService.updateBookInfoToLocal(it) }
            .flatMap { mUsersService.updateReadHistoryToLocal(it) }
//            .flatMap { mUsersService.updateContentToLocal(it) }
            .compose(bindToLifecycle())
            .compose(io_main())
            .subscribe({
            }, {
                closeProgressbar()
                showUpdateCollectionDialog()
            }, {
                closeProgressbar()
                EventBus.getDefault().postSticky(LoginEvent())
            })

    }

    /**
     * 显示同步失败Dialog
     */
    private fun showUpdateCollectionDialog() {
        MaterialDialog(mActivity!!).show {
            message(text = "同步收藏数据失败,是否重试")
            negativeButton(text = "取消")
            positiveButton(text = "同步", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    showUpdateCollectDialog()
                }
            })
        }
    }

    /**
     * 最近浏览界面需要登陆才可以查看.提示框
     */
    private fun showHistoryDialog() {
        MaterialDialog(mActivity!!).show {
            title(text = "提示")
            message(text = "登录以后可以查看浏览记录")
            negativeButton(text = "取消")
            positiveButton(text = "登录", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    LoginActivity.start(mActivity!!)
                }
            })
        }

    }


    /**
     * 登录/注册成功回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(userResult: UserResult) {
        initUserInfoView()
        showUpdateCollectDialog()
    }

    /**
     * 退出登录回调
     */
    @Subscribe
    fun onEvent(obj: LogoutEvent) {
        initUserInfoView()
    }
}