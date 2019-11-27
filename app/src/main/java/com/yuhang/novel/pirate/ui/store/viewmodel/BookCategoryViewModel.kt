package com.yuhang.novel.pirate.ui.store.viewmodel

import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.ui.store.fragment.CategoryLadyFragment
import com.yuhang.novel.pirate.ui.store.fragment.CategoryManFragment
import com.yuhang.novel.pirate.ui.store.fragment.CategoryPressFragment
import java.util.*

class BookCategoryViewModel : BaseViewModel() {

    var lastTabEntity = 0

    val mTitles: ArrayList<String> = arrayListOf<String>("男生", "女生", "出版")

    fun getFragments(): ArrayList<BaseFragment<*, *>> {
        return arrayListOf(
            CategoryManFragment.newInstance(), CategoryLadyFragment.newInstance(),
            CategoryPressFragment.newInstance()
        )
    }
}