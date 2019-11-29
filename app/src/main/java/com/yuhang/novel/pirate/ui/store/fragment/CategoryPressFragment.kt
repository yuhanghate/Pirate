package com.yuhang.novel.pirate.ui.store.fragment

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentCategoryPressBinding
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.ui.store.activity.CategoryDetailActivity
import com.yuhang.novel.pirate.ui.store.adapter.CategoryAdapter
import com.yuhang.novel.pirate.ui.store.viewmodel.CategoryPressViewModel

/**
 * 出版分类
 */
class CategoryPressFragment:BaseFragment<FragmentCategoryPressBinding, CategoryPressViewModel>(),
    OnClickItemListener {

    companion object{
        fun newInstance(): CategoryPressFragment {
            return CategoryPressFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_category_press
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
        mViewModel.getCategoryPress()
            .compose(bindToLifecycle())
            .subscribe({
                val adapter = CategoryAdapter()
                    .setListener(this)
                    .initData(it)
                mViewModel.list.addAll(it)
                mViewModel.adapter.addAdapter(adapter)
                mBinding.recyclerview.requestLayout()
            },{})
    }

    /**
     * 点击事件
     */
    override fun onClickItemListener(view: View, position: Int) {
        val entity = mViewModel.list[position]
        CategoryDetailActivity.start(mActivity!!, entity.gender, entity.majorCate)
    }
}