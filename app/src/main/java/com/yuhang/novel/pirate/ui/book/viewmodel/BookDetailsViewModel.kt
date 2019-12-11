package com.yuhang.novel.pirate.ui.book.viewmodel

import android.annotation.SuppressLint
import android.text.TextUtils
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.database.entity.*
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookDetailsDataResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.concurrent.thread

class BookDetailsViewModel : BaseViewModel() {

    var obj: BookDetailsDataResult? = null

    var entity: BookInfoKSEntity? = null

    var chapterList: ArrayList<BookChapterKSEntity> = arrayListOf()

    /**
     * 是否收藏书箱
     */
    var isCollection = false


    /**
     * 作者所有作品
     */
    fun getAuthorBooksList(obj: BooksResult): Flowable<List<BooksResult>> {
        return mConvertRepository.getAuthorBooksList(obj)
            .compose(io_main())

    }

    /**
     * 获取详情信息
     */
    fun getBookDetails(obj: BooksResult): Flowable<BookInfoKSEntity> {
        return mConvertRepository.getDetailsInfo(obj)
            .compose(io_main())
    }

    /**
     * 增加收藏
     */
    fun insertCollection(obj: BooksResult): Flowable<Boolean> {
        return Flowable.just("")
            .map {
                mDataRepository.insertCollection(obj)
                return@map true
            }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }


    /**
     * 收藏到服务器
     */
    @SuppressLint("CheckResult")
    fun postCollection(obj: BooksResult) {
        if (TextUtils.isEmpty(PirateApp.getInstance().getToken()) || entity == null) return
        Flowable.just(obj.getBookid())

            .flatMap {
                entity?.let {
                    mDataRepository.addCollection(
                        bookid = it.bookid,
                        bookName = it.bookName,
                        author = it.author,
                        cover = it.cover,
                        description = it.description,
                        bookStatus = it.bookStatus,
                        classifyName = it.classifyName,
                        resouceType = obj.getType()
                    )
                }

            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(mActivity?.bindToLifecycle())
            .subscribe({
                Logger.i("")
            }, {
                Logger.i("")
            })
    }

    /**
     * 插入书箱信息
     */
    fun insertBookInfoEntity() {
        val book = entity ?: return
        thread {
            val bookInfo = mDataRepository.queryBookInfo(book.bookid)
            if (bookInfo == null) {
                //书籍信息插入本地
                mDataRepository.insertBookInfo(book)
            } else {
                //更新本地数据
                book.id = bookInfo.id
                mDataRepository.updateBookInfo(book)
            }
        }

    }

    /**
     * 查询书箱
     */
    fun queryCollection(bookid: String): BookCollectionKSEntity? {
        return mDataRepository.queryCollection(bookid)
    }

    /**
     * 查询书籍
     */
    fun queryCollectionAll(): List<BookCollectionKSEntity> {
        return mDataRepository.queryCollectionAll()
    }

    /**
     * 查询下载书籍
     */
    fun queryDownloadAll(): List<BookDownloadEntity> {
        return mDataRepository.queryDownloadBooks()
    }

    /**
     * 查询配置文件
     */
    fun isVip(): Boolean {
        //是否开启会员模式
        if (!mDataRepository.queryConfig().isOpenVip) {
            return true
        }
        //不是会员
        if (mDataRepository.getLastUser()?.isVip == null) {
            return false
        }
        return mDataRepository.getLastUser()?.isVip!!
    }

    /**
     * 删除收藏
     */
    fun deleteCollection(bookid: String): Flowable<Boolean> {
        return Flowable.just(bookid)
            .map {
                mDataRepository.deleteCollection(it)
                return@map true
            }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }


    /**
     * 章节列表
     */
    private fun getChapterListV2(obj: BooksResult): Flowable<List<BookChapterKSEntity>> {
        return mConvertRepository.getChapterList(obj)
    }

    /**
     * 从服务器更新书籍章节到本地
     */
    fun updateChapterToDB(bookid: BooksResult): Flowable<List<BookChapterKSEntity>> {
        return getChapterListV2(bookid)
            .map {
                chapterList.clear()
                chapterList.addAll(it)
                mDataRepository.deleteChapterList(it[0].bookId)
                mDataRepository.insertChapterList(it)
                return@map it
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 下载小说
     */
    fun downloadBook(obj:BooksResult) {
        mDataRepository.startWorker(obj)
    }
}