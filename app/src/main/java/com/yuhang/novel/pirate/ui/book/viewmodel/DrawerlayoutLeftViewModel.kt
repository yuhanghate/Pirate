package com.yuhang.novel.pirate.ui.book.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.ui.book.adapter.DrawerlayoutLeftAdapter

class DrawerlayoutLeftViewModel : BaseViewModel() {

    val adapter by lazy { DrawerlayoutLeftAdapter() }

    /**
     * 从本地查询章节目录
     */
//    fun queryChapterList(bookid: String): Flowable<List<BookChapterKSEntity>> {
//        return Flowable.just(bookid)
//                .map { mConvertRepository.getChapterList(it) }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }

    /**
     * 从本地查询书籍信息
     */
    suspend fun queryBookInfo(bookid: String): BookInfoKSEntity? {
        return mDataRepository.queryBookInfo(bookid)
    }

    /**
     * 获取最后打开的章节
     */
    suspend fun queryLastChapter(bookid: String): BookContentKSEntity? {
        return mDataRepository.queryLastOpenChapter(bookid)
    }
}