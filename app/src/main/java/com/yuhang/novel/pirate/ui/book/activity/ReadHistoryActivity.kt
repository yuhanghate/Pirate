package com.yuhang.novel.pirate.ui.book.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityReadHistoryBinding
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadHistoryViewModel

/**
 * 最近浏览记录
 */
class ReadHistoryActivity : BaseSwipeBackActivity<ActivityReadHistoryBinding, ReadHistoryViewModel>(), OnRefreshLoadMoreListener, OnClickItemListener {



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
        mBinding.backIv.setOnClickListener { onBackPressed() }
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
                    val list = it.filterNotNull().map { it }.toList()
                    if (list.isEmpty()) {
                        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                    } else {
                        mViewModel.adapter.loadMore(list)

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
                    if (list.size < 20) {
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
        BookDetailsActivity.start(this, mViewModel.adapter.getObj(position).bookid)
    }
}