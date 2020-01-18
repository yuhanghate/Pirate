package com.yuhang.novel.pirate.ui.store.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceBooksKSResult
import com.yuhang.novel.pirate.repository.database.entity.SexBooksEntity
import com.yuhang.novel.pirate.repository.database.entity.StoreEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.ChapterSexResult
import com.yuhang.novel.pirate.ui.store.adapter.BookSexAdapter
import io.reactivex.Flowable

class SexViewModel : BaseViewModel() {

    val adapter by lazy { BookSexAdapter() }


    /**
     * 随机获取小黄书列表
     */
    fun getBookSexList(pageNum: Int): Flowable<ChapterSexResult> {
        return mDataRepository.getBookSexList(pageNum).compose(io_main())
            .compose(mFragment?.bindToLifecycle())
    }


    /**
     * 本地随机获取小黄书列表
     */
    fun queryStoreSex(): Flowable<List<SexBooksEntity>> {
        return Flowable.just("")
            .map { mDataRepository.querySexBooksEntityAll() }
            .compose(io_main())
    }


    /**
     * 随机获取小黄书列表
     */
    fun getStoreSex(pageNum: Int, isClean:Boolean): Flowable<List<SexBooksEntity>> {
        return mDataRepository.getBookSexList(pageNum).map {
            if (isClean) {
                mDataRepository.cleanSexBooksEntity()
            }
            mDataRepository.insert(it.data)
            it.data
        }.compose(io_main())
    }

}