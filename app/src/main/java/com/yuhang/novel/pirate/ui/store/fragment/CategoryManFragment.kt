package com.yuhang.novel.pirate.ui.store.fragment

import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentCategoryManBinding
import com.yuhang.novel.pirate.ui.store.viewmodel.CategoryManViewModel

/**
 * 男生分类
 */
class CategoryManFragment:BaseFragment<FragmentCategoryManBinding, CategoryManViewModel>() {

    companion object{
        fun newInstance(): CategoryManFragment {
            return CategoryManFragment()
        }
    }
    override fun onLayoutId(): Int {
        return R.layout.fragment_category_man
    }
}