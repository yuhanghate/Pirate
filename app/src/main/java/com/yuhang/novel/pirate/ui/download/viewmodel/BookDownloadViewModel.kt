package com.yuhang.novel.pirate.ui.download.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity
import com.yuhang.novel.pirate.ui.download.adapter.BookDownloadAdapter
import io.reactivex.Flowable
import kotlin.concurrent.thread

class BookDownloadViewModel:BaseViewModel() {

    val adapter by lazy { BookDownloadAdapter() }


    /**
     * 获取下载缓存书籍
     */
    fun queryBookDownloadAll() :Flowable<List<BookDownloadEntity>>{
        return Flowable.just("")
            .map { mDataRepository.queryDownloadBooks() }
            .compose(io_main())
    }


    /**
     * 删除缓存记录
     */
    fun deleteDownload(bookid:String) {
        thread { mDataRepository.deleteDownload(bookid) }
    }
}