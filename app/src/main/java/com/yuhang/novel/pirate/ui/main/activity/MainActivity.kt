package com.yuhang.novel.pirate.ui.main.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.gyf.immersionbar.ImmersionBar
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Progress
import com.orhanobut.logger.Logger
import com.trello.rxlifecycle2.android.ActivityEvent
import com.vondear.rxtool.RxAppTool
import com.vondear.rxtool.RxEncryptTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivityMain2Binding
import com.yuhang.novel.pirate.databinding.DialogVersionUpdateBinding
import com.yuhang.novel.pirate.eventbus.UpdateChapterEvent
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.network.NetURL
import com.yuhang.novel.pirate.repository.network.data.pirate.result.VersionResult
import com.yuhang.novel.pirate.ui.main.fragment.MainFragment
import com.yuhang.novel.pirate.ui.main.fragment.MeFragment
import com.yuhang.novel.pirate.ui.main.fragment.StoreFragmentV2
import com.yuhang.novel.pirate.ui.main.viewmodel.MainViewModel
import com.yuhang.novel.pirate.ui.user.activity.LoginActivity
import com.yuhang.novel.pirate.utils.DownloadUtil
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit

@RuntimePermissions
class MainActivity : BaseActivity<ActivityMain2Binding, MainViewModel>() {
    var binding: DialogVersionUpdateBinding? = null

    var dialog: AlertDialog? = null

    var result: VersionResult? = null

