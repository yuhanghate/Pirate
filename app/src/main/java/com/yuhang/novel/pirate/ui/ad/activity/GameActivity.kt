package com.yuhang.novel.pirate.ui.ad.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.View
import co.mobiwise.library.ProgressLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.vondear.rxtool.RxAppTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseDownloadBackActivity
import com.yuhang.novel.pirate.databinding.ActivityGameBinding
import com.yuhang.novel.pirate.eventbus.DownloadStatusEvent
import com.yuhang.novel.pirate.listener.OnClickGameDownloadListener
import com.yuhang.novel.pirate.listener.OnClickItemLongListener
import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameDataResult
import com.yuhang.novel.pirate.service.impl.DownloadServiceImpl
import com.yuhang.novel.pirate.ui.ad.dialog.DownloadDeleteDialog
import com.yuhang.novel.pirate.ui.ad.viewmodel.GameViewModel
import com.yuhang.novel.pirate.viewholder.ItemGameVH
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

/**
 * 游戏推荐
 */
@RuntimePermissions
class GameActivity : BaseDownloadBackActivity<ActivityGameBinding, GameViewModel>(),
    OnRefreshListener, OnClickItemLongListener,
    OnClickGameDownloadListener {

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
        onCreateEventbus(this)
        mViewModel.adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        onDestryEventbus(this)
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

        DownloadServiceImpl.start(this, obj.downloadUrl, obj.name)

//        view.start()
//
//        mViewModel.progressView[obj.downloadUrl] = view
//
//        //如果当前任务已经成功,显示安装
//        if (StatusUtil.getStatus(obj.task!!) == StatusUtil.Status.COMPLETED) {
//            val holder =
//                mBinding.recyclerview.findViewHolderForAdapterPosition(position) as ItemGameVH
//            holder.mBinding.downloadTv.text = "安装"
//            return
//        }
//        obj.task?.enqueue(this)
    }

    /**
     * 点击暂停
     */
    override fun onGameDownloadPauseListener(
        view: ProgressLayout,
        obj: GameDataResult,
        position: Int
    ) {
//        view.stop()
//        mViewModel.progressView.remove(obj.downloadUrl)
//        obj.task?.cancel()
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

//        val path = obj.task?.path
//        if (TextUtils.isEmpty(path)) {
//            niceToast("安装包不存在")
//            return
//        }
//
//        installApkWithPermissionCheck(path!!)
    }

    /**
     * 按钮点击事件
     */
    override fun onGameDownloadListener(obj: GameDataResult, position: Int) {
        DownloadServiceImpl.start(this, obj.downloadUrl, obj.name)
    }

    /**
     * 下载进度
     */
//    override fun fetchProgress(task: DownloadTask, blockIndex: Int, increaseBytes: Long) {
//
//        task.info ?: return
//        mViewModel.progressView[task.tag as String]?.let {
//            DownloadUtil.calcProgressToView(it, task.info?.totalOffset!!, task.info?.totalLength!!)
//        }
//
//    }

    /**
     * 下载完成
     */
//    override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {
//        super.fetchEnd(task, blockIndex, contentLength)
//        task.info ?: return
//        if (StatusUtil.getStatus(task) == StatusUtil.Status.COMPLETED) {
//            mViewModel.progressView[task.tag as String]?.setCurrentProgress(0)
//            task.file?.absolutePath?.let { installApkWithPermissionCheck(it) }
//            return
//        }
//    }


    /**
     * 任务结束
     */
//    override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
//        super.taskEnd(task, cause, realCause)
//        //任务失败重试
//        if (cause == EndCause.ERROR || cause == EndCause.PRE_ALLOCATE_FAILED) {
//            task.enqueue(this)
//        }
//    }


    /**
     * 长按清除缓存
     */
    override fun onClickItemLongListener(view: View, position: Int) {
        DownloadDeleteDialog(this, mViewModel, mViewModel.adapter.getObj(position)).show()
    }

    override fun onBackPressedSupport() {
//        OkDownload.with().downloadDispatcher().cancelAll()
        super.onBackPressedSupport()
    }

    /**
     * 更新进度条
     */
    private fun updateProgressView(url: String, progress: Int) {
        val position = mViewModel.objList[url]?.also { position ->
            val vh =
                mBinding.recyclerview.findViewHolderForLayoutPosition(position) as? ItemGameVH
            vh?.mBinding?.progressLayout?.setCurrentProgress(progress)
            vh?.mBinding?.downloadTv?.text = "暂停"
        }
    }

    /**
     * 更新按钮
     */
    private fun updateDownloadTextView(url: String, text:String, isProgress:Boolean) {
        val position = mViewModel.objList[url]?.also { position ->
            val vh =
                mBinding.recyclerview.findViewHolderForLayoutPosition(position) as? ItemGameVH
            vh?.mBinding?.downloadTv?.text = text
            if (isProgress) {
                vh?.mBinding?.progressLayout?.start()
            } else {
                vh?.mBinding?.progressLayout?.cancel()
            }

        }
    }

    /**
     * 下载状态回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: DownloadStatusEvent) {
        when (obj.status) {
            DownloadServiceImpl.SERVICE_ACTION_INSTALL -> installApkWithPermissionCheck(obj.path)
            DownloadServiceImpl.SERVICE_ACTION_PROGRESS -> updateProgressView(obj.url, obj.progress)
            DownloadServiceImpl.SERVICE_ACTION_PAUSE -> updateDownloadTextView(obj.url, "继续", true)
            DownloadServiceImpl.SERVICE_ACTION_START -> updateDownloadTextView(obj.url, "暂停", false)
            DownloadServiceImpl.SERVICE_ACTION_PENDING -> updateDownloadTextView(obj.url, "暂停", false)
            DownloadServiceImpl.SERVICE_ACTION_ERROR -> updateDownloadTextView(obj.url, "重试", true)
            DownloadServiceImpl.SERVICE_ACTION_COMPLETED -> updateDownloadTextView(obj.url, "安装", false)
            DownloadServiceImpl.SERVICE_ACTION_DELETE -> updateDownloadTextView(obj.url, "下载", false)

        }
    }

}