package com.yuhang.novel.pirate.ui.book.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookCollectionKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.book.adapter.DrawerlayoutLeftAdapter

class ChapterListViewModel : BaseViewModel() {

    val adapter by lazy { DrawerlayoutLeftAdapter() }

    /**
     * 从本地查询章节目录
     */
    suspend fun queryChapterList(obj: BooksResult): List<BookChapterKSEntity> {
        return mConvertRepository.getChapterList(obj)
    }

    /**
     * 从本地查询书籍信息
     */
    suspend fun queryBookInfo(bookid: String): BookInfoKSEntity? {
        return mDataRepository.queryBookInfo(bookid)
    }


    /**
     * 查找收藏
     */
    suspend fun queryCollection(bookid: String):BookCollectionKSEntity? {
        return mDataRepository.queryCollection(bookid)
    }

//    /**
//     * 获取章节内容
//     */
//    fun getChapterContent(bookid: String, chapterid: String): Flowable<ContentResult> {
//        return mDataRepository.getChapterContent(bookid, chapterid)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//    }
}