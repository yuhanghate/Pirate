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

    var fragments: ArrayList<BaseFragment<*, *>>? = null

    fun getFragments(gender: String, type: String): ArrayList<BaseFragment<*, *>> {
        if (fragments == null) {
            fragments = arrayListOf(
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
        return fragments!!
    }
}