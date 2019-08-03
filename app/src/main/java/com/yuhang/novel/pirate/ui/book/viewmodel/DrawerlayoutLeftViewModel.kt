package com.yuhang.novel.pirate.ui.book.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.ui.book.adapter.DrawerlayoutLeftAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DrawerlayoutLeftViewModel: BaseViewModel() {

    val adapter by lazy { DrawerlayoutLeftAdapter() }

    /**
     * 从本地查询章节目录
     */
    fun queryChapterList(bookid: Int): Flowable<List<BookChapterKSEntity>> {
        return Flowable.just(bookid)
                .map { mDataRepository.queryChapterObjList(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 从本地查询书籍信息
     */
    fun queryBookInfo(bookid: Int): Flowable<BookInfoKSEntity?> {
        return Flowable.just(bookid)
            .map { mDataRepository.queryBookInfo(it) }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}