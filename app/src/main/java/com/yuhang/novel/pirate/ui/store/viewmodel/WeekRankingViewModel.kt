package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceBooksKSEntity
import com.yuhang.novel.pirate.extension.niceBooksKSResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.KanShuRankingResult
import kotlin.properties.Delegates

class WeekRankingViewModel:BaseViewModel() {

    var adapter: DelegateAdapter by Delegates.notNull()

    /**
     * 本地获取数据
     */
    suspend fun queryKanShuRanking(name:String, gender: String, type: String, date: String):List<BooksKSResult> {
        return mDataRepository.queryBooksKSEntity(name, gender, type, date).map { it.niceBooksKSResult() }.toList()
    }

    /**
     * 看书神器 排行榜
     */
    suspend fun getKanShuRanking(name:String, gender:String, type:String, date:String, pageNum:Int): KanShuRankingResult {
        val kanShuRankingList = mDataRepository.getKanShuRankingList(gender, type, date, pageNum)
        val list = kanShuRankingList.data.bookList.map { obj ->
            obj.niceBooksKSEntity().apply {
                this.date = date
                this.toobarName = name
                this.gender = gender
                this.type = type
            }
        }.toList()

        mDataRepository.deleteBooksKSEntity(name, gender, type, date)
        mDataRepository.insertBooksKSEntity(list)
        return kanShuRankingList
    }
}