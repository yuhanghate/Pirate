package com.yuhang.novel.pirate.ui.download.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceBookResult
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.download.adapter.BookDownloadAdapter
import kotlinx.coroutines.launch
import java.util.*

class BookDownloadViewModel : BaseViewModel() {

    val adapter by lazy { BookDownloadAdapter() }


    /**
     * 获取下载缓存书籍
     */
    suspend fun queryBookDownloadAll(): List<BookDownloadEntity> {
        return mDataRepository.queryDownloadBooks()
    }


    /**
     * 删除缓存记录
     */
    fun deleteDownload(bookid: String) {
        viewModelScope.launch {
            mDataRepository.deleteBookContent(bookid)
            mDataRepository.deleteDownload(bookid)
        }
    }

    /**
     * 开始任务
     */
    fun startTask(obj:BookDownloadEntity) {
        val uuid = mDataRepository.startWorker(obj.niceBookResult())
        obj.uuid = uuid.toString()
    }


    /**
     * 取消任务
     */
    fun cancelTask(obj:BookDownloadEntity) {
        WorkManager.getInstance().cancelWorkById(UUID.fromString(obj.uuid))
    }

    /**
     * 下载小说
     */
    fun downloadBook(obj: BooksResult, chapterid: String = "") {
        mDataRepository.startWorker(obj.apply { this.lastChapterId = chapterid })
    }

}