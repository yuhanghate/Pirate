package com.yuhang.novel.pirate.ui.ad.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.View
import co.mobiwise.library.ProgressLayout
import com.liulishuo.filedownloader.model.FileDownloadStatus
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.vondear.rxtool.RxAppTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseDownloadBackActivity
import com.yuhang.novel.pirate.databinding.ActivityGameBinding
import com.yuhang.novel.pirate.eventbus.DownloadStatusEvent
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceToast
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
import kotlin.experimental.or

/**
 * 游戏推荐
 */
@RuntimePermissions
class GameActivity : BaseDownloadBackActivity<ActivityGameBinding, GameViewModel>(),
    OnRefreshLoadMoreListener, OnClickItemLongListener,
    OnClickGameDownloadListener {

    var PAGE_NUM = 1

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
        initRefreshLayout()
        onClick()
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this)
        onRefresh(mBinding.refreshLayout)
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        mViewModel.adapter
            .setListener(this)
            .setRecyclerView(mBinding.recyclerview)
            .initData(arrayListOf())
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
        mBinding.btnBack.clickWithTrigger { onBackPressedSupport() }
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
     * 加载更多
     */
    override fun onLoadMore(refreshLayout: RefreshLayout) {
        PAGE_NUM++

        mViewModel.getGameRecommentList(PAGE_NUM)
            .compose(bindToLifecycle())
            .subscribe({
                mViewModel.adapter.loadMore(mViewModel.createTask(it.data.list))
                mBinding.refreshLayout.finishLoadMore()
                if (mViewModel.adapter.itemCount == it.data.total) {
                    mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                }
            }, {
                mBinding.refreshLayout.finishLoadMore()
            })
    }


    /**
     * 刷新
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        PAGE_NUM = 1
        mViewModel.getGameRecommentList(PAGE_NUM)
            .compose(bindToLifecycle())
            .subscribe({
                mViewModel.adapter.setRefersh(mViewModel.createTask(it.data.list))
                mBinding.refreshLayout.finishRefresh()
                if (mViewModel.adapter.itemCount == it.data.total) {
                    mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                }
            }, {
                mBinding.refreshLayout.finishRefresh()
            })
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
    }


    /**
     * 按钮点击事件
     */
    override fun onGameDownloadListener(obj: GameDataResult, position: Int) {
        when (DownloadServiceImpl.getDownloadStatus(obj.downloadUrl)) {
            FileDownloadStatus.completed -> {
                //安装
                installApkWithPermissionCheck(DownloadServiceImpl.getDownloadPath(obj.downloadUrl))
                return
            }
        }

        //打开
        if (RxAppTool.isInstallApp(this, obj.packageName)) {
            RxAppTool.launchApp(this, obj.packageName)
            return
        }
        DownloadServiceImpl.start(this, obj.downloadUrl, obj.name)

    }


    /**
     * 长按清除缓存
     */
    override fun onClickItemLongListener(view: View, position: Int) {
        DownloadDeleteDialog(this, mViewModel, mViewModel.adapter.getObj(position)).show()
    }


    /**
     * 更新进度条
     */
    private fun updateProgressView(url: String, progress: Int) {
        mViewModel.objList[url]?.also { position ->
            val vh =
                mBinding.recyclerview.findViewHolderForLayoutPosition(position) as? ItemGameVH
            vh?.mBinding?.progressLayout?.setCurrentProgress(progress)
            vh?.mBinding?.downloadTv?.text = "暂停"
        }
    }

    /**
     * 更新按钮
     */
    private fun updateDownloadTextView(url: String, text: String, isProgress: Boolean) {
        mViewModel.objList[url]?.also { position ->
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
            DownloadServiceImpl.SERVICE_ACTION_START -> {
                updateDownloadTextView(obj.url, "暂停", false)
                niceToast("开始下载...")
            }
            DownloadServiceImpl.SERVICE_ACTION_PENDING -> {
                updateDownloadTextView(obj.url,"暂停",false)
                niceToast("开始下载...")
            }
            DownloadServiceImpl.SERVICE_ACTION_ERROR -> updateDownloadTextView(obj.url, "重试", true)
            DownloadServiceImpl.SERVICE_ACTION_COMPLETED -> {
                //跳安装
                updateDownloadTextView(obj.url, "安装", false)
                installApkWithPermissionCheck(obj.path)
            }
            DownloadServiceImpl.SERVICE_ACTION_DELETE -> {
                updateDownloadTextView(obj.url, "下载", false)
            }

        }
    }

}