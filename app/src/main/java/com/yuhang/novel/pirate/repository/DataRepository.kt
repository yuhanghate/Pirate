package com.yuhang.novel.pirate.repository

import android.content.Context
import com.google.gson.Gson
import com.hunter.library.debug.HunterDebug
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.extension.niceDir
import com.yuhang.novel.pirate.repository.database.AppDatabase
import com.yuhang.novel.pirate.repository.database.entity.*
import com.yuhang.novel.pirate.repository.network.NetManager
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.*
import io.reactivex.Flowable
import java.text.SimpleDateFormat
import java.util.*

/**
 * 数据提供源
 */
class DataRepository(val context: Context) {
    /**
     * 网络
     */
    private val mNetManager by lazy { NetManager() }

    /**
     * 数据库
     */
    private val mDatabase by lazy { AppDatabase.getInstance(context.applicationContext) }

    /**
     * 提供网络操作
     */
    fun getNetApi() = mNetManager.getNetApi()

    /**
     * 看书神器
     */
    fun getKSNetApi() = mNetManager.getKanShuApi()

    /**
     * 提供数据库操作
     */
    fun getDatabase() = mDatabase

    /**
     * 看书神器站内搜索
     */
    fun searchBook(keyword: String): Flowable<BookSearchResult> {
        return getKSNetApi().searchBook(keyword)
    }

    /**
     * 男生 -> 分类
     */
    fun getBookCategory(): Flowable<BookCategoryResult> {
        return getKSNetApi().getBookCategory()
    }

    /**
     * 书本详情
     */
    @HunterDebug
    fun getBookDetails(id: Int): Flowable<BookDetailsResult> {
        return getKSNetApi().getBookDetails(dirId = niceDir(id), bookId = id)
    }

    /**
     * 书本章节目录
     */
    @HunterDebug
    fun getBookChapterList(id: Int): Flowable<ChapterListResult> {
        //返回的格式第二条数据是空的,单独进行处理
        return getKSNetApi().getBookChapterList(dirId = niceDir(id), bookId = id).map { it.replace("},]}}", "}]}}") }
                .flatMap { Flowable.just(Gson().fromJson(it, ChapterListResult::class.java)) }
    }

    /**
     * 获取章节内容
     */
    @HunterDebug
    fun getChapterContent(
            bookid: Int,
            chapterid: Int
    ): Flowable<ContentResult> {
        return getKSNetApi().getChapterContent(niceDir(bookid), bookid, chapterid)
    }

    /**
     * 下载章节内容
     */
    fun downloadChapterContent(
            bookid: Int,
            chapterid: Int
    ): Flowable<ContentResult> {
        return getKSNetApi().downloadChapterContent(niceDir(bookid), bookid, chapterid)
    }

    /**
     * 查询数据库书籍章节
     */
    fun queryChapterList(bookid: Int): Flowable<List<BookChapterKSEntity>> {
        return mDatabase.bookChapterKSDao.query(bookid)
    }

    /**
     * 查询数据库书籍章节
     */
    @HunterDebug
    fun queryChapterObjList(bookid: Int): List<BookChapterKSEntity> {
        return mDatabase.bookChapterKSDao.queryObj(bookid)
    }

    /**
     * 插入数据库书籍章节
     */
    @HunterDebug
    fun insertChapterList(list: List<BookChapterKSEntity>) {
        mDatabase.bookChapterKSDao.insert(list)
    }

    /**
     * 删除数据库书籍对应的章节列表
     */
    @HunterDebug
    fun deleteChapterList(bookid: Int) {
        mDatabase.bookChapterKSDao.delete(bookid)
    }

    /**
     * 从数据库查询当前id对应的书籍信息
     */
    @HunterDebug
    fun queryBookInfo(bookid: Int): BookInfoKSEntity? {
        return mDatabase.bookInfoKSDao.query(bookid)
    }

    /**
     * 插入数据库书籍信息
     */
    @HunterDebug
    fun insertBookInfo(obj: BookInfoKSEntity) {
        mDatabase.bookInfoKSDao.insert(obj)
    }

    /**
     * 更新数据库书籍信息
     */
    @HunterDebug
    fun updateBookInfo(obj: BookInfoKSEntity) {
        mDatabase.bookInfoKSDao.update(obj)
    }

    /**
     * 数据库查询章节内容
     */
    @HunterDebug
    fun queryBookContent(bookid: Int, chapterid: Int): BookContentKSEntity? {
        return mDatabase.bookContentKSDao.query(bookid, chapterid)
    }

    /**
     * 插入数据库章节内容
     */
    @HunterDebug
    @Synchronized
    fun insertBookContent(obj: BookContentKSEntity) {
        val contentObj = queryBookContentObj(obj.bookId, obj.chapterId)
        //防止重复插入
        if (contentObj == null) {
            mDatabase.bookContentKSDao.insert(obj)
        }

    }


    /**
     * 数据库查询章节内容
     */
    @HunterDebug
    fun queryBookContentObj(bookid: Int, chapterid: Int): BookContentKSEntity? {
        return mDatabase.bookContentKSDao.queryObj(bookid, chapterid)
    }


    /**
     * 查询搜索记录记录
     */
    @HunterDebug
    fun querySearchHistoryList(): List<SearchHistoryKSEntity?> {
        return mDatabase.searchHistoryKSDao.query()
    }

    /**
     * 插入历史记录
     */
    @HunterDebug
    fun insertSearchHistory(obj: SearchHistoryKSEntity) {
        return mDatabase.searchHistoryKSDao.insert(obj)
    }

    /**
     * 删除历史记录
     */
    @HunterDebug
    fun deleteSearchHistory(obj: SearchHistoryKSEntity) {
        return mDatabase.searchHistoryKSDao.delete(obj)
    }

    /**
     * 搜索历史记录
     */
    @HunterDebug
    fun querySearchHisotry(keyword: String): SearchHistoryKSEntity? {
        return mDatabase.searchHistoryKSDao.query(keyword)
    }

    /**
     * 收藏书籍
     */
    @HunterDebug
    fun insertCollection(bookid: Int) {

        mDatabase.bookCollectionKSDao.insert(
                BookCollectionKSEntity(
                        bookid = bookid,
                        time = System.currentTimeMillis() / 1000
                )
        )
    }

    /**
     * 查询书箱
     */
    @HunterDebug
    fun queryCollection(bookid: Int): BookCollectionKSEntity? {
        return mDatabase.bookCollectionKSDao.query(bookid)

    }

    /**
     * 查询所有收藏的书箱
     */
    @HunterDebug
    fun queryCollectionAll(): List<BookCollectionKSEntity?> {
        return mDatabase.bookCollectionKSDao.queryAll()
    }

    /**
     * 删除收藏
     */
    @HunterDebug
    fun deleteCollection(bookid: Int) {
        mDatabase.bookCollectionKSDao.delete(bookid)
    }

    /**
     * 更新收藏时间
     */
    fun updateCollectionTime(bookid: Int) {
        mDatabase.bookCollectionKSDao.updateTime(bookid, System.currentTimeMillis() / 1000)
    }

    /**
     * 更新置顶时间戳
     */
    @HunterDebug
    fun updateBookInfoStickTime(bookid: Int) {
        mDatabase.bookInfoKSDao.update(System.currentTimeMillis() / 1000, bookid)
        val query = mDatabase.bookInfoKSDao.query(bookid)
        Logger.i("")
    }

    /**
     * 查询所有收藏的书箱信息
     */
    @HunterDebug
    fun queryBookInfoAll(): List<BookInfoKSEntity?> {
        return mDatabase.bookInfoKSDao.queryCollectionAll()
    }


    /**
     * 排行榜
     */
    @HunterDebug
    fun getRankingList(gender: String, type: String, date: String, pageNum: Int): Flowable<RankingListResult> {
        return getKSNetApi().getRankingList(gender, type, date, pageNum)
    }

    /**
     * 获取最近浏览的小说
     */
    @HunterDebug
    fun queryReadHistoryList(pageNum: Int): List<BookInfoKSEntity?> {
        return getDatabase().bookInfoKSDao.queryReadHistoryList(pageNum)
    }

    /**
     * 更新最后一次打开的时间和内容角标
     */
    @HunterDebug
    fun updateLastOpenContent(chapterid: Int, lastContentPosition: Int) {
        getDatabase().bookContentKSDao.updateLastOpenContent(
                chapterid,
                Date(),
                lastContentPosition
        )
    }

    /**
     * 根据小说id查询最近阅读章节
     */
    @HunterDebug
    fun queryLastOpenChapter(bookid: Int): BookContentKSEntity? {
        return getDatabase().bookContentKSDao.queryLastOpenChapter(bookid)
    }

    /**
     * 获取第一章节id
     */
    @HunterDebug
    fun queryFirstChapterid(bookid: Int): Int {
        return getDatabase().bookChapterKSDao.queryFirstChapterid(bookid)
    }

    /**
     * 是否显示更新标签
     */
    @HunterDebug
    fun isShowUpdateLable(): Int {
        return getDatabase().bookContentKSDao.isShowUpdateLable()
    }

    /**
     * 查询书箱章节所有的更新时间
     */
    fun queryTimeList(bookid: Int): List<Long> {
        return getDatabase().bookContentKSDao.queryTimeList(bookid)
    }

    /**
     * 更新时间+章节id
     */
    fun queryTimeAndChapterid(bookid: Int): List<BookContentKSEntity?> {
        return getDatabase().bookContentKSDao.queryTimeAndChapterid(bookid)
    }
}