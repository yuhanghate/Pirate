package com.yuhang.novel.pirate.ui.main.viewmodel

import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.ui.store.fragment.LadyFragment
import com.yuhang.novel.pirate.ui.store.fragment.ManFragment
import com.yuhang.novel.pirate.ui.store.fragment.SexFragment
import java.util.ArrayList

/**
 * 书城
 */
class StoreViewModelV2:BaseViewModel() {

    var lastTabEntity = 0

    val mTitles: ArrayList<String> = arrayListOf<String>( "男生", "女生", "小黄书")
    val mFragments: ArrayList<BaseFragment<*, *>> = arrayListOf(ManFragment.newInstance(), LadyFragment.newInstance(), SexFragment.newInstance())

}