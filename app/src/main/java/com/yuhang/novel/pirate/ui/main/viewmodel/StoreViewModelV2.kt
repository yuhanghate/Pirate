package com.yuhang.novel.pirate.ui.main.viewmodel

import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.ui.store.fragment.LadyFragment
import com.yuhang.novel.pirate.ui.store.fragment.ManFragment
import com.yuhang.novel.pirate.ui.store.fragment.SexFragment
import java.util.*

/**
 * 书城
 */
class StoreViewModelV2 : BaseViewModel() {

    var lastTabEntity = 0

    val mTitles: ArrayList<String> = arrayListOf()
    val mFragments: ArrayList<BaseFragment<*, *>> = arrayListOf()

    /**
     * 获取标题
     */
    fun getTitles(): ArrayList<String> {
        if (mTitles.isNotEmpty()) return mTitles
        mTitles.addAll(arrayListOf("小黄书", "男生榜", "女生榜"))

        return mTitles
    }

    fun getFragments(): ArrayList<BaseFragment<*, *>> {
        if (mFragments.isNotEmpty()) return mFragments
        mFragments.add(SexFragment.newInstance())
        mFragments.add(ManFragment.newInstance())
        mFragments.add(LadyFragment.newInstance())


        return mFragments
    }

}