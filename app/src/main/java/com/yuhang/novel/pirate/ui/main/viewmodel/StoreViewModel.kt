package com.yuhang.novel.pirate.ui.main.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookCategoryResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.RankingListResult
import com.yuhang.novel.pirate.ui.main.adapter.StoreAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StoreViewModel:BaseViewModel() {

    val adapter by lazy { StoreAdapter() }

    /**
     * 频道
     */
    var gender = "man"

    /**
     * 类型
     */
    var type = "hot"

    /**
     * 日期
     */
    var date = "week"

    /**
     * 男生 -> 分类
     */
    fun getBookCategory(): Flowable<BookCategoryResult> {
        return mDataRepository.getBookCategory().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取排行榜
     */
    fun getRankingList(pageNum:Int): Flowable<RankingListResult> {
        return mDataRepository.getRankingList(gender, type, date, pageNum)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}