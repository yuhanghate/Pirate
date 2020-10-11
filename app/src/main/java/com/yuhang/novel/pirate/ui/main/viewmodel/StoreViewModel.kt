package com.yuhang.novel.pirate.ui.main.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceRankingDataListResult
import com.yuhang.novel.pirate.extension.niceRankingListEntity
import com.yuhang.novel.pirate.repository.database.entity.RankingListEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.RankingDataListResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.RankingListResult
import com.yuhang.novel.pirate.ui.main.adapter.StoreAdapter

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
    suspend fun getRankingList(pageNum: Int): RankingListResult {
        val result = mDataRepository.getRankingList(gender, type, date, pageNum)
        insertRankingList(result)
        return result
    }

    /**
     * 获取本地数据
     */
    suspend fun getRankingListLocal(): List<RankingDataListResult> {
        val toList = arrayListOf<RankingDataListResult>()
        val list = queryRankingList()
        list.forEach {
            if (it != null) {
                toList.add(it.niceRankingDataListResult())
            }
        }
        return toList
    }

    /**
     * 查询本地排行榜
     */
    suspend fun queryRankingList(): List<RankingListEntity?> {
        return mDataRepository.queryRankingListAll()
    }

    /**
     * 插入本地排行榜
     */
    suspend fun insertRankingList(result: RankingListResult) {
        mDataRepository.insertRankingList(result.data.bookList.mapIndexed { index, rankingDataListResult ->
            rankingDataListResult.niceRankingListEntity().apply { this.index = index }
        }.toList())
    }
}