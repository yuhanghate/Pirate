package com.yuhang.novel.pirate.ui.store.fragment

import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentCategoryDetailBinding
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.CategoryDetailResult
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.store.adapter.CategoryDetailAdapter
import com.yuhang.novel.pirate.ui.store.viewmodel.CategoryDetailViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 分类详情
 * 热门/好评/连载/完结
 */
class CategoryDetailFragment :
    BaseFragment<FragmentCategoryDetailBinding, CategoryDetailViewModel>(),
    OnRefreshLoadMoreListener, OnClickItemListener {


    var gender: String = ""

    /**
     * 类型: 热门/好评/完结
     */
    var type = 1

    /**
     * 类型
     */
    var major: String = ""

    var PAGE_NUM = 0

    companion object {
        fun newInstance(): CategoryDetailFragment {
            return CategoryDetailFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_category_detail
    }

    override fun initView() {
        super.initView()
        initRecyclerView()
        initRefreshLayout()
    }

    override fun initRecyclerView() {
        super.initRecyclerView()

        val layoutManager = VirtualLayoutManager(mActivity!!)
        mBinding.recyclerview.layoutManager = layoutManager
        mBinding.recyclerview.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(mActivity!!)
                .size(niceDp2px(1f))
                .color(ContextCompat.getColor(mActivity!!, R.color.list_divider_color))
                .build()
        )
        mViewModel.adapter = DelegateAdapter(layoutManager, true)
        mBinding.recyclerview.adapter = mViewModel.adapter
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this)
        mBinding.refreshLayout.autoRefresh()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

        lifecycleScope.launch {
            flow {
                emit(
                    mViewModel.getCategoryDetailList(
                        gender,
                        type = type,
                        major = major,
                        pageNum = PAGE_NUM
                    )
                )
            }
                .onStart { PAGE_NUM = 0 }
                .onCompletion { mBinding.refreshLayout.finishRefresh() }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    mViewModel.adapter.clear()
                    val adapter = CategoryDetailAdapter()
                        .setListener(this)
                        .initData(it.books)

                    mViewModel.list.addAll(it.books)
                    mViewModel.adapter.addAdapter(adapter.toAdapter())
                    adapter.notifyDataSetChanged()
                }
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        lifecycleScope.launch {
            flow {
                emit(
                    mViewModel.getCategoryDetailList(
                        gender,
                        type = type,
                        major = major,
                        pageNum = PAGE_NUM
                    )
                )
            }
                .onStart { PAGE_NUM += 51 }
                .onCompletion { mBinding.refreshLayout.finishLoadMore() }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    if (it.books.isEmpty()) {
                        //最后一页
                        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                        return@collect
                    }
                    val adapter =
                        mViewModel.adapter.findAdapterByIndex(0) as? CategoryDetailAdapter

                    mViewModel.list.addAll(it.books)
                    adapter?.getList()?.addAll(it.books)
                    adapter?.notifyDataSetChanged()
                }
        }
    }

    /**
     * 打开详情页
     */
    override fun onClickItemListener(view: View, position: Int) {
        val result = mViewModel.list[position]
        BookDetailsActivity.start(mActivity!!, result = result.niceBooksResult())
    }


}