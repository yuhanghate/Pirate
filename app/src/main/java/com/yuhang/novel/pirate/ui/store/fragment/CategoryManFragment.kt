package com.yuhang.novel.pirate.ui.store.fragment

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentCategoryManBinding
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity
import com.yuhang.novel.pirate.ui.store.activity.CategoryDetailActivity
import com.yuhang.novel.pirate.ui.store.adapter.CategoryAdapter
import com.yuhang.novel.pirate.ui.store.viewmodel.CategoryManViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * 男生分类
 */
class CategoryManFragment : BaseFragment<FragmentCategoryManBinding, CategoryManViewModel>(),
    OnClickItemListener {

    companion object {
        fun newInstance(): CategoryManFragment {
            return CategoryManFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_category_man
    }

    override fun initView() {
        super.initView()
        initRecyclerView()
        netData()
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        val layoutManager = VirtualLayoutManager(mActivity!!)
        mBinding.recyclerview.layoutManager = layoutManager
        mViewModel.adapter = DelegateAdapter(layoutManager, true)
        mBinding.recyclerview.adapter = mViewModel.adapter
    }

    private fun netData() {
        lifecycleScope.launch {
            flow { emit(mViewModel.getCategoryMan()) }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    val adapter = CategoryAdapter()
                        .setListener(this@CategoryManFragment)
                        .initData(it)
                    mViewModel.list.addAll(it)
                    mViewModel.adapter.addAdapter(adapter)
                    mBinding.recyclerview.requestLayout()
                }
        }
    }

    /**
     * 点击事件
     */
    override fun onClickItemListener(view: View, position: Int) {
        val entity = mViewModel.list[position]
        CategoryDetailActivity.start(mActivity!!, entity.gender, entity.majorCate)
    }
}