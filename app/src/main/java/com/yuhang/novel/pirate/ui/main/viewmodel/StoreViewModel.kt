package com.yuhang.novel.pirate.ui.main.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceRankingDataListResult
import com.yuhang.novel.pirate.extension.niceRankingListEntity
import com.yuhang.novel.pirate.repository.database.entity.RankingListEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.RankingDataListResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.RankingListResult
import com.yuhang.novel.pirate.ui.main.adapter.StoreAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StoreViewModel : BaseViewModel() {

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
     * 获取排行榜
     */
    fun getRankingList(pageNum: Int): Flowable<RankingListResult> {
        return mDataRepository.getRankingList(gender, type, date, pageNum).map {
            insertRankingList(it)
            it
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取本地数据
     */
    fun getRankingListLocal(): Flowable<List<RankingDataListResult?>> {
     return Flowable.just("")
         .map { queryRankingList().map { it?.niceRankingDataListResult() }.toList() }
         .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 查询本地排行榜
     */
    fun queryRankingList(): List<RankingListEntity?> {
        return mDataRepository.queryRankingListAll()
    }

    /**
     * 插入本地排行榜
     */
    fun insertRankingList(result: RankingListResult) {
        mDataRepository.insertRankingList(result.data.bookList.mapIndexed { index, rankingDataListResult -> rankingDataListResult.niceRankingListEntity().apply { this.index = index } }.toList())
    }
}