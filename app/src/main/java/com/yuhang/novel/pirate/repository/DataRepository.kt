package com.yuhang.novel.pirate.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.*
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.extension.niceBody
import com.yuhang.novel.pirate.extension.niceDir
import com.yuhang.novel.pirate.repository.database.AppDatabase
import com.yuhang.novel.pirate.repository.database.entity.*
import com.yuhang.novel.pirate.repository.network.NetManager
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.*
import com.yuhang.novel.pirate.repository.network.data.pirate.result.*
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.workmanager.NovelDownloadWorker
import io.reactivex.Flowable
import retrofit2.Call
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
    fun getBookDetails(id: Long): Flowable<BookDetailsResult> {
        return getKSNetApi().getBookDetails(dirId = niceDir(id), bookId = id)
    }

    /**
     * 书本章节目录
     */
    fun getBookChapterList(bookid: Long): Flowable<ChapterListResult> {
        //返回的格式第二条数据是空的,单独进行处理
        return getKSNetApi().getBookChapterList(dirId = niceDir(bookid), bookId = bookid)
                .map { it.replace("},]}}", "}]}}") }
                .flatMap { Flowable.just(Gson().fromJson(it, ChapterListResult::class.java)) }
    }

    /**
     * 获取章节内容
     */
    fun getChapterContent(
            bookid: Long,
            chapterid: Int
    ): Flowable<ContentResult> {
        return getKSNetApi().getChapterContent(niceDir(bookid), bookid, chapterid)
    }

    /**
     * 下载章节内容
     */
    fun downloadChapterContent(
            bookid: Long,
            chapterid: Int
    ): Flowable<ContentResult> {
        return getKSNetApi().downloadChapterContent(niceDir(bookid), bookid, chapterid)
    }

    /**
     * 下载章节内容
     */
    fun downloadNovel(bookid: Long, chapterid: Int): Call<ContentResult> {
        return getKSNetApi().downloadNovel(niceDir(bookid), bookid, chapterid)
    }


    /**
     * 查询数据库书籍章节
     */
    fun queryChapterObjList(bookid: Long): List<BookChapterKSEntity> {
        return mDatabase.bookChapterKSDao.queryObj(bookid)
    }

    /**
     * 插入数据库书籍章节
     */
    fun insertChapterList(list: List<BookChapterKSEntity>) {
        mDatabase.bookChapterKSDao.insert(list)
    }

    /**
     * 删除数据库书籍对应的章节列表
     */
    fun deleteChapterList(bookid: Long) {
        mDatabase.bookChapterKSDao.delete(bookid)
    }

    /**
     * 从数据库查询当前id对应的书籍信息
     */
    fun queryBookInfo(bookid: Long): BookInfoKSEntity? {
        return mDatabase.bookInfoKSDao.query(bookid)
    }

    /**
     * 插入数据库书籍信息
     */
    fun insertBookInfo(obj: BookInfoKSEntity) {
        mDatabase.bookInfoKSDao.insert(obj)
    }

    /**
     * 更新数据库书籍信息
     */
    fun updateBookInfo(obj: BookInfoKSEntity) {
        //更新标签
        updateLable(obj.bookid, obj.lastChapterId)
        mDatabase.bookInfoKSDao.update(obj)
    }

    /**
     * 数据库查询章节内容
     */
    fun queryBookContent(bookid: Long, chapterid: Int): BookContentKSEntity? {
        return mDatabase.bookContentKSDao.query(bookid, chapterid)
    }

    /**
     * 插入数据库章节内容
     */
    @Synchronized
    fun insertBookContent(obj: BookContentKSEntity) {
        val contentObj = queryBookContentObj(obj.bookId, obj.chapterId)
        //防止重复插入
        if (contentObj == null) {
            getDatabase().bookContentKSDao.insert(obj)
        } else {
            getDatabase().bookContentKSDao.delete(obj.bookId, obj.chapterId)
            //记录最后一次阅读角标
            obj.lastContentPosition = contentObj.lastContentPosition
            getDatabase().bookContentKSDao.insert(obj)
        }

    }


    /**
     * 数据库查询章节内容
     */
    fun queryBookContentObj(bookid: Long, chapterid: Int): BookContentKSEntity? {
        return mDatabase.bookContentKSDao.queryObj(bookid, chapterid)
    }


    /**
     * 查询搜索记录记录
     */
    fun querySearchHistoryList(): List<SearchHistoryKSEntity?> {
        return mDatabase.searchHistoryKSDao.query()
    }

    /**
     * 插入历史记录
     */
    fun insertSearchHistory(obj: SearchHistoryKSEntity) {
        return mDatabase.searchHistoryKSDao.insert(obj)
    }

    /**
     * 删除历史记录
     */
    fun deleteSearchHistory(obj: SearchHistoryKSEntity) {
        return mDatabase.searchHistoryKSDao.delete(obj)
    }

    /**
     * 清空历史记录
     */
    fun clearSearchHistory() {
        mDatabase.searchHistoryKSDao.clear()
    }

    /**
     * 搜索历史记录
     */
    fun querySearchHisotry(keyword: String): SearchHistoryKSEntity? {
        return mDatabase.searchHistoryKSDao.query(keyword)
    }

    /**
     * 根据关键字匹配模糊匹配取5个
     */
    fun queryListHisotry(keyword: String): List<SearchHistoryKSEntity?> {
        return mDatabase.searchHistoryKSDao.queryList(keyword)
    }

    /**
     * 收藏书籍
     */
    fun insertCollection(bookid: Long) {

        val collectionKSEntity = mDatabase.bookCollectionKSDao.query(bookid)
        if (collectionKSEntity == null) {
            mDatabase.bookCollectionKSDao.insert(
                    BookCollectionKSEntity(
                            bookid = bookid,
                            time = System.currentTimeMillis()
                    )
            )
        }

    }

    /**
     * 查询书箱
     */
    fun queryCollection(bookid: Long): BookCollectionKSEntity? {
        return mDatabase.bookCollectionKSDao.query(bookid)

    }

    /**
     * 查询所有收藏的书箱
     */
    fun queryCollectionAll(): List<BookCollectionKSEntity?> {
        return mDatabase.bookCollectionKSDao.queryAll()
    }

    /**
     * 删除收藏
     */
    fun deleteCollection(bookid: Long) {
        mDatabase.bookCollectionKSDao.delete(bookid)
    }

    /**
     * 更新收藏时间
     */
    fun updateCollectionTime(bookid: Long) {
        mDatabase.bookCollectionKSDao.updateTime(bookid, System.currentTimeMillis())
    }

    /**
     * 更新置顶时间戳
     */
    fun updateBookInfoStickTime(bookid: Long) {
        mDatabase.bookInfoKSDao.update(System.currentTimeMillis(), bookid)
        val query = mDatabase.bookInfoKSDao.query(bookid)
        Logger.i("")
    }


    /**
     * 排行榜
     */
    fun getRankingList(gender: String, type: String, date: String, pageNum: Int): Flowable<RankingListResult> {
        return getKSNetApi().getRankingList(gender, type, date, pageNum)
    }

    /**
     * 获取最近浏览的小说
     */
    fun queryReadHistoryList(pageNum: Int): List<BookInfoKSEntity?> {
        return getDatabase().bookInfoKSDao.queryReadHistoryList(pageNum)
    }

    /**
     * 更新最后一次打开的时间和内容角标
     */
    fun updateLastOpenContent(bookid: Long, chapterid: Int, lastContentPosition: Int) {
        getDatabase().bookContentKSDao.updateLastOpenContent(
                bookid,
                chapterid,
                System.currentTimeMillis(),
                lastContentPosition
        )
    }

    /**
     * 根据小说id查询最近阅读章节
     */
    fun queryLastOpenChapter(bookid: Long): BookContentKSEntity? {
//        val queryAll = getDatabase().bookReadHistoryDao.queryAll()
        return getDatabase().bookContentKSDao.queryLastOpenChapter(bookid)
    }

    /**
     * 获取第一章节id
     */
    fun queryFirstChapterid(bookid: Long): Int {
        return getDatabase().bookChapterKSDao.queryFirstChapterid(bookid)
    }

    /**
     * 更新标签数据
     */
    fun updateLable(bookid: Long, chapterid: Int) {
        val infoKSEntity = getDatabase().bookInfoKSDao.query(bookid) ?: return
//        val lastChapterid = getDatabase().bookChapterKSDao.queryLastChapterid(bookid)

        if (chapterid > infoKSEntity.lastChapterId) {
            PreferenceUtil.commitBoolean(bookid.toString(), true)
        }
    }

    /**
     * 清理标签
     */
    fun clearLable(bookid: Long) {
        PreferenceUtil.commitBoolean(bookid.toString(), false)
    }

    /**
     * 是否显示更新标签
     */
    @SuppressLint("SimpleDateFormat")
    fun isShowUpdateLable(bookid: Long): Boolean {
//        val queryLastTime = getDatabase().bookContentKSDao.queryLastTime(bookid)
//        val queryLastTime1 = getDatabase().bookInfoKSDao.queryLastTime(bookid)

//        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val content = format.format(Date(queryLastTime))
//        val info = format.format(Date(queryLastTime))

//        val infoKSEntity = getDatabase().bookInfoKSDao.query(bookid)?:return false
//        val lastChapterid = getDatabase().bookChapterKSDao.queryLastChapterid(bookid)


//        getDatabase().bookChapterKSDao.queryFirstChapterid()
//
//        Logger.t("lable").i("content:$content  info:$info")
//        return queryLastTime < queryLastTime1 && queryLastTime != 0.toLong()
//        return infoKSEntity.lastChapterId > lastChapterid

        return PreferenceUtil.getBoolean(bookid.toString(), false)
    }

