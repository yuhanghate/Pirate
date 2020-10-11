package com.yuhang.novel.pirate.ui.store.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.SexBooksEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.ChapterSexResult
import com.yuhang.novel.pirate.ui.store.adapter.BookSexAdapter

class SexViewModel : BaseViewModel() {

    val adapter by lazy { BookSexAdapter() }


    /**
     * 随机获取小黄书列表
     */
     suspend fun getBookSexList(pageNum: Int): ChapterSexResult {
        return mDataRepository.getBookSexList(pageNum)
    }


    /**
     * 本地随机获取小黄书列表
     */
    suspend fun queryStoreSex(): List<SexBooksEntity> {
        return mDataRepository.querySexBooksEntityAll()
    }


    /**
     * 随机获取小黄书列表
     */
    suspend fun getStoreSex(pageNum: Int, isClean:Boolean): List<SexBooksEntity> {
        val bookSexList = mDataRepository.getBookSexList(pageNum)
        if (isClean) {
            mDataRepository.cleanSexBooksEntity()
        }
        mDataRepository.insert(bookSexList.data)
        return bookSexList.data
    }

}