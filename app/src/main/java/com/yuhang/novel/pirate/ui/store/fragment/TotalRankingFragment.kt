package com.yuhang.novel.pirate.ui.store.fragment

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentTotalRankingBinding
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.listener.OnClickMoreRankingListListener
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.KanShuRankingResult
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.store.activity.KanShuRankingActivity
import com.yuhang.novel.pirate.ui.store.adapter.MoreRankingListAdapter
import com.yuhang.novel.pirate.ui.store.viewmodel.TotalRankingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 月榜
 */
class TotalRankingFragment : BaseFragment<FragmentTotalRankingBinding, TotalRankingViewModel>(),
    OnRefreshLoadMoreListener, OnClickMoreRankingListListener {

    //性别
    var gender: String = ""

    //类型
    var type: String = ""

    //标题名称
    var name: String = ""

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
//        mBinding.refreshLayout.autoRefresh()
    }

    override fun initData() {
        super.initData()

        lifecycleScope.launch {
            flow {
                emit(
                    mViewModel.queryKanShuRanking(
                        name,
                        gender,
                        type,
                        KanShuRankingActivity.TYPE_TOTAL
                    )
                )
            }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    withContext(Dispatchers.Main){
                        if (it.isEmpty()) {
                            mBinding.refreshLayout.autoRefresh()
                            return@withContext
                        }
                        mViewModel.adapter.clear()
                        val adapters = arrayListOf<DelegateAdapter.Adapter<RecyclerView.ViewHolder>>()
                        val adapter = MoreRankingListAdapter()
                            .setListener(this@TotalRankingFragment)
                            .initData(it)
                        adapters.add(adapter.toAdapter())
                        mViewModel.adapter.addAdapters(adapters)
                        mBinding.recyclerview.requestLayout()
                    }

                }
        }
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
//        addOnScrollListener(mBinding.recyclerview)
        val layoutManager = VirtualLayoutManager(mActivity!!)
        mBinding.recyclerview.layoutManager = layoutManager
        mViewModel.adapter = DelegateAdapter(layoutManager, true)
        mBinding.recyclerview.adapter = mViewModel.adapter
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

        lifecycleScope.launch {
            flow {
                emit(
                    mViewModel.getKanShuRanking(
                        name,
                        gender,
                        type,
                        KanShuRankingActivity.TYPE_TOTAL,
                        PAGE_NUM
                    )
                )
            }
                .onStart { PAGE_NUM = 1 }
                .onCompletion { mBinding.refreshLayout.finishRefresh() }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    withContext(Dispatchers.Main){
                        if (!it.data.isHasNext) {
                            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                        }
                        mViewModel.adapter.clear()
                        val adapters = arrayListOf<DelegateAdapter.Adapter<RecyclerView.ViewHolder>>()
                        val adapter = MoreRankingListAdapter()
                            .setListener(this@TotalRankingFragment)
                            .initData(it.data.bookList)
                        adapters.add(adapter.toAdapter())
                        mViewModel.adapter.addAdapters(adapters)
                        mBinding.recyclerview.requestLayout()
                    }

                }
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {

        lifecycleScope.launch {
            flow {
                emit(
                    mViewModel.getKanShuRanking(
                        name,
                        gender,
                        type,
                        KanShuRankingActivity.TYPE_TOTAL,
                        PAGE_NUM
                    )
                )
            }
                .onStart { PAGE_NUM++ }
                .onCompletion { mBinding.refreshLayout.finishLoadMore() }
                .collect {
                    withContext(Dispatchers.Main){
                        if (!it.data.isHasNext) {
                            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                        }
                        val adapter = MoreRankingListAdapter()
                            .setListener(this@TotalRankingFragment)
                            .initData(it.data.bookList)
                        mViewModel.adapter.addAdapter(adapter.toAdapter())
                    }

                }
        }
    }

    /**
     * 点击进入书籍详情页
     */
    override fun onClickMoreRankingListListener(obj: BooksKSResult, position: Int) {
        BookDetailsActivity.start(mActivity!!, obj.niceBooksResult())
    }
}