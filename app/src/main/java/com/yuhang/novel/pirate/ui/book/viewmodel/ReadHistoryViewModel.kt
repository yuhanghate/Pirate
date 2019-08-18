package com.yuhang.novel.pirate.ui.book.viewmodel

import android.text.TextUtils
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceBookInfoKSEntity
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
    fun queryReadHistoryList(pageNum: Int): Flowable<List<BookInfoKSEntity>> {
        return Flowable.just("")
            .map { mDataRepository.queryReadHistoryList(pageNum).filterNotNull().map { it }.toList() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取在线最近阅读
     */
    fun getReadHistoryListLocal(pageNum: Int): Flowable<List<BookInfoKSEntity>> {
        return mDataRepository.getReadHistoryList(pageNum).subscribeOn(Schedulers.io())
            .map {
                it.data.list.map {
                    it.niceBookInfoKSEntity()
                }.toList()
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getReadHistoryList(pageNum: Int): Flowable<List<BookInfoKSEntity>> {
        return if (TextUtils.isEmpty(PirateApp.getInstance().getToken())) {
            //从本地获取
            queryReadHistoryList(pageNum)

        } else {
            //从服务器获取
            getReadHistoryListLocal(pageNum)
        }

    }
}