    companion object {

        private const val PUSH_TITLE = "push_title"
        private const val PUSH_CONTENT = "push_content"
        fun start(context: Context, title: String, content: String = "") {
            val intent = Intent(context, MainActivity::class.java)
            intent.setPackage(context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(PUSH_TITLE, title)
            intent.putExtra(PUSH_CONTENT, content)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_main2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateEventbus(this)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    override fun initStatusTool() {
        ImmersionBar.with(this)
            .statusBarView(mBinding.statusBarV)
            .navigationBarColor(R.color.md_white_1000)
            .flymeOSStatusBarFontColor(R.color.primary_text)
            .statusBarDarkFont(true)
            .autoDarkModeEnable(true)
            .init()
    }

    override fun onDestroy() {
        onDestryEventbus(this)
        super.onDestroy()

    }

    override fun initView() {
        super.initView()


        initUpdateChapterList()
        loadMultipleRootFragment(
            R.id.nav_host_fragment,
            intent.getIntExtra("tab_index", 0),
            MainFragment.newInstance(),
            StoreFragmentV2.newInstance(),
            MeFragment.newInstance()
        )

        mBinding.bottomBar.setOnTabSelectListener {

            when (it) {
                R.id.tab_main -> {
                    ImmersionBar.with(this)
                        .statusBarView(mBinding.statusBarV)
                        .statusBarColor(R.color.window_background)
                        .flymeOSStatusBarFontColor(R.color.primary_text)
                        .init()
                    showHideFragment(findFragment(MainFragment::class.java))
                }
                R.id.tab_store -> {
                    ImmersionBar.with(this)
                        .statusBarView(mBinding.statusBarV)
                        .statusBarColor(R.color.window_background)
                        .flymeOSStatusBarFontColor(R.color.primary_text)
                        .init()
                    startLoginActivity()
                    showHideFragment(findFragment(StoreFragmentV2::class.java))
                }
                R.id.tab_me -> {
                    ImmersionBar.with(this)
                        .statusBarView(mBinding.statusBarV)
                        .statusBarColor(R.color.md_grey_f2)
                        .flymeOSStatusBarFontColor(R.color.md_white_1000)
                        .init()
                    showHideFragment(findFragment(MeFragment::class.java))
                }
            }
        }
//
        mBinding.bottomBar.setOnTabReselectListener {
            when (it) {
                R.id.tab_main -> showHideFragment(findFragment(MainFragment::class.java))
                R.id.tab_store -> {
                    val storeFragmentV2 = findFragment(StoreFragmentV2::class.java)
                    showHideFragment(storeFragmentV2)
                    storeFragmentV2.onTabReselect(storeFragmentV2.mViewModel.lastTabEntity)
                    Flowable.just("").map { throw NullPointerException("") }
                        .compose(io_main())
                        .subscribe({},{})
                }
                R.id.tab_me -> showHideFragment(findFragment(MeFragment::class.java))
            }
        }

        mBinding.bottomBar.getTabAtPosition(intent.getIntExtra("tab_index", 0)).callOnClick()



        Handler().postDelayed({ checkVersionWithPermissionCheck() }, 1000 * 2)

        initCategory()
        initConfig()
    }

    /**
     * 预加载分类数据
     */
    private fun initCategory() {
        mViewModel.preloadCategory()
            .compose(bindToLifecycle())
            .subscribe({},{})
    }

    /**
     * 加载配置文件
     */
    private fun initConfig() {
        mViewModel.preloadConfig()
            .compose(bindToLifecycle())
            .subscribe({},{})
    }

    /**
     * 打开登陆界面
     */
    private fun startLoginActivity() {
        if (mViewModel.isShowLoginDialog()) {
            Handler().postDelayed({
                //第一次安装打开登陆界面
                LoginActivity.startForResult(this)
            }, 1000 * 2)
        }

    }


    /**
     * 延迟3秒钟, 10分钟更新一次章节目录
     */
    @SuppressLint("CheckResult")
    private fun initUpdateChapterList() {
        Flowable.interval(1, 60 * 5, TimeUnit.SECONDS)
            .flatMap { mViewModel.updateChapterToDB() }
            .compose(io_main())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe({ }, { })
    }


    override fun onBackPressedSupport() {
        moveTaskToBack(true)
    }

    /**
     * 更新所有章节信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: UpdateChapterEvent) {
        mViewModel.updateChapterToDB()
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe({
                Logger.i("")
            }, {
                Logger.i("")
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
     * 版本升级
     */
    @NeedsPermission(
        Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE
    )
    fun checkVersion() {
        if (mViewModel.isShowVersionDialog()) {
            mViewModel.checkVersion()
                .compose(bindToLifecycle())
                .subscribe({
                    if (it.update == "Yes") {
                        showVersionUpdateDialog(it)
                    }
                }, {})
        }

    }


    @SuppressLint("NoDelegateOnResumeDetector")
    override fun onResume() {
        super.onResume()
        mViewModel.onResume(this)

        showNoteDialog()
    }

    override fun onPause() {
        super.onPause()
        mViewModel.onPause(this)

    }

    /**
     * 提示公告
     */
    @SuppressLint("CheckResult")
    private fun showNoteDialog() {

        mViewModel.getPushMessageEntity()
            .compose(bindToLifecycle())
            .subscribe({ entity ->
                Handler().postDelayed({
                    MaterialDialog(this).show {
                        title(text = entity?.title)
                        message(text = entity?.message)
                        positiveButton(text = "确定", click = object : DialogCallback {
                            override fun invoke(p1: MaterialDialog) {
                                mViewModel.deletePushMessage(entity!!)
                                p1.dismiss()
                            }
                        })
                        cancelable(cancelable = false)
                    }
                }, 1200)

            }, {})
    }

    /**
     * 新版本Dialog
     */
    private fun showVersionUpdateDialog(result: VersionResult) {

        // 构建 OkHttpClient 时,将 OkHttpClient.Builder() 传入 with() 方法,进行初始化配置
        val builder = AlertDialog.Builder(this)
        builder.setTitle("检测到新版本")
        builder.setMessage(mViewModel.getMessage(result))
        //点击对话框以外的区域是否让对话框消失
        builder.setCancelable(true)
        builder.setNegativeButton("更新") { p0, p1 ->
            this.result = result
            mViewModel.onUMEvent(this, UMConstant.TYPE_VERSION_UPDATE_YES, "版本更新 -> 点击更新")
            if (mViewModel.installProcess()) {
                showVersionUpdateProgress(result)
            }

        }
        builder.setPositiveButton("取消") { p0, p1 ->
            mViewModel.onUMEvent(this, UMConstant.TYPE_VERSION_UPDATE_NO, "分享应用 -> 点击取消")
        }
        builder.show()
    }

    /**
     * 更新进度条显示
     */
    @SuppressLint("CheckResult", "SetTextI18n")
    private fun showVersionUpdateProgress(result: VersionResult) {
        binding = DialogVersionUpdateBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(binding?.root)
        builder.setCancelable(false)
        dialog = builder.show()


        /**
         * 下载APK文件
         */
        val url = "${NetURL.HOST_RESOUCE}${result.apkFileUrl}"
        val outputApk =
            DownloadUtil.PATH_CHALLENGE_VIDEO + File.separator + RxEncryptTool.encryptMD5File2String(url) + ".apk"
        OkGo.get<File>(url).execute(object :
            com.lzy.okgo.callback.FileCallback(
                DownloadUtil.PATH_CHALLENGE_VIDEO,
                RxEncryptTool.encryptMD5File2String(url) + ".apk"
            ) {
            override fun onSuccess(response: com.lzy.okgo.model.Response<File>) {
                dialog?.dismiss()
                RxAppTool.installApp(this@MainActivity, outputApk)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 10086) {
            //再次执行安装流程，包含权限判等
            this.result?.let { showVersionUpdateProgress(it) }

        }
    }
}