package com.yuhang.novel.pirate.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.*
import com.google.gson.Gson
import com.hunter.library.debug.HunterDebug
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.extension.niceBody
import com.yuhang.novel.pirate.extension.niceDir
import com.yuhang.novel.pirate.repository.database.AppDatabase
import com.yuhang.novel.pirate.repository.database.entity.*
import com.yuhang.novel.pirate.repository.network.NetManager
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.*
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.BookCategoryDataResult
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.CategoryDetailResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.*
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.workmanager.NovelDownloadWorker
import io.reactivex.Flowable
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
     * 快读
     */
    fun getKuaiDuApi() = mNetManager.getKuaiDuApi()

    /**
     * okhttp
     */
    fun getOkhttpClick() = mNetManager.getOkHttpClient()

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
    fun getBookDetails(id: String): Flowable<BookDetailsResult> {
        return getKSNetApi().getBookDetails(dirId = niceDir(id.toLong()), bookId = id.toLong())
    }

    /**
     * 书本章节目录
     */
    fun getBookChapterList(bookid: String): Flowable<ChapterListResult> {
        //返回的格式第二条数据是空的,单独进行处理
        return getKSNetApi().getBookChapterList(
            dirId = niceDir(bookid.toLong()),
            bookId = bookid.toLong()
        )
            .map { it.replace("},]}}", "}]}}") }
            .flatMap { Flowable.just(Gson().fromJson(it, ChapterListResult::class.java)) }
    }


    /**
     * 查询数据库书籍章节
     */
    fun queryChapterObjList(bookid: String): List<BookChapterKSEntity> {
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
    fun deleteChapterList(bookid: String) {
        mDatabase.bookChapterKSDao.delete(bookid)
    }

    /**
     * 更新章节内容
     */
    fun updateChapter(obj: BookChapterKSEntity) {
        mDatabase.bookChapterKSDao.update(obj)
    }

    /**
     * 从数据库查询当前id对应的书籍信息
     */
    fun queryBookInfo(bookid: String): BookInfoKSEntity? {
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
        updateLable(obj.bookid, obj.lastChapterName)
        mDatabase.bookInfoKSDao.update(obj)
    }

    /**
     * 数据库查询章节内容
     */
    fun queryBookContent(bookid: String, chapterid: String): BookContentKSEntity? {
        return mDatabase.bookContentKSDao.query(bookid, chapterid)
    }

    /**
     * 插入数据库章节内容
     */
    @Synchronized
    fun insertBookContent(obj: BookContentKSEntity): BookContentKSEntity {
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
        return obj
    }


    /**
     * 数据库查询章节内容
     */
    fun queryBookContentObj(bookid: String, chapterid: String): BookContentKSEntity? {
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
     * 搜索历史记录
     */
    fun querySearchHisotry(keyword: String): SearchHistoryKSEntity? {
        return mDatabase.searchHistoryKSDao.query(keyword)
    }

    /**
     * 收藏书籍
     */
    fun insertCollection(obj: BooksResult) {

        val collectionKSEntity = mDatabase.bookCollectionKSDao.query(obj.getBookid())
        if (collectionKSEntity == null) {
            mDatabase.bookCollectionKSDao.insert(
                BookCollectionKSEntity(
                    bookid = obj.getBookid(),
                    resouce = obj.getType(),
                    time = System.currentTimeMillis()
                )
            )
        }

    }

    /**
     * 查询书箱
     */
    fun queryCollection(bookid: String): BookCollectionKSEntity? {
        return mDatabase.bookCollectionKSDao.query(bookid)
    }

    /**
     * 查询所有收藏书籍
     */
    fun queryCollectionAll(): List<BookCollectionKSEntity> {
        return mDatabase.bookCollectionKSDao.queryAll()
    }

    /**
     * 删除收藏
     */
    fun deleteCollection(bookid: String) {
        mDatabase.bookCollectionKSDao.delete(bookid)
    }

    /**
     * 更新置顶时间戳
     */
    fun updateBookInfoStickTime(bookid: String) {
        mDatabase.bookInfoKSDao.update(System.currentTimeMillis(), bookid)
        mDatabase.bookInfoKSDao.query(bookid)
        Logger.i("")
    }

    /**
     * 取消置顶
     */
    fun updateBookInfoClearStickTime(bookid: String) {
        mDatabase.bookInfoKSDao.update(0, bookid)
        mDatabase.bookInfoKSDao.query(bookid)
    }


    /**
     * 排行榜
     */
    fun getRankingList(
        gender: String,
        type: String,
        date: String,
        pageNum: Int
    ): Flowable<RankingListResult> {
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
    fun updateLastOpenContent(bookid: String, chapterid: String, lastContentPosition: Int) {
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
    fun queryLastOpenChapter(bookid: String): BookContentKSEntity? {
        return getDatabase().bookContentKSDao.queryLastOpenChapter(bookid)
    }

    /**
     * 更新标签数据
     */
    fun updateLable(bookid: String, chapterName: String) {
        val infoKSEntity = getDatabase().bookInfoKSDao.query(bookid) ?: return

        if (chapterName != infoKSEntity.lastChapterName) {
            PreferenceUtil.commitBoolean(bookid, true)
        }
    }

    /**
     * 清理标签
     */
    fun clearLable(bookid: String) {
        PreferenceUtil.commitBoolean(bookid, false)
    }

    /**
     * 是否显示更新标签
     */
    @SuppressLint("SimpleDateFormat")
    fun isShowUpdateLable(bookid: String): Boolean {
        return PreferenceUtil.getBoolean(bookid, false)
    }


    /**
     * 登录
     */
    fun login(username: String, password: String): Flowable<UserResult> {
        val map = hashMapOf<String, String>("username" to username, "password" to password)
        return getNetApi().login(niceBody(map))
    }

    /**
     * 注册
     */
    fun register(username: String, password: String, email: String): Flowable<UserResult> {
        val map = hashMapOf<String, String>(
            "username" to username,
            "password" to password,
            "email" to email
        )
        return getNetApi().register(niceBody(map))
    }

    /**
     * 添加收藏
     */
    fun addCollection(
        bookName: String, bookid: String, author: String, cover: String,
        description: String, bookStatus: String, classifyName: String, resouceType: String
    ): Flowable<StatusResult> {
        return Flowable.just("")
            .map {
                val kdEntity = getResouceTypeKd(bookid)
                val tocId = kdEntity?.tocId ?: ""
                val tocName = kdEntity?.tocId ?: ""
                val map = hashMapOf(
                    "bookName" to bookName,
                    "bookid" to bookid,
                    "author" to author,
                    "cover" to cover,
                    "description" to description,
                    "bookStatus" to bookStatus,
                    "classifyName" to classifyName,
                    "resouceType" to resouceType,
                    "tocId" to tocId,
                    "tocName" to tocName
                )
                return@map map
            }
            .flatMap { getNetApi().addCollection(niceBody(it)) }
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
     * 查找最后一次登录帐号
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
    fun deleteNetCollect(bookid: String, resouceType: String): Flowable<StatusResult> {
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
        getDatabase().searchHistoryKSDao.clear()
        getDatabase().bookResouceTypeKDDao.clear()
        getDatabase().bookDownloadDao.clear()

    }

    /**
     * 清除书箱所有数据
     */
    fun clearBookInfo() {
        getDatabase().bookCollectionKSDao.clear()
        getDatabase().bookInfoKSDao.clear()
        getDatabase().bookChapterKSDao.clear()
        getDatabase().bookContentKSDao.clear()
        getDatabase().bookResouceTypeKDDao.clear()
        getDatabase().bookReadHistoryDao.clear()
        getDatabase().bookDownloadDao.clear()
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
     * 获取指定小说阅读记录
     */
    fun getReadHistoryCollectionsList(bookid: String): Flowable<ReadHistoryBookResult> {
        return getNetApi().getReadHistoryCollectionsList(bookid = bookid)
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
        return Flowable.just("")
            .map {
                val kdEntity = getResouceTypeKd(bookid)
                val tocId = kdEntity?.tocId ?: ""
                val tocName = kdEntity?.typeName ?: ""
                val map = hashMapOf<String, Any>(
                    "bookName" to bookName,
                    "bookid" to bookid,
                    "chapterid" to chapterid,
                    "chapterName" to chapterName,
                    "author" to author,
                    "cover" to cover,
                    "description" to description,
                    "resouceType" to resouceType,
                    "content" to content,
                    "tocId" to tocId,
                    "tocName" to tocName
                )
                return@map map
            }
            .flatMap {
                //没登陆返回空
                if (PirateApp.getInstance().getToken().isEmpty()) {
                    return@flatMap Flowable.empty<StatusResult>()
                }
                getNetApi().updateReadHistory(niceBody(it))
            }

    }

    /**
     * 查看章节对应记录
     */
    fun queryBookReadHistoryEntity(bookid: String, chapterid: String): BookReadHistoryEntity? {
        return getDatabase().bookReadHistoryDao.queryBookReadHistoryEntity(bookid, chapterid)
    }

    /**
     * 更新最后一次阅读记录到本地
     */
    fun updateLocalREadHistory(
        bookid: String,
        chapterid: String,
        lastReadTime: Long = System.currentTimeMillis()
    ) {

        val entity = getDatabase().bookReadHistoryDao.queryBookReadHistoryEntity(bookid, chapterid)
        if (entity == null) {
            getDatabase().bookReadHistoryDao.insert(BookReadHistoryEntity().apply {
                this.bookid = bookid
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
    fun queryBookReadHistoryEntity(bookid: String): BookReadHistoryEntity? {
        return getDatabase().bookReadHistoryDao.queryLastChanpterEntity(bookid)
    }

    /**
     * 开始下载任务
     */
    fun startWorker(obj: BooksResult): UUID {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)//指定设备电池是否不应低于临界阈值
            .build()

        val data = Data.Builder().putString(NovelDownloadWorker.BOOKS_RESULT, obj.toJson())
            .build()


        val request = OneTimeWorkRequest.Builder(NovelDownloadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()


        WorkManager.getInstance().enqueue(request)
        return request.id
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
        val map = hashMapOf<String, String>(
            "email" to email, "newCode" to newCode,
            "oldCode" to oldCode
        )
        return getNetApi().checkEmailCode(niceBody(map))
    }

    /**
     * 修改密码
     */
    fun updatePassword(
        email: String,
        username: String,
        password: String,
        againPassword: String
    ): Flowable<UserResult> {
        val map = hashMapOf<String, String>(
            "email" to email, "username" to username,
            "password" to password, "againPassword" to againPassword
        )
        return getNetApi().updatePassword(niceBody(map))
    }

    /**
     * 书源列表
     */
    fun getResouceList(pageNum: Int): Flowable<BookResouceResult> {
        val map = hashMapOf<String, Int>("type" to 0, "pageNum" to pageNum, "pageSize" to 100)
        return getNetApi().getResouceList(niceBody(map))
    }


    /**
     * 书名/作者搜索
     */
    fun getBookSearchList(keyword: String): Flowable<SearchSuggestResult> {
        val map = hashMapOf<String, String>("keyword" to keyword)
        return getNetApi().getBookSearchList(niceBody(map))
    }

    /**
     * 作者所有作品
     */
    fun getAuthorBooksList(author: String): Flowable<AuthorBooksResult> {
        val map = hashMapOf<String, String>("author" to author)
        return getNetApi().getAuthorBooksList(niceBody(map))
    }

    /**
     * 保存公告信息
     */
    fun insert(obj: PushMessageEntity) {
        getDatabase().pushMessageDao.insert(obj)
    }

    /**
     * 查询最后一次公告信息
     */
    fun queryNoteEntity(): PushMessageEntity? {
        return getDatabase().pushMessageDao.queryNote()
    }

    /**
     * 删除推送消息
     */
    fun delete(obj: PushMessageEntity) {
        getDatabase().pushMessageDao.delete(obj)
    }

    /**
     * 修改书籍详情修改时间
     */
    fun updateBookInfoLastReadTime(bookid: String) {
        getDatabase().bookInfoKSDao.updateLastReadTime(System.currentTimeMillis(), bookid)
    }

    /**
     * 快读源插入
     */
    fun insertKuaiDuResouce(obj: BookResouceTypeKDEntity) {
        val typeKDEntity = getDatabase().bookResouceTypeKDDao.query(obj.bookid)
        if (typeKDEntity == null) {
            getDatabase().bookResouceTypeKDDao.insert(obj)
            return
        }

        obj.id = typeKDEntity.id
        getDatabase().bookResouceTypeKDDao.update(obj)
    }

    /**
     * 删除指定小说记录
     */
    fun deleteBookHistory(bookid: String) {
        getDatabase().bookReadHistoryDao.clear(bookid)
    }

    /**
     * 删除小说内容
     */
    fun deleteBookContent(bookid: String) {
        getDatabase().bookContentKSDao.delete(bookid)
    }

    /**
     * 更改收藏
     */
    fun updateCollection(obj: BookCollectionKSEntity) {
        getDatabase().bookCollectionKSDao.update(obj)
    }

    /**
     * 获取快读渠道源
     */
    fun getResouceTypeKd(bookid: String): BookResouceTypeKDEntity? {
        return getDatabase().bookResouceTypeKDDao.query(bookid)
    }

    /**
     * 快读精确求书
     */
    fun getBookFeedback(bookName: String, author: String): Flowable<String> {
        val map = hashMapOf<String, String>(
            "book" to bookName, "author" to author, "system" to "Android",
            "package" to "kuaidu.xiaoshuo.yueduqi", "version" to "1100"
        )
        return getKuaiDuApi().getBookFeedback(niceBody(map))
    }

    /**
     * 获取所有下载的书籍
     */
    fun queryDownloadBooks(): List<BookDownloadEntity> {
        return getDatabase().bookDownloadDao.queryAll()
    }

    /**
     * 更新下载记录
     */
    fun updateDownloadBook(
        bookid: String,
        bookName: String,
        resouce: String,
        progress: Int,
        total: Int,
        cover: String,
        author: String,
        uuid: String
    ) {

        val entity = getDatabase().bookDownloadDao.query(bookid)
        if (entity == null) {
            getDatabase().bookDownloadDao.insert(BookDownloadEntity().apply {
                this.bookId = bookid
                this.bookName = bookName
                this.resouce = resouce
                this.progress = progress
                this.total = total
                this.cover = cover
                this.author = author
                this.uuid = uuid
            })
            return
        }

        getDatabase().bookDownloadDao.update(entity.apply {
            this.progress = progress
            this.total = total
        })
    }

    /**
     * 删除缓存记录
     */
    @Synchronized
    fun deleteDownload(bookid: String) {
        getDatabase().bookDownloadDao.deleteDownload(bookid)
    }

    /**
     * 书城 -> 男生
     */
    fun getStoreMan(): Flowable<StoreManResult> {
        return getKSNetApi().getStoreMan()
    }

    /**
     * 书城 -> 女生
     */
    fun getStoreLady(): Flowable<StoreManResult> {
        return getKSNetApi().getStoreLady()
    }

    /**
     * 书城 -> 榜单 -> 男生
     */
    fun getStoreRankingMan(): Flowable<StoreRankingResult> {
        return getKSNetApi().getStoreRankingMan()
    }

    /**
     * 书城 -> 榜单 -> 女生
     */
    fun getStoreRankingLady(): Flowable<StoreRankingResult> {
        return getKSNetApi().getStoreRankingLady()
    }

    /**
     * 获取书单
     *
     * 最新发布/本周最热/最多收藏/小编推荐
     */
    fun getBooksList(gender: String, type: String, pageNum: Int): Flowable<BooksListResult> {
        return getKSNetApi().getBooksList(gender, type, pageNum.toString())
    }

    /**
     * 正版排行榜
     *
     * 起点/纵横/去起/若初/红薯/潇湘/逐浪
     */
    fun getMoreRankingList(gender: String, type: Int, pageNum: Int): Flowable<MoreRankingResult> {
        return getKSNetApi().getMoreRankingList(gender, type, pageNum.toString())
    }

    /**
     * 看书神器 排行榜
     */
    fun getKanShuRankingList(
        gender: String,
        type: String,
        date: String,
        pageNum: Int
    ): Flowable<KanShuRankingResult> {
        return getKSNetApi().getKanShuRankingList(gender, type, date, pageNum)
    }


    /**
     * 书单详情页
     */
    fun getBookListDetail(id: String): Flowable<ShuDanDetailResult> {
        return getKSNetApi().getBookListDetail(id)
    }

    /**
     * 分类
     */
    fun getCategoryList(): Flowable<List<BookCategoryDataResult>> {
        return getKuaiDuApi().getCategoryList()
    }

    /**
     * 快读 男生分类
     */
    fun queryCategoryMan(): List<CategoryKDEntity> {
        return getDatabase().categoryKDDao.queryMan()
    }

    /**
     * 快读 女生分类
     */
    fun queryCategoryLady(): List<CategoryKDEntity> {
        return getDatabase().categoryKDDao.queryLady()
    }

    /**
     * 快读 出版分类
     */
    fun queryCategoryPress(): List<CategoryKDEntity> {
        return getDatabase().categoryKDDao.queryPress()
    }

    /**
     * 快读 插入分类
     */
    fun insertCategoryList(obj: List<CategoryKDEntity>) {
        if (obj.isEmpty()) return
        getDatabase().categoryKDDao.clear()
        getDatabase().categoryKDDao.insert(obj = obj)
    }

    /**
     * 分类详情
     */
    fun getCategoryDetailList(
        gender: String,
        type: Int,
        major: String,
        pageNum: Int
    ): Flowable<CategoryDetailResult> {
        return getKuaiDuApi().getCategoryDetailList(gender, type, major, pageNum, 50)
    }

    /**
     * 获取游戏推荐
     */
    fun getGameRecommentList(pageNum: Int): Flowable<GameRecommentResult> {
        return getNetApi().getGameRecommentList(pageNum, 20)
    }

    /**
     * 获取配置文件
     */
    fun getAppConfig(): Flowable<AppConfigResult> {
        return getNetApi().getAppConfig()
    }

    /**
     * 查询配置
     */
    fun queryConfig(): ConfigEntity {
        return getDatabase().configDao.query()
    }

    /**
     * 保存配置文件
     */
    fun insertConfig(obj: ConfigEntity) {
        getDatabase().configDao.insert(obj)
    }

    /**
     * 保存看书神器 排行榜
     */
    fun insertStoreRanking(obj: StoreRankingEntity) {
        getDatabase().storeRankingDao.insert(obj)
    }

    /**
     * 清空书城记录
     */
    fun cleanStoreRanking(genderType: String) {
        getDatabase().storeRankingDao.clean(genderType)
    }

    /**
     * 查询看书神器 排行榜
     */
    fun queryStoreRanking(genderType: String):StoreRankingEntity? {
        return getDatabase().storeRankingDao.query(genderType)
    }

    /**
     * 看书神器 保存书城列表
     */
    fun insertStoreEntity(obj: List<StoreEntity>) {
        getDatabase().storeDao.insert(obj)
    }

    /**
     * 看书神器 查看看书列表
     */
    fun queryStoreEntity(genderType: String): List<StoreEntity> {
        return getDatabase().storeDao.query(genderType)
    }

    /**
     * 看书神器 删除书城列表
     */
    fun deleteStoreEntity(genderType: String) {
        getDatabase().storeDao.delete(genderType)
    }

    /**
     * 查询 书城 -> 点击更多
     */
    fun queryBooksKSEntity(name:String, gender: String, type: String, date: String = ""): List<BooksKSEntity> {
        return getDatabase().booksKSDao.query(gender = gender, toobarName = name, type = type, date = date)
    }

    /**
     * 删除 书城 -> 点击更多
     */
    fun deleteBooksKSEntity(name: String, gender: String, type: String, date: String = "") {
        getDatabase().booksKSDao.delete(gender = gender, toobarName = name, type = type, date = date)
    }

    /**
     * 保存 书城 -> 点击更多
     * 保存 书城 -> 书单
     */
    fun insertBooksKSEntity(obj: List<BooksKSEntity>) {
        getDatabase().booksKSDao.insert(obj)
    }

    /**
     * 查询 书城 -> 书单
     */
    fun queryShuDanEntity(name: String, gender: String, type: String):List<ShuDanEntity> {
        return getDatabase().shuDanDao.query(gender = gender, name = name, type = type)
    }

    /**
     * 删除 书城 -> 书单
     */
    fun deleteShuDanEntity(name: String, gender: String, type: String) {
        return getDatabase().shuDanDao.delete(gender = gender, name = name, type = type)
    }

    /**
     * 保存 书城 -> 书单
     */
    fun insertShuDanEntity(list: List<ShuDanEntity>) {
        getDatabase().shuDanDao.insert(list)
    }

    /**
     * 查询收藏列表  看书源
     */
    fun queryCollectionKS(): List<BookInfoKSEntity> {
        return getDatabase().bookInfoKSDao.queryCollectionKS()
    }

    /**
     * 查询收藏列表  快读源
     */
    fun queryCollectionKD(): List<BookInfoKSEntity> {
        return getDatabase().bookInfoKSDao.queryCollectionKD()
    }

    /**
     * 获取所有收藏的连载列表
     */
    fun queryCollectionAllSerial(): List<BookInfoKSEntity> {
        return getDatabase().bookInfoKSDao.queryCollectionAllSerial()
    }

    /**
     * 随机获取小黄书列表
     */
    fun getBookSexList(pageNum:Int): Flowable<ChapterSexResult> {
        val map = hashMapOf<String, Int>("limit" to pageNum)
        return getNetApi().getBookSexList(niceBody(map))
    }

    /**
     * 插入小黄书
     */
    fun insert(list: List<SexBooksEntity>) {
        getDatabase().sexBooksDao.insert(list)
    }

    /**
     * 删除小黄书列表
     */
    fun cleanSexBooksEntity() {
        getDatabase().sexBooksDao.clean()
    }

    /**
     * 获取小黄书列表
     */
    fun querySexBooksEntityAll() :List<SexBooksEntity>{
        return getDatabase().sexBooksDao.queryAll()
    }
}