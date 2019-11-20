package com.yuhang.novel.pirate.ui.ad.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import co.mobiwise.library.ProgressLayout
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.StatusUtil
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.vondear.rxtool.RxAppTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseDownloadBackActivity
import com.yuhang.novel.pirate.databinding.ActivityGameBinding
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.listener.OnClickGameDownloadListener
import com.yuhang.novel.pirate.listener.OnClickItemLongListener
import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameDataResult
import com.yuhang.novel.pirate.ui.ad.dialog.DownloadDeleteDialog
import com.yuhang.novel.pirate.ui.ad.viewmodel.GameViewModel
import com.yuhang.novel.pirate.utils.DownloadUtil
import com.yuhang.novel.pirate.viewholder.ItemGameVH
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

/**
 * 游戏推荐
 */
@RuntimePermissions
class GameActivity : BaseDownloadBackActivity<ActivityGameBinding, GameViewModel>(),
    OnRefreshListener, OnClickItemLongListener,
    OnClickGameDownloadListener, DownloadListener {

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, GameActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_game
    }

    override fun initView() {
        super.initView()
        DownloadDispatcher.setMaxParallelRunningCount(4)
        initRecyclerView()
        onClick()
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.refreshLayout.setOnRefreshListener(this)
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        mViewModel.adapter
            .setListener(this)
            .setRecyclerView(mBinding.recyclerview)
            .initData(mViewModel.createTask(mViewModel.getTestData()))
    }

    override fun onResume() {
        super.onResume()
        mViewModel.adapter.notifyDataSetChanged()
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
    }


    /**
     * 安装apk
     */
    @NeedsPermission(
        Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE
    )
    fun installApk(file: String) {
        if (mViewModel.installProcess()) {
            RxAppTool.installApp(this, file)
        }

    }


    /**
     * 刷新
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        android.os.Handler().postDelayed({
            mViewModel.adapter.setRefersh(mViewModel.createTask(mViewModel.getTestData()))
            mBinding.refreshLayout.finishRefresh()
        }, 900)
    }

    /**
     * 点击下载
     */
    override fun onGameDownloadStartListener(
        view: ProgressLayout,
        obj: GameDataResult,
        position: Int
    ) {


        view.start()

        mViewModel.progressView[obj.downloadUrl] = view

        //如果当前任务已经成功,显示安装
        if (StatusUtil.getStatus(obj.task!!) == StatusUtil.Status.COMPLETED) {
            val holder =
                mBinding.recyclerview.findViewHolderForAdapterPosition(position) as ItemGameVH
            holder.mBinding.downloadTv.text = "安装"
            return
        }
        obj.task?.enqueue(this)
    }

    /**
     * 点击暂停
     */
    override fun onGameDownloadPauseListener(
        view: ProgressLayout,
        obj: GameDataResult,
        position: Int
    ) {
        view.stop()
        mViewModel.progressView.remove(obj.downloadUrl)
        obj.task?.cancel()
    }

    /**
     * 点击打开
     */
    override fun onGameDownloadOpenListener(
        view: ProgressLayout,
        obj: GameDataResult,
        position: Int
    ) {

        RxAppTool.launchApp(this, obj.packageName)

    }

    /**
     * 安装apk
     */
    override fun onGameDownloadInstallListener(
        view: ProgressLayout,
        obj: GameDataResult,
        position: Int
    ) {

        val path = obj.task?.file?.absolutePath
        if (TextUtils.isEmpty(path)) {
            niceToast("安装包不存在")
            return
        }

        installApkWithPermissionCheck(path!!)
    }

    /**
     * 下载进度
     */
    override fun fetchProgress(task: DownloadTask, blockIndex: Int, increaseBytes: Long) {

        task.info ?: return
        mViewModel.progressView[task.tag as String]?.let {
            DownloadUtil.calcProgressToView(it, task.info?.totalOffset!!, task.info?.totalLength!!)
        }

    }

    /**
     * 下载完成
     */
    override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {
        super.fetchEnd(task, blockIndex, contentLength)
        task.info ?: return
        if (StatusUtil.getStatus(task) == StatusUtil.Status.COMPLETED) {
            mViewModel.progressView[task.tag as String]?.setCurrentProgress(0)
            task.file?.absolutePath?.let { installApkWithPermissionCheck(it) }
            return
        }
    }


    /**
     * 任务结束
     */
    override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
        super.taskEnd(task, cause, realCause)
        //任务失败重试
        if (cause == EndCause.ERROR || cause == EndCause.PRE_ALLOCATE_FAILED) {
            task.enqueue(this)
        }
    }

    override fun taskStart(task: DownloadTask) {
        super.taskStart(task)
    }

    /**
     * 长按清除缓存
     */
    override fun onClickItemLongListener(view: View, position: Int) {
        DownloadDeleteDialog(this, mViewModel, mViewModel.adapter.getObj(position)).show()
    }

    override fun onBackPressedSupport() {
        OkDownload.with().downloadDispatcher().cancelAll()
        super.onBackPressedSupport()
    }

}