//    /**
//     * 查询书箱章节所有的更新时间
//     */
//    fun queryTimeList(bookid: Long): List<Long> {
//        return getDatabase().bookContentKSDao.queryTimeList(bookid)
//    }

//    /**
//     * 更新时间+章节id
//     */
//    fun queryTimeAndChapterid(bookid: Long): List<BookContentKSEntity?> {
//        return getDatabase().bookContentKSDao.queryTimeAndChapterid(bookid)
//    }

    /**
     * 登陆
     */
    fun login(username: String, password: String): Flowable<UserResult> {
        val map = hashMapOf<String, String>("username" to username, "password" to password)
        return getNetApi().login(niceBody(map))
    }

    /**
     * 注册
     */
    fun register(username: String, password: String, email: String): Flowable<UserResult> {
        val map = hashMapOf<String, String>("username" to username, "password" to password, "email" to email)
        return getNetApi().register(niceBody(map))
    }

    /**
     * 添加收藏
     */
    fun addCollection(
            bookName: String, bookid: String, author: String, cover: String,
            description: String, bookStatus: String, classifyName: String, resouceType: String
    ): Flowable<StatusResult> {
        val map = hashMapOf<String, String>(
                "bookName" to bookName,
                "bookid" to bookid,
                "author" to author,
                "cover" to cover,
                "description" to description,
                "bookStatus" to bookStatus,
                "classifyName" to classifyName,
                "resouceType" to resouceType
        )

        return getNetApi().addCollection(niceBody(map))
    }

    /**
     * 获取收藏列表
     */
    fun getCollectionList(pageNum: Int): Flowable<CollectionResult> {
        return getNetApi().getCollectionList(pageNum, pageSize = 30)
    }

    /**
     * 检测版本号
     */
    fun checkVersion(versionName: String): Flowable<VersionResult> {
        return getNetApi().checkVersion(versionName)
    }

    /**
     * 查找最后一次登陆帐号
     */
    fun getLastUser(): UserEntity? {
        return getDatabase().userDao.queryUser()
    }

    /**
     * 插入一条帐号
     */
    fun insert(userEntity: UserEntity) {
        getDatabase().userDao.insert(userEntity)
    }

    /**
     * 修改最后登陆时间
     */
    fun updateUserLastTime(uid: String) {
        getDatabase().userDao.updateLastTime(uid, Date())
    }

    /**
     * 删除用户
     */
    fun deleteUser(userEntity: UserEntity) {
        getDatabase().userDao.delete(userEntity)
    }

    /**
     * 根据uid查询
     */
    fun queryUser(username: String): UserEntity? {
        return getDatabase().userDao.queryUser(username)
    }

    /**
     * 查询所有收藏书本信息
     */
    fun queryBookInfoCollectionAll(): List<BookInfoKSEntity?> {
        return getDatabase().bookInfoKSDao.queryCollectionAll()
    }

    /**
     * 查询排行榜数据
     */
    fun queryRankingListAll(): List<RankingListEntity?> {
        return getDatabase().rankingListDao.queryAll()
    }

    /**
     * 删除排行榜所有数据
     */
    fun deleteRankingListAll() {
        return getDatabase().rankingListDao.deleteAll()
    }

    /**
     * 批量插入排行榜数据
     */
    fun insertRankingList(list: List<RankingListEntity>) {
        val listAll = queryRankingListAll()
        if (listAll.isEmpty()) {
            getDatabase().rankingListDao.insert(list)
        } else {
            //本地有保存就删除, 只缓存一页数据
            deleteRankingListAll()
            getDatabase().rankingListDao.insert(list)
        }
    }

    /**
     * 删除收藏
     */
    fun deleteNetCollect(bookid: Long, resouceType: String): Flowable<StatusResult> {
        return getNetApi().deleteCollectList(bookid, resouceType)
    }

    /**
     * 清空帐号
     */
    fun clearUsers() {
        getDatabase().bookReadHistoryDao.clear()
        getDatabase().userDao.clear()
        getDatabase().bookCollectionKSDao.clear()
        getDatabase().bookInfoKSDao.clear()
        getDatabase().bookChapterKSDao.clear()
        getDatabase().bookContentKSDao.clear()
    }

    /**
     * 清除书箱所有数据
     */
    fun clearBookInfo() {
        getDatabase().bookCollectionKSDao.clear()
        getDatabase().bookInfoKSDao.clear()
        getDatabase().bookChapterKSDao.clear()
        getDatabase().bookContentKSDao.clear()
    }

    /**
     * 最近浏览
     */
    fun getReadHistoryList(pageNum: Int): Flowable<ReadHistoryResult> {
        return getNetApi().getReadHistoryList(pageNum = pageNum, pageSize = 100)
    }

    /**
     * 获取收藏列表的阅读记录
     */
    fun getReadHistoryCollectionsList(pageNum: Int): Flowable<ReadHistoryResult> {
        return getNetApi().getReadHistoryCollectionsList(pageNum = pageNum, pageSize = 100)
    }

    /**
     * 更新阅读记录
     */
    fun updateReadHistory(
            bookName: String,
            bookid: String,
            chapterid: String,
            chapterName: String,
            author: String,
            cover: String,
            description: String,
            resouceType: String,
            content: String
    ): Flowable<StatusResult> {

        val map = hashMapOf<String, Any>(
                "bookName" to bookName, "bookid" to bookid, "chapterid" to chapterid,
                "chapterName" to chapterName, "author" to author, "cover" to cover, "description" to description,
                "resouceType" to resouceType, "content" to content
        )
        return getNetApi().updateReadHistory(niceBody(map))
    }

    /**
     * 更新最后一次阅读记录到本地
     */
    fun updateLocalREadHistory(bookid: Long, chapterid: Int, lastReadTime: Long = System.currentTimeMillis()) {

//        val queryAll = getDatabase().bookReadHistoryDao.queryAll()
//        queryAll.forEach {
//            Logger.t("read_history").i("isChapter = ${it.chapterid == chapterid}  chapterid = ${it.chapterid} time=${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(it.lastReadTime*1000))}")
//        }
        val entity = getDatabase().bookReadHistoryDao.queryBookReadHistoryEntity(bookid, chapterid)
        if (entity == null) {
            getDatabase().bookReadHistoryDao.insert(BookReadHistoryEntity().apply {
                this.bookid = bookid.toInt()
                this.chapterid = chapterid
                this.lastReadTime = lastReadTime
            })
        } else {
            entity.lastReadTime = lastReadTime
            getDatabase().bookReadHistoryDao.update(entity)
        }
    }

    /**
     * 获取最近阅读记录
     */
    fun queryBookReadHistoryEntity(bookid: Long): BookReadHistoryEntity? {
        return getDatabase().bookReadHistoryDao.queryLastChanpterEntity(bookid)
    }

    /**
     * 开始下载任务
     */
    fun startWorker(bookid: Long, chapterid: List<Int>) {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)//指定设备电池是否不应低于临界阈值
                .build()

        val data = Data.Builder().putLong(
                NovelDownloadWorker.BOOKID,
                bookid
        ).putIntArray(NovelDownloadWorker.CHANPTER_ID, chapterid.toIntArray()).build()


        val request = OneTimeWorkRequest.Builder(NovelDownloadWorker::class.java)
                .setConstraints(constraints)
                .setInputData(data)
                .build()
        val enqueue = WorkManager.getInstance().enqueue(request)
        enqueue.state
    }

    /**
     * 发送邮箱验证码
     */
    fun getMailCode(mail: String): Flowable<EmailCodeResult> {
        return getNetApi().getMailCode(mail)
    }

    /**
     * 检测用户邮箱是否存在
     */
    fun checkEmailEmpty(email: String): Flowable<StatusResult> {
        return getNetApi().checkEmailEmpty(email)
    }

    /**
     * 检测邮箱验证码
     */
    fun checkEmailCode(email: String, newCode: String, oldCode: String): Flowable<StatusResult> {
        val map = hashMapOf<String, String>("email" to email, "newCode" to newCode,
                "oldCode" to oldCode)
        return getNetApi().checkEmailCode(niceBody(map))
    }

    /**
     * 修改密码
     */
    fun updatePassword(email: String, username: String, password: String, againPassword: String): Flowable<UserResult> {
        val map = hashMapOf<String, String>("email" to email, "username" to username,
                "password" to password, "againPassword" to againPassword)
        return getNetApi().updatePassword(niceBody(map))
    }
}