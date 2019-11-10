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
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivityMain2Binding
import com.yuhang.novel.pirate.eventbus.UpdateChapterEvent
import com.yuhang.novel.pirate.repository.network.data.pirate.result.VersionResult
import com.yuhang.novel.pirate.ui.main.fragment.MainFragment
import com.yuhang.novel.pirate.ui.main.fragment.MeFragment
import com.yuhang.novel.pirate.ui.main.fragment.StoreFragment
import com.yuhang.novel.pirate.ui.main.viewmodel.MainViewModel
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.concurrent.TimeUnit

@RuntimePermissions
class MainActivity : BaseActivity<ActivityMain2Binding, MainViewModel>() {

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

    fun getPushTitle() = intent.getStringExtra(PUSH_TITLE)
    fun getPushContent() = intent.getStringExtra(PUSH_CONTENT)
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
            StoreFragment.newInstance(),
            MeFragment.newInstance()
        )

//        val nearby = mBinding.bottomBar.getTabWithId(R.id.tab_main)
//        nearby.setBadgeCount(5)

        mBinding.bottomBar.setOnTabSelectListener {

            when (it) {
                R.id.tab_main -> showHideFragment(findFragment(MainFragment::class.java))
                R.id.tab_store -> showHideFragment(findFragment(StoreFragment::class.java))
                R.id.tab_me -> showHideFragment(findFragment(MeFragment::class.java))
            }
        }
//
        mBinding.bottomBar.setOnTabReselectListener {
            when (it) {
                R.id.tab_main -> showHideFragment(findFragment(MainFragment::class.java))
                R.id.tab_store -> showHideFragment(findFragment(StoreFragment::class.java))
                R.id.tab_me -> showHideFragment(findFragment(MeFragment::class.java))
            }
        }

        mBinding.bottomBar.getTabAtPosition(intent.getIntExtra("tab_index", 0)).callOnClick()

        checkVersionWithPermissionCheck()


    }


    /**
     * 延迟3秒钟, 10分钟更新一次章节目录
     */
    @SuppressLint("CheckResult")
    private fun initUpdateChapterList() {
        Flowable.interval(3, 60 * 10, TimeUnit.SECONDS)
            .flatMap { mViewModel.updateChapterToDB() }
            .subscribeOn(Schedulers.io())
            .compose(bindToLifecycle())
            .subscribe({ }, { })
    }


    override fun onBackPressedSupport() {
        moveTaskToBack(false)
    }

    /**
     * 更新所有章节信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: UpdateChapterEvent) {
        mViewModel.updateChapterToDB().compose(bindToLifecycle()).subscribeOn(Schedulers.io())
            .subscribe({}, {})
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
    @NeedsPermission(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun checkVersion() {
//        mActivity?.showProgressbar()
        mViewModel.checkVersion()
            .compose(bindToLifecycle())
            .subscribe({
                if (it.constraint) {
                    showVersionUpdateDialog(it)
                }
            }, {
            })
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
            mViewModel.onUMEvent(this, UMConstant.TYPE_VERSION_UPDATE_YES, "版本更新 -> 点击更新")

        }
        builder.setPositiveButton("取消") { p0, p1 ->
            mViewModel.onUMEvent(this, UMConstant.TYPE_VERSION_UPDATE_NO, "分享应用 -> 点击取消")
        }
        builder.show()
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
            .subscribe({entity ->
                Handler().postDelayed({
                    MaterialDialog(this).show {
                        title(text = entity?.title)
                        message(text = entity?.message)
                        positiveButton(text = "确定", click = object :DialogCallback{
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

}