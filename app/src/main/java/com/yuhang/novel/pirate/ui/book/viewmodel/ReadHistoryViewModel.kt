package com.yuhang.novel.pirate.ui.book.viewmodel

import android.text.TextUtils
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceBookInfoKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.ui.book.adapter.ReadHistoryAdapter

class ReadHistoryViewModel : BaseViewModel() {

    val adapter by lazy { ReadHistoryAdapter() }

    /**
     * 获取最近浏览的小说
     */
    private suspend fun queryReadHistoryList(pageNum: Int): List<BookInfoKSEntity> {
        return mDataRepository.queryReadHistoryList(pageNum).filterNotNull().map { it }.toList()
    }

    /**
     * 获取在线最近阅读
     */
    private suspend fun getReadHistoryListLocal(pageNum: Int): List<BookInfoKSEntity> {
        val history = mDataRepository.getReadHistoryList(pageNum)
        return history.data.list.map { it.niceBookInfoKSEntity() }.toList()
    }

    suspend fun getReadHistoryList(pageNum: Int): List<BookInfoKSEntity> {
        return if (TextUtils.isEmpty(PirateApp.getInstance().getToken())) {
            //从本地获取
            queryReadHistoryList(pageNum)

        } else {
            //从服务器获取
            getReadHistoryListLocal(pageNum)
        }

    }
}