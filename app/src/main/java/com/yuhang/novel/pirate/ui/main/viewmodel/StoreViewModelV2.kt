package com.yuhang.novel.pirate.ui.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.ui.store.fragment.LadyFragment
import com.yuhang.novel.pirate.ui.store.fragment.ManFragment
import com.yuhang.novel.pirate.ui.store.fragment.SexFragment
import kotlinx.coroutines.launch
import java.util.*

/**
 * 书城
 */
class StoreViewModelV2 : BaseViewModel() {

    var lastTabEntity = 0

    val mTitles: ArrayList<String> = arrayListOf<String>()
    val mFragments: ArrayList<BaseFragment<*, *>> = arrayListOf()


    var isVip = false

    suspend fun getUser() = mDataRepository.getLastUser()

    /**
     * 获取标题
     */
    fun getTitles(): ArrayList<String> {
        if (mTitles.isNotEmpty()) return mTitles
        if (!isVip) {
            mTitles.addAll(arrayListOf<String>("男生", "女生"))
        } else {
            mTitles.addAll(arrayListOf<String>("男生", "女生", "小黄书"))
        }

        return mTitles
    }

    fun getFragments(): ArrayList<BaseFragment<*, *>> {
        if (mFragments.isNotEmpty()) return mFragments
        if (!isVip) {
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