package com.yuhang.novel.pirate.ui.store.fragment

import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentCategoryLadyBinding
import com.yuhang.novel.pirate.ui.store.viewmodel.CategoryLadyViewModel

/**
 * 女生分类
 */
class CategoryLadyFragment :BaseFragment<FragmentCategoryLadyBinding, CategoryLadyViewModel>(){

    companion object{
        fun newInstance(): CategoryLadyFragment {
            return CategoryLadyFragment()
        }
    }
    override fun onLayoutId(): Int {
        return R.layout.fragment_category_lady
    }
}