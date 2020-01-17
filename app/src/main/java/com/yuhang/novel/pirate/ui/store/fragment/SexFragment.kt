package com.yuhang.novel.pirate.ui.store.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentSexBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.ui.store.viewmodel.SexViewModel

class SexFragment : BaseFragment<FragmentSexBinding, SexViewModel>(), OnRefreshLoadMoreListener {

    var PAGE_NUM = 20

    companion object {
        fun newInstance(): SexFragment {
            return SexFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_sex
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
        mViewModel.adapter.setListener(this)
            .setDecorationSize(niceDp2px(5f))
            .setlayoutManager(LinearLayoutManager(mActivity))
            .setRecyclerView(mBinding.recyclerview)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel.getBookSexList(PAGE_NUM)
            .subscribe({
                mBinding.refreshLayout.finishLoadMore()
                val list = it.data
                mViewModel.adapter.loadMore(list)
            }, { mBinding.refreshLayout.finishLoadMore() })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        PAGE_NUM = 20
        mViewModel.getBookSexList(PAGE_NUM)
            .subscribe({
                mBinding.refreshLayout.finishRefresh()
                val list = it.data
                mViewModel.adapter.setRefersh(list)
            }, {
                mBinding.refreshLayout.finishRefresh()
            })
    }
}