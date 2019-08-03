package com.yuhang.novel.pirate.ui.book.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.ui.book.adapter.ReadHistoryAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ReadHistoryViewModel : BaseViewModel() {

    val adapter by lazy { ReadHistoryAdapter() }

    /**
     * 获取最近浏览的小说
     */
    fun queryReadHistoryList(pageNum: Int): Flowable<List<BookInfoKSEntity?>> {
        return Flowable.just("")
                .map { mDataRepository.queryReadHistoryList(pageNum) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}