package com.yuhang.novel.pirate.ui.book.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityReadHistoryBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadHistoryViewModel

/**
 * 最近浏览记录
 */
class ReadHistoryActivity : BaseSwipeBackActivity<ActivityReadHistoryBinding, ReadHistoryViewModel>(),
    OnRefreshLoadMoreListener, OnClickItemListener {


    companion object {
        private var PAGE_NUM = 0
        fun start(context: Activity) {
            val intent = Intent(context, ReadHistoryActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_read_history
    }

    override fun initView() {
        super.initView()
        initRefreshLayout()
        initRecyclerView()
        onClick()
    }

    private fun onClick() {
        mBinding.backIv.clickWithTrigger { onBackPressed() }
        mBinding.titleTv.text = "最近浏览"
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this)
        mBinding.refreshLayout.autoRefresh()
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        mViewModel.adapter.setListener(this)
            .setDecorationMargin(20f)
            .setlayoutManager(LinearLayoutManager(this))
            .setRecyclerView(mBinding.recyclerview)
    }

    /**
     * 加载更多
     */
    @SuppressLint("CheckResult")
    override fun onLoadMore(refreshLayout: RefreshLayout) {
        PAGE_NUM++

        mViewModel.getReadHistoryList(PAGE_NUM)
            .compose(bindToLifecycle())
            .subscribe({
                val list = it.map { it }.toList()
                if (list.isEmpty()) {
                    mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                } else {
                    mViewModel.adapter.loadMore(list)
                    mBinding.refreshLayout.finishLoadMore()
                }

            }, {
                mBinding.refreshLayout.finishLoadMore()
            })
    }

    /**
     * 下拉刷新
     */
    @SuppressLint("CheckResult")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        PAGE_NUM = 0

        mViewModel.getReadHistoryList(PAGE_NUM)
            .compose(bindToLifecycle())
            .subscribe({
                mBinding.refreshLayout.finishRefresh()
                val list = it.map { it }.toList()
                mViewModel.adapter.setRefersh(list)
                if (list.size < 100) {
                    mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                }

            }, {
                mBinding.refreshLayout.finishRefresh()
            })
    }

    /**
     * Item点击事件
     */
    override fun onClickItemListener(view: View, position: Int) {
        val obj = mViewModel.adapter.getObj(position)
        BookDetailsActivity.start(this, BooksResult().apply {
            this.author = obj.author
            this.cover = obj.cover
            this.bookName = obj.bookName
            if (obj.resouce.trim() == "" || obj.resouce.trim() == "KS") {
                this.resouce = "KS"
                this.typeKs = 1
                this.typeKd = 2
                this.bookKsId = obj.bookid
            }
            if (obj.resouce == "KD") {
                this.resouce = "KD"
                this.typeKd = 1
                this.typeKs = 2
                this.bookKdId = obj.bookid
            }
        })
    }

    override fun onPause() {
        super.onPause()
        mViewModel.onPause(this)
    }

    override fun onResume() {

        super.onResume()
        mViewModel.onResume(this)
    }
}