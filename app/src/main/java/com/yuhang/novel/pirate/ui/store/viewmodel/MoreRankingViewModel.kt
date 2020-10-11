package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceBooksKSEntity
import com.yuhang.novel.pirate.extension.niceBooksKSResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.MoreRankingResult
import kotlin.properties.Delegates

class MoreRankingViewModel:BaseViewModel() {

    var adapter : DelegateAdapter by Delegates.notNull()

    /**
     * 从本地获取数据
     */
    suspend fun queryMoreRankingList(name: String, gender: String, type: Int):List<BooksKSResult> {
        return mDataRepository.queryBooksKSEntity(name, gender, type.toString(),"").map { it.niceBooksKSResult() }.toList()
    }

    /**
     * 正版排行榜
     *
     * 起点/纵横/去起/若初/红薯/潇湘/逐浪
     */
    suspend fun getMoreRankingList(name: String, gender:String, type:Int, pageNum:Int): MoreRankingResult {
        val moreRankingList = mDataRepository.getMoreRankingList(gender, type, pageNum)
        val list = moreRankingList.data.bookList.map { obj ->
            obj.niceBooksKSEntity().apply {
                this.date = date
                this.toobarName = name
                this.gender = gender
                this.type = type.toString()
            }
        }.toList()

        mDataRepository.deleteBooksKSEntity(name, gender, type.toString())
        mDataRepository.insertBooksKSEntity(list)
        return moreRankingList
    }
}