package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceBooksKSEntity
import com.yuhang.novel.pirate.extension.niceBooksKSResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.KanShuRankingResult
import io.reactivex.Flowable
import kotlin.properties.Delegates

class TotalRankingViewModel:BaseViewModel() {

    var adapter: DelegateAdapter by Delegates.notNull()


    /**
     * 本地获取数据
     */
    fun queryKanShuRanking(name:String, gender: String, type: String, date: String):Flowable<List<BooksKSResult>> {
        return Flowable.just("")
            .map {
                mDataRepository.queryBooksKSEntity(name, gender, type, date).map { it.niceBooksKSResult() }.toList()
            }.compose(io_main())
    }
    /**
     * 看书神器 排行榜
     */
    fun getKanShuRanking(name: String, gender:String, type:String, date:String, pageNum:Int): Flowable<KanShuRankingResult> {
        return mDataRepository.getKanShuRankingList(gender, type, date, pageNum)
            .map {
                val list = it.data.bookList.map {obj ->
                    obj.niceBooksKSEntity().apply {
                        this.date = date
                        this.toobarName = name
                        this.gender = gender
                        this.type = type
                    }
                }.toList()
                mDataRepository.deleteBooksKSEntity(name, gender, type, date)
                mDataRepository.insertBooksKSEntity(list)
                it
            }
            .compose(io_main())
    }
}