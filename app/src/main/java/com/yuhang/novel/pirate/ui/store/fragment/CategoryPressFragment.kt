package com.yuhang.novel.pirate.ui.store.fragment

import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentCategoryPressBinding
import com.yuhang.novel.pirate.ui.store.viewmodel.CategoryPressViewModel

/**
 * 出版分类
 */
class CategoryPressFragment:BaseFragment<FragmentCategoryPressBinding, CategoryPressViewModel>() {

    companion object{
        fun newInstance(): CategoryPressFragment {
            return CategoryPressFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_category_press
    }
}