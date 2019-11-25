package com.yuhang.novel.pirate.ui.store.fragment

import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentTotalRankingBinding
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.listener.OnClickMoreRankingListListener
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.store.activity.KanShuRankingActivity
import com.yuhang.novel.pirate.ui.store.adapter.MoreRankingListAdapter
import com.yuhang.novel.pirate.ui.store.viewmodel.TotalRankingViewModel

/**
 * 月榜
 */
class TotalRankingFragment : BaseFragment<FragmentTotalRankingBinding, TotalRankingViewModel>(),
    OnRefreshLoadMoreListener, OnClickMoreRankingListListener {

    //性别
    var gender: String = ""

    //类型
    var type: String = ""

    var PAGE_NUM = 1

    companion object {
        fun newInstance(): TotalRankingFragment {
            return TotalRankingFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_total_ranking
    }

    override fun initView() {
        super.initView()
        initRecyclerView()
        initRefreshLayout()
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this)
        mBinding.refreshLayout.autoRefresh()
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        val layoutManager = VirtualLayoutManager(mActivity!!)
        mBinding.recyclerview.layoutManager = layoutManager
        mViewModel.adapter = DelegateAdapter(layoutManager, true)
        mBinding.recyclerview.adapter = mViewModel.adapter
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

        PAGE_NUM = 1
        mViewModel.getKanShuRanking(gender, type, KanShuRankingActivity.TYPE_TOTAL, PAGE_NUM)
            .compose(bindToLifecycle())
            .subscribe({

                if (!it.data.isHasNext) {
                    mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                }

                mViewModel.adapter.clear()

                val adapters = arrayListOf<DelegateAdapter.Adapter<RecyclerView.ViewHolder>>()

                val adapter = MoreRankingListAdapter()
                    .setListener(this)
                    .initData(it.data.bookList)

                adapters.add(adapter.toAdapter())

                mViewModel.adapter.addAdapters(adapters)
                mBinding.recyclerview.requestLayout()
                mBinding.refreshLayout.finishRefresh()
            }, { mBinding.refreshLayout.finishRefresh() })
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        PAGE_NUM++

        mViewModel.getKanShuRanking(gender, type, KanShuRankingActivity.TYPE_TOTAL, PAGE_NUM)
            .compose(bindToLifecycle())
            .subscribe({
                if (!it.data.isHasNext) {
                    mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                }

                val adapter = MoreRankingListAdapter()
                    .setListener(this)
                    .initData(it.data.bookList)

                mViewModel.adapter.addAdapter(adapter.toAdapter())
                mBinding.refreshLayout.finishLoadMore()
            }, {
                mBinding.refreshLayout.finishLoadMore()
            })
    }

    /**
     * 点击进入书籍详情页
     */
    override fun onClickMoreRankingListListener(obj: BooksKSResult, position: Int) {
        BookDetailsActivity.start(mActivity!!, obj.niceBooksResult())
    }
}