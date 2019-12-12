package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceBooksKSEntity
import com.yuhang.novel.pirate.extension.niceBooksKSResult
import com.yuhang.novel.pirate.repository.database.entity.BooksKSEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.MoreRankingResult
import io.reactivex.Flowable
import kotlin.properties.Delegates

class MoreRankingViewModel:BaseViewModel() {

    var adapter : DelegateAdapter by Delegates.notNull()

    /**
     * 从本地获取数据
     */
    fun queryMoreRankingList(name: String, gender: String, type: Int):Flowable<List<BooksKSResult>> {
        return Flowable.just("")
            .map {
                mDataRepository.queryBooksKSEntity(name, gender, type.toString(),"").map { it.niceBooksKSResult() }.toList()
            }.compose(io_main())
    }

    /**
     * 正版排行榜
     *
     * 起点/纵横/去起/若初/红薯/潇湘/逐浪
     */
    fun getMoreRankingList(name: String, gender:String, type:Int, pageNum:Int): Flowable<MoreRankingResult> {
        return mDataRepository.getMoreRankingList(gender, type, pageNum)
            .map {
                val list = it.data.bookList.map {obj ->
                    obj.niceBooksKSEntity().apply {
                        this.date = date
                        this.toobarName = name
                        this.gender = gender
                        this.type = type.toString()
                    }
                }.toList()
                mDataRepository.deleteBooksKSEntity(name, gender, type.toString())
                mDataRepository.insertBooksKSEntity(list)
                it
            }
            .compose(io_main())
    }
}