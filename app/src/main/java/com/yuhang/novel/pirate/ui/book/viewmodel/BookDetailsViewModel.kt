package com.yuhang.novel.pirate.ui.book.viewmodel

import android.annotation.SuppressLint
import android.text.TextUtils
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookCollectionKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookDetailsDataResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult

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
    suspend fun getAuthorBooksList(obj: BooksResult): List<BooksResult> {
        return mConvertRepository.getAuthorBooksList(obj)

    }

    /**
     * 获取详情信息
     */
    suspend fun getBookDetails(obj: BooksResult): BookInfoKSEntity {
        return mConvertRepository.getDetailsInfo(obj)
    }

    /**
     * 增加收藏
     */
    suspend fun insertCollection(obj: BooksResult): Boolean {
        mDataRepository.insertCollection(obj)
        return true
    }


    /**
     * 收藏到服务器
     */
    @SuppressLint("CheckResult")
    suspend fun postCollection(obj: BooksResult) {
        if (TextUtils.isEmpty(PirateApp.getInstance().getToken()) || entity == null) return
        entity?.let {
            mDataRepository.addCollection(bookid = it.bookid,bookName = it.bookName,
                author = it.author,cover = it.cover,description = it.description,
                bookStatus = it.bookStatus,classifyName = it.classifyName,resouceType = obj.getType()
            )
        }
    }

    /**
     * 插入书箱信息
     */
    suspend fun insertBookInfoEntity() {
        val book = entity ?: return
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

    /**
     * 查询书箱
     */
    suspend fun queryCollection(bookid: String): BookCollectionKSEntity? {
        return mDataRepository.queryCollection(bookid)
    }

    /**
     * 查询书籍
     */
    suspend fun queryCollectionAll(): List<BookCollectionKSEntity> {
        return mDataRepository.queryCollectionAll()
    }

    /**
     * 查询下载书籍
     */
    suspend fun queryDownloadAll(): List<BookDownloadEntity> {
        return mDataRepository.queryDownloadBooks()
    }

    /**
     * 查询配置文件
     */
    suspend fun isVip(): Boolean {
        //是否开启会员模式
        mDataRepository.queryConfig()?.let {
            return it.isOpenVip
        }
        return mDataRepository.getLastUser()?.isVip!!
    }

    /**
     * 删除收藏
     */
    suspend fun deleteCollection(bookid: String): Boolean {
        mDataRepository.deleteCollection(bookid)
        return true
    }


    /**
     * 章节列表
     */
    private suspend fun getChapterListV2(obj: BooksResult): List<BookChapterKSEntity> {
        return mConvertRepository.getChapterList(obj)
    }

    /**
     * 从服务器更新书籍章节到本地
     */
    suspend fun updateChapterToDB(bookid: BooksResult): List<BookChapterKSEntity> {
        val list = getChapterListV2(bookid)
        chapterList.clear()
        chapterList.addAll(list)
        mDataRepository.deleteChapterList(bookid.getBookid())
        mDataRepository.insertChapterList(list)
        return list
    }

    /**
     * 下载小说
     */
    fun downloadBook(obj:BooksResult) {
        mDataRepository.startWorker(obj)
    }
}