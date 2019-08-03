package com.yuhang.novel.pirate.ui.book.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceBookChapterKSEntity
import com.yuhang.novel.pirate.extension.niceBookInfoKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookCollectionKSEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookDetailsDataResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ChapterListResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ContentResult
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.concurrent.thread

class BookDetailsViewModel : BaseViewModel() {

    var obj: BookDetailsDataResult? = null


    /**
     * 是否收藏书箱
     */
    var isCollection = false
    /**
     * 获取小说详情
     */
    fun getBookDetails(bookid: Int): Flowable<BookDetailsDataResult> {
        return mDataRepository.getBookDetails(bookid)
                .filter { it.status == 1 }
                .map { it.data }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 增加收藏
     */
    fun insertCollection(bookid: Int): Flowable<Boolean> {
        return Flowable.just(bookid)
                .map {
                    mDataRepository.insertCollection(it)
                    return@map true
                }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    /**
     * 插入书箱信息
     */
    fun insertBookInfoEntity() {
        val book = obj?:return
        thread {
            val bookInfo = mDataRepository.queryBookInfo(book.Id)
            if (bookInfo == null) {
                //书籍信息插入本地
                mDataRepository.insertBookInfo(book.niceBookInfoKSEntity())
            } else {
                //更新本地数据
                val infoKSEntity = book.niceBookInfoKSEntity()
                infoKSEntity.id = bookInfo.id
                mDataRepository.updateBookInfo(infoKSEntity)
            }
        }

    }

    /**
     * 查询书箱
     */
    fun queryCollection(bookid: Int): BookCollectionKSEntity? {
        return mDataRepository.queryCollection(bookid)

    }

    /**
     * 删除收藏
     */
    fun deleteCollection(bookid: Int):Flowable<Boolean> {
        return Flowable.just(bookid)
                .map {
                    mDataRepository.deleteCollection(it)
                    return@map true
                }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 插入章节列表到本地
     */
    fun insertChapterList(list: List<BookChapterKSEntity>) {
        mDataRepository.insertChapterList(list)
    }

    /**
     * 删除本地对应的书籍章节
     */
    fun deleteChapterList(bookid: Int) {
        mDataRepository.deleteChapterList(bookid)
    }

    /**
     * 获取章节列表
     */
    fun getChapterList(bookid: Int): Flowable<ChapterListResult> {
        return mDataRepository.getBookChapterList(bookid)
    }

    /**
     * 从服务器更新书籍章节到本地
     */
    fun updateChapterToDB(bookid:Int): Flowable<List<BookChapterKSEntity>> {
        return getChapterList(bookid)
                .filter { it.status == 1 }
                .map {
                    deleteChapterList(it.data.id)
                    val list = it.data.niceBookChapterKSEntity()
                    insertChapterList(list)
                    return@map list
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    }
}