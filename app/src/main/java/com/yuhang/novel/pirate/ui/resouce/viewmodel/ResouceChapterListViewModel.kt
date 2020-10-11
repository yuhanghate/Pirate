package com.yuhang.novel.pirate.ui.resouce.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookResouceTypeKDEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.book.adapter.DrawerlayoutLeftAdapter

class ResouceChapterListViewModel : BaseViewModel() {

    val adapter by lazy { DrawerlayoutLeftAdapter() }

    /**
     * 章节列表
     */
    val chapterList: ArrayList<BookChapterKSEntity> by lazy { arrayListOf<BookChapterKSEntity>() }

    /**
     * 第三方源目录列表
     */
    suspend fun getChapterList(tocId: String, bookid: String): List<BookChapterKSEntity> {
        return mConvertRepository.getResouceChapterList(tocId, bookid)
    }

    /**
     * 插入章节列表
     */
    suspend fun insertChapterList(obj: BookResouceTypeKDEntity, result: BooksResult) {

        mDataRepository.insertKuaiDuResouce(obj)
        mDataRepository.deleteBookHistory(result.getBookid())
        mDataRepository.deleteBookContent(result.getBookid())
        mDataRepository.deleteChapterList(result.getBookid())
        mDataRepository.insertChapterList(chapterList)
        val oldInfo = mDataRepository.queryBookInfo(result.getBookid())
        val oldColl = mDataRepository.queryCollection(result.getBookid())
        if (oldInfo != null) {
            oldInfo.bookid = obj.bookid
            oldInfo.resouce = obj.resouce
            mDataRepository.updateBookInfo(oldInfo)
        }

        if (oldColl != null) {
            oldColl.bookid = obj.bookid
            oldColl.resouce = obj.resouce
            mDataRepository.updateCollection(oldColl)
        }
    }

    /**
     * 获取最新阅读的角标
     */
    fun getChapterIndex(index: Int): Int {
        return if (chapterList.size > index) {
            return index
        } else {
            chapterList.size - 1
        }
    }
}