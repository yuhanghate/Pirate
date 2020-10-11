package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.ShuDanEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksListResult
import kotlin.properties.Delegates

class BooksListViewModel : BaseViewModel() {

    var adapter: DelegateAdapter by Delegates.notNull()

    val list = arrayListOf<ShuDanEntity>()

    /**
     * 从本地查询
     */
    suspend fun queryBooksKSEntity(name: String, gender: String, type: String): List<ShuDanEntity> {
        return mDataRepository.queryShuDanEntity(name, gender, type)
    }

    /**
     * 获取书单
     *
     * 最新发布/本周最热/最多收藏/小编推荐
     */
    suspend fun getBooksList(
        name: String,
        gender: String,
        type: String,
        pageNum: Int,
    ): BooksListResult {
        val booksListResult = mDataRepository.getBooksList(gender, type, pageNum)
        val entity = booksListResult.data.map {
            it.apply {
                this.type = type
                this.toolbarName = name
                this.gender = gender
            }
        }.toList()
        mDataRepository.deleteShuDanEntity(name, gender, type)
        mDataRepository.insertShuDanEntity(entity)
        return booksListResult
    }
}