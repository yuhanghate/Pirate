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

    val mTitles: ArrayList<String> = arrayListOf<String>()
    val mFragments: ArrayList<BaseFragment<*, *>> = arrayListOf()

    /**
     * 获取标题
     */
    fun getTitles(): ArrayList<String> {
        if (mTitles.isNotEmpty()) return mTitles
        val lastUser = mDataRepository.getLastUser()?.isVip
        if (lastUser == null || !lastUser) {
            mTitles.addAll(arrayListOf<String>("男生", "女生"))
        } else {
            mTitles.addAll(arrayListOf<String>("男生", "女生", "小黄书"))
        }

        return mTitles
    }

    fun getFragments(): ArrayList<BaseFragment<*, *>> {
        if (mFragments.isNotEmpty()) return mFragments
        val lastUser = mDataRepository.getLastUser()?.isVip
        if (lastUser == null || !lastUser) {
            mFragments.add(ManFragment.newInstance())
            mFragments.add(LadyFragment.newInstance())
        } else {
            mFragments.add(ManFragment.newInstance())
            mFragments.add(LadyFragment.newInstance())
            mFragments.add(SexFragment.newInstance())
        }

        return mFragments
    }

}