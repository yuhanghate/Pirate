package com.yuhang.novel.pirate.ui.store.fragment

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentSexBinding
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.SexBooksEntity
import com.yuhang.novel.pirate.ui.book.activity.SexReadBookActivity
import com.yuhang.novel.pirate.ui.store.viewmodel.SexViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 书城 -> 小黄书
 */
class SexFragment : BaseFragment<FragmentSexBinding, SexViewModel>(), OnRefreshLoadMoreListener,
    OnClickItemListener {

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
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        mViewModel.adapter.setListener(this)
            .setDecorationSize(niceDp2px(5f))
            .setlayoutManager(LinearLayoutManager(mActivity))
            .setRecyclerView(mBinding.recyclerview)
    }

    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            flow { emit(mViewModel.queryStoreSex()) }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    withContext(Dispatchers.Main) {
                        if (it.isEmpty()) {
                            mBinding.refreshLayout.autoRefresh()
                            return@withContext
                        }
                        mViewModel.adapter.setRefersh(it)
                    }

                }
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        lifecycleScope.launch {
            flow { emit(mViewModel.getStoreSex(PAGE_NUM, false)) }
                .onCompletion { mBinding.refreshLayout.finishLoadMore() }
                .catch { Logger.e(it.message ?: "") }
                .collect { withContext(Dispatchers.Main) { mViewModel.adapter.loadMore(it) } }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        lifecycleScope.launch {
            flow { emit(mViewModel.getStoreSex(PAGE_NUM, true)) }
                .onStart { PAGE_NUM = 20 }
                .onCompletion { mBinding.refreshLayout.finishRefresh() }
                .catch { Logger.e(it.message ?: "") }
                .collect { withContext(Dispatchers.Main) { mViewModel.adapter.setRefersh(it) } }
        }
    }

    /**
     * 阅读界面
     */
    override fun onClickItemListener(view: View, position: Int) {
        val obj = mViewModel.adapter.getObj(position)
        SexReadBookActivity.start(mActivity!!, obj.niceBooksResult())
    }
}