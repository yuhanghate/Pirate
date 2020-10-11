package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.CategoryDetailResult
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.SearchDataKdResult
import com.yuhang.novel.pirate.ui.store.fragment.CategoryDetailFragment
import java.util.*
import kotlin.properties.Delegates

class CategoryDetailViewModel : BaseViewModel() {

    var adapter: DelegateAdapter by Delegates.notNull()

    var lastTabEntity = 0

    val list = arrayListOf<SearchDataKdResult>()

    val mTitles: ArrayList<String> = arrayListOf("热门", "好评", "连载", "完结")

    var fragments: ArrayList<BaseFragment<*, *>> = arrayListOf()

    fun getFragments(gender: String, major: String): ArrayList<BaseFragment<*, *>> {
        if (fragments.isEmpty()) {
            fragments = arrayListOf(
                CategoryDetailFragment.newInstance().apply {
                    this.gender = gender
                    this.major = major
                    this.type = 1
                }, CategoryDetailFragment.newInstance().apply {
                    this.gender = gender
                    this.major = major
                    this.type = 2
                },
                CategoryDetailFragment.newInstance().apply {
                    this.gender = gender
                    this.major = major
                    this.type = 3
                },
                CategoryDetailFragment.newInstance().apply {
                    this.gender = gender
                    this.major = major
                    this.type = 4
                }
            )

        }
        return fragments
    }

    /**
     * 分类详情
     */
    suspend fun getCategoryDetailList(
        gender: String,
        type: Int,
        major: String,
        pageNum: Int
    ): CategoryDetailResult {
        return mDataRepository.getCategoryDetailList(gender, type, major, pageNum)

    }
}