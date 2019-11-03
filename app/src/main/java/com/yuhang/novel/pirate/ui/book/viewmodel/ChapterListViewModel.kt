package com.yuhang.novel.pirate.ui.book.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookCollectionKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.book.adapter.DrawerlayoutLeftAdapter
import io.reactivex.Flowable

class ChapterListViewModel : BaseViewModel() {

    val adapter by lazy { DrawerlayoutLeftAdapter() }

    /**
     * 从本地查询章节目录
     */
    fun queryChapterList(obj: BooksResult): Flowable<List<BookChapterKSEntity>> {
        return Flowable.just(obj)
            .flatMap { mConvertRepository.getChapterList(it) }
            .compose(io_main())
    }

    /**
     * 从本地查询书籍信息
     */
    fun queryBookInfo(bookid: String): Flowable<BookInfoKSEntity?> {
        return Flowable.just(bookid)
            .map { mDataRepository.queryBookInfo(it) }
            .compose(io_main())
    }


    /**
     * 查找收藏
     */
    fun queryCollection(bookid: String):Flowable<BookCollectionKSEntity?> {
        return Flowable.just(bookid)
            .map { mDataRepository.queryCollection(it) }
            .compose(io_main())
    }

//    /**
//     * 获取章节内容
//     */
//    fun getChapterContent(bookid: String, chapterid: String): Flowable<ContentResult> {
//        return mDataRepository.getChapterContent(bookid, chapterid)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//    }
}