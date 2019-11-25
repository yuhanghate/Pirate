package com.yuhang.novel.pirate.ui.store.viewmodel

import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.ui.store.fragment.MonthRankingFragment
import com.yuhang.novel.pirate.ui.store.fragment.TotalRankingFragment
import com.yuhang.novel.pirate.ui.store.fragment.WeekRankingFragment
import java.util.*

/**
 * 看书神器,榜单
 */
class KanShuRankingViewModel : BaseViewModel() {

    var lastTabEntity = 0

    val mTitles: ArrayList<String> = arrayListOf<String>("周榜", "月榜", "总榜")
//    val mFragments: ArrayList<BaseFragment<*, *>> = arrayListOf(
//        WeekRankingFragment.newInstance(),
//        MonthRankingFragment.newInstance(),
//        TotalRankingFragment.newInstance()
//    )


    fun getFragments(gender: String, type: String):ArrayList<BaseFragment<*, *>> {
        return arrayListOf(
            WeekRankingFragment.newInstance().apply {
                this.gender = gender
                this.type = type
            }, MonthRankingFragment.newInstance().apply {
                this.gender = gender
                this.type = type
            },
            TotalRankingFragment.newInstance().apply {
                this.gender = gender
                this.type = type
            }
        )
    }
}