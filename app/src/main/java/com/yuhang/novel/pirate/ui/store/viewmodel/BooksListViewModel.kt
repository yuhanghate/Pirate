package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.database.entity.BooksKSEntity
import com.yuhang.novel.pirate.repository.database.entity.ShuDanEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksListDataResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksListResult
import io.reactivex.Flowable
import kotlin.properties.Delegates

class BooksListViewModel:BaseViewModel() {

    var adapter : DelegateAdapter by Delegates.notNull()

    val list = arrayListOf<ShuDanEntity>()

    /**
     * 从本地查询
     */
    fun queryBooksKSEntity(name: String, gender: String, type: String) :Flowable<List<ShuDanEntity>>{
        return Flowable.just("")
            .map { mDataRepository.queryShuDanEntity(name, gender, type) }
            .compose(io_main())
    }

    /**
     * 获取书单
     *
     * 最新发布/本周最热/最多收藏/小编推荐
     */
    fun getBooksList(name: String, gender: String, type: String, pageNum: Int): Flowable<BooksListResult> {
        return mDataRepository.getBooksList(gender, type, pageNum)
            .map {
                val entity = it.data.map {
                    it.apply {
                        this.type = type
                        this.toolbarName = name
                        this.gender = gender
                    }
                }.toList()
                mDataRepository.deleteShuDanEntity(name, gender, type)
                mDataRepository.insertShuDanEntity(entity)
                it
            }
            .compose(io_main())
    }
}