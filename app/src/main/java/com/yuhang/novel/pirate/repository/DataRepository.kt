package com.yuhang.novel.pirate.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.*
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.extension.niceBody
import com.yuhang.novel.pirate.extension.niceDir
import com.yuhang.novel.pirate.repository.api.KanShuNetApi
import com.yuhang.novel.pirate.repository.api.KuaiDuNetApi
import com.yuhang.novel.pirate.repository.api.NetApi
import com.yuhang.novel.pirate.repository.database.AppDatabase
import com.yuhang.novel.pirate.repository.database.entity.*
import com.yuhang.novel.pirate.repository.network.Http
import com.yuhang.novel.pirate.repository.network.NetManager
import com.yuhang.novel.pirate.repository.network.NetURL
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.*
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.BookCategoryDataResult
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.CategoryDetailResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.*
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.utils.application
import com.yuhang.novel.pirate.workmanager.NovelDownloadWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * 数据提供源
 */
class DataRepository {
    /**
     * 网络
     */
    private val mNetManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED, ::NetManager)


    /**
     * 提供网络操作
     */
    fun getNetApi() = Http.createRetrofit(NetURL.HOST).create(NetApi::class.java)

    /**
     * 看书神器
     */
    fun getKSNetApi() = Http.createRetrofit(NetURL.HOST_KUAIDU).create(KanShuNetApi::class.java)


    /**
     * 提供数据库操作
     */
    fun getDatabase() = AppDatabase.getInstance(application)

    /**
     * 快读
     */
    fun getKuaiDuApi() = Http.createRetrofit(NetURL.HOST_KUAIDU).create(KuaiDuNetApi::class.java)


    /**
     * 看书神器站内搜索
     */
    suspend fun searchBook(keyword: String) = withContext(Dispatchers.IO) {
         getKSNetApi().searchBook(keyword)
    }

    /**
     * 男生 -> 分类
     */
    suspend fun getBookCategory() = withContext(Dispatchers.IO) {
         getKSNetApi().getBookCategory()
    }

    /**
     * 书本详情
     */
    suspend fun getBookDetails(id: String) = withContext(Dispatchers.IO) {
         getKSNetApi().getBookDetails(dirId = niceDir(id.toLong()), bookId = id.toLong())
    }

    /**
     * 书本章节目录
     */
    suspend fun getBookChapterList(bookid: String) = withContext(Dispatchers.IO) {
        //返回的格式第二条数据是空的,单独进行处理
        val str = getKSNetApi().getBookChapterList(
            dirId = niceDir(bookid.toLong()),
            bookId = bookid.toLong()
        ).replace("},]}}", "}]}}")
         Gson().fromJson(str, ChapterListResult::class.java)
    }


    /**
     * 查询数据库书籍章节
     */
    suspend fun queryChapterObjList(bookid: String) = withContext(Dispatchers.IO) {
         getDatabase().bookChapterKSDao.queryObj(bookid)
    }

    /**
     * 插入数据库书籍章节
     */
    suspend fun insertChapterList(list: List<BookChapterKSEntity>)  = withContext(Dispatchers.IO){
        getDatabase().bookChapterKSDao.insert(list)
    }

    /**
     * 删除数据库书籍对应的章节列表
     */
    suspend fun deleteChapterList(bookid: String) = withContext(Dispatchers.IO) {
        getDatabase().bookChapterKSDao.query(bookid)?.let {
            getDatabase().bookChapterKSDao.delete(bookid)
        }

    }

    /**
     * 更新章节内容
     */
    suspend fun updateChapter(obj: BookChapterKSEntity) = withContext(Dispatchers.IO) {
        getDatabase().bookChapterKSDao.update(obj)
    }

    /**
     * 从数据库查询当前id对应的书籍信息
     */
    suspend fun queryBookInfo(bookid: String) = withContext(Dispatchers.IO) {
         getDatabase().bookInfoKSDao.query(bookid)
    }

    /**
     * 插入数据库书籍信息
     */
    suspend fun insertBookInfo(obj: BookInfoKSEntity) = withContext(Dispatchers.IO) {
        getDatabase().bookInfoKSDao.insert(obj)
    }

    /**
     * 更新数据库书籍信息
     */
    suspend fun updateBookInfo(obj: BookInfoKSEntity)  = withContext(Dispatchers.IO){
        //更新标签
        updateLable(obj.bookid, obj.lastChapterName)
        getDatabase().bookInfoKSDao.update(obj)
    }

    /**
     * 数据库查询章节内容
     */
    suspend fun queryBookContent(bookid: String, chapterid: String) = withContext(Dispatchers.IO) {
         getDatabase().bookContentKSDao.query(bookid, chapterid)
    }

    /**
     * 插入数据库章节内容
     */
    @Synchronized
    suspend fun insertBookContent(obj: BookContentKSEntity) = withContext(Dispatchers.IO) {
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
        return@withContext obj
    }


    /**
     * 数据库查询章节内容
     */
    suspend fun queryBookContentObj(bookid: String, chapterid: String) = withContext(Dispatchers.IO) {
         getDatabase().bookContentKSDao.queryObj(bookid, chapterid)
    }


    /**
     * 查询搜索记录记录
     */
    suspend fun querySearchHistoryList() = withContext(Dispatchers.IO) {
         getDatabase().searchHistoryKSDao.query()
    }

    /**
     * 插入历史记录
     */
    suspend fun insertSearchHistory(obj: SearchHistoryKSEntity) = withContext(Dispatchers.IO) {
         getDatabase().searchHistoryKSDao.insert(obj)
    }

    /**
     * 删除历史记录
     */
    suspend fun deleteSearchHistory(obj: SearchHistoryKSEntity)  = withContext(Dispatchers.IO){
         getDatabase().searchHistoryKSDao.delete(obj)
    }

    /**
     * 搜索历史记录
     */
    suspend fun querySearchHisotry(keyword: String) = withContext(Dispatchers.IO) {
         getDatabase().searchHistoryKSDao.query(keyword)
    }

    /**
     * 收藏书籍
     */
    suspend fun insertCollection(obj: BooksResult)  = withContext(Dispatchers.IO){

        val collectionKSEntity = getDatabase().bookCollectionKSDao.query(obj.getBookid())
        if (collectionKSEntity == null) {
            getDatabase().bookCollectionKSDao.insert(
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
    suspend fun queryCollection(bookid: String) = withContext(Dispatchers.IO) {
         getDatabase().bookCollectionKSDao.query(bookid)
    }

    /**
     * 查询所有收藏书籍
     */
    suspend fun queryCollectionAll() = withContext(Dispatchers.IO) {
         getDatabase().bookCollectionKSDao.queryAll()
    }

    /**
     * 删除收藏
     */
    suspend fun deleteCollection(bookid: String) = withContext(Dispatchers.IO) {
        getDatabase().bookCollectionKSDao.delete(bookid)
    }

    /**
     * 更新置顶时间戳
     */
    suspend fun updateBookInfoStickTime(bookid: String) = withContext(Dispatchers.IO) {
        getDatabase().bookInfoKSDao.update(System.currentTimeMillis(), bookid)
        getDatabase().bookInfoKSDao.query(bookid)
    }

    /**
     * 取消置顶
     */
    suspend fun updateBookInfoClearStickTime(bookid: String) = withContext(Dispatchers.IO) {
        getDatabase().bookInfoKSDao.update(0, bookid)
        getDatabase().bookInfoKSDao.query(bookid)
    }


    /**
     * 排行榜
     */
    suspend fun getRankingList(
        gender: String,
        type: String,
        date: String,
        pageNum: Int,
    )  = withContext(Dispatchers.IO) {
        getKSNetApi().getRankingList(gender, type, date, pageNum)
    }

    /**
     * 获取最近浏览的小说
     */
    suspend fun queryReadHistoryList(pageNum: Int) = withContext(Dispatchers.IO) {
         getDatabase().bookInfoKSDao.queryReadHistoryList(pageNum)
    }

    /**
     * 更新最后一次打开的时间和内容角标
     */
    suspend fun updateLastOpenContent(bookid: String, chapterid: String, lastContentPosition: Int)  = withContext(Dispatchers.IO){
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
    suspend fun queryLastOpenChapter(bookid: String) = withContext(Dispatchers.IO) {
         getDatabase().bookContentKSDao.queryLastOpenChapter(bookid)
    }

    /**
     * 更新标签数据
     */
    suspend fun updateLable(bookid: String, chapterName: String) = withContext(Dispatchers.IO) {
        val infoKSEntity = getDatabase().bookInfoKSDao.query(bookid) ?: return@withContext

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
    suspend fun login(username: String, password: String) = withContext(Dispatchers.IO) {
        val map = hashMapOf<String, String>("username" to username, "password" to password)
         getNetApi().login(niceBody(map))
    }

    /**
     * 注册
     */
    suspend fun register(username: String, password: String, email: String) = withContext(Dispatchers.IO) {
        val map = hashMapOf<String, String>(
            "username" to username,
            "password" to password,
            "email" to email
        )
         getNetApi().register(niceBody(map))
    }

    /**
     * 添加收藏
     */
    suspend fun addCollection(
        bookName: String, bookid: String, author: String, cover: String,
        description: String, bookStatus: String, classifyName: String, resouceType: String,
    ) = withContext(Dispatchers.IO) {
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
        return@withContext getNetApi().addCollection(niceBody(map))
    }

    /**
     * 获取收藏列表
     */
    suspend fun getCollectionList(pageNum: Int) = withContext(Dispatchers.IO) {
         getNetApi().getCollectionList(pageNum, pageSize = 30)
    }

    /**
     * 检测版本号
     */
    suspend fun checkVersion(versionName: String) = withContext(Dispatchers.IO) {
         getNetApi().checkVersion(versionName)
    }

    /**
     * 查找最后一次登录帐号
     */
    suspend fun getLastUser() = withContext(Dispatchers.IO) {
         getDatabase().userDao.queryUser()
    }

    /**
     * 插入一条帐号
     */
    suspend fun insert(userEntity: UserEntity)  = withContext(Dispatchers.IO){
        getDatabase().userDao.insert(userEntity)
    }

    /**
     * 修改最后登陆时间
     */
    suspend fun updateUserLastTime(uid: String)  = withContext(Dispatchers.IO){
        getDatabase().userDao.updateLastTime(uid, Date())
    }

    /**
     * 删除用户
     */
    suspend fun deleteUser(userEntity: UserEntity)  = withContext(Dispatchers.IO){
        getDatabase().userDao.delete(userEntity)
    }

    /**
     * 根据uid查询
     */
    suspend fun queryUser(username: String) = withContext(Dispatchers.IO) {
         getDatabase().userDao.queryUser(username)
    }

    /**
     * 查询所有收藏书本信息
     */
    suspend fun queryBookInfoCollectionAll() = withContext(Dispatchers.IO) {
         getDatabase().bookInfoKSDao.queryCollectionAll()
    }

    /**
     * 查询排行榜数据
     */
    suspend fun queryRankingListAll() = withContext(Dispatchers.IO) {
         getDatabase().rankingListDao.queryAll()
    }

    /**
     * 删除排行榜所有数据
     */
    suspend fun deleteRankingListAll()  = withContext(Dispatchers.IO){
         getDatabase().rankingListDao.deleteAll()
    }

    /**
     * 批量插入排行榜数据
     */
    suspend fun insertRankingList(list: List<RankingListEntity>) = withContext(Dispatchers.IO) {
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
    suspend fun deleteNetCollect(bookid: String, resouceType: String) = withContext(Dispatchers.IO) {
         getNetApi().deleteCollectList(bookid, resouceType)
    }

    /**
     * 清空帐号
     */
    suspend fun clearUsers()  = withContext(Dispatchers.IO){
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
    suspend fun clearBookInfo()  = withContext(Dispatchers.IO){
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
    suspend fun getReadHistoryList(pageNum: Int) = withContext(Dispatchers.IO) {
         getNetApi().getReadHistoryList(pageNum = pageNum, pageSize = 100)
    }

    /**
     * 获取收藏列表的阅读记录
     */
    suspend fun getReadHistoryCollectionsList(pageNum: Int) = withContext(Dispatchers.IO) {
         getNetApi().getReadHistoryCollectionsList(pageNum = pageNum, pageSize = 100)
    }

    /**
     * 获取指定小说阅读记录
     */
    suspend fun getReadHistoryCollectionsList(bookid: String) = withContext(Dispatchers.IO) {
         getNetApi().getReadHistoryCollectionsList(bookid = bookid)
    }

    /**
     * 更新阅读记录
     */
    suspend fun updateReadHistory(
        bookName: String,
        bookid: String,
        chapterid: String,
        chapterName: String,
        author: String,
        cover: String,
        description: String,
        resouceType: String,
        content: String,
    ) = withContext(Dispatchers.IO) {

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

        //没登陆返回空
        if (application.getToken().isEmpty()) {
            return@withContext StatusResult(-1, "未查询到阅读记录")
        }
         getNetApi().updateReadHistory(niceBody(map))
    }

    /**
     * 查看章节对应记录
     */
    suspend fun queryBookReadHistoryEntity(bookid: String, chapterid: String) = withContext(Dispatchers.IO) {
         getDatabase().bookReadHistoryDao.queryBookReadHistoryEntity(bookid, chapterid)
    }

    /**
     * 更新最后一次阅读记录到本地
     */
    suspend fun updateLocalREadHistory(
        bookid: String,
        chapterid: String,
        lastReadTime: Long = System.currentTimeMillis(),
    )  = withContext(Dispatchers.IO){

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
    suspend fun queryBookReadHistoryEntity(bookid: String) = withContext(Dispatchers.IO) {
         getDatabase().bookReadHistoryDao.queryLastChanpterEntity(bookid)
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
    suspend fun getMailCode(mail: String) = withContext(Dispatchers.IO) {
         getNetApi().getMailCode(mail)
    }

    /**
     * 检测用户邮箱是否存在
     */
    suspend fun checkEmailEmpty(email: String) = withContext(Dispatchers.IO) {
         getNetApi().checkEmailEmpty(email)
    }

    /**
     * 检测邮箱验证码
     */
    suspend fun checkEmailCode(email: String, newCode: String, oldCode: String) = withContext(Dispatchers.IO) {
        val map = hashMapOf<String, String>(
            "email" to email, "newCode" to newCode,
            "oldCode" to oldCode
        )
         getNetApi().checkEmailCode(niceBody(map))
    }

    /**
     * 修改密码
     */
    suspend fun updatePassword(
        email: String,
        username: String,
        password: String,
        againPassword: String,
    ) = withContext(Dispatchers.IO) {
        val map = hashMapOf<String, String>(
            "email" to email, "username" to username,
            "password" to password, "againPassword" to againPassword
        )
         getNetApi().updatePassword(niceBody(map))
    }

    /**
     * 书源列表
     */
    suspend fun getResouceList(pageNum: Int) = withContext(Dispatchers.IO) {
        val map = hashMapOf<String, Int>("type" to 0, "pageNum" to pageNum, "pageSize" to 100)
         getNetApi().getResouceList(niceBody(map))
    }


    /**
     * 书名/作者搜索
     */
    suspend fun getBookSearchList(keyword: String) = withContext(Dispatchers.IO) {
        val map = hashMapOf<String, String>("keyword" to keyword)
         getNetApi().getBookSearchList(niceBody(map))
    }

    /**
     * 作者所有作品
     */
    suspend fun getAuthorBooksList(author: String) = withContext(Dispatchers.IO) {
        val map = hashMapOf<String, String>("author" to author)
         getNetApi().getAuthorBooksList(niceBody(map))
    }

    /**
     * 保存公告信息
     */
    suspend fun insert(obj: PushMessageEntity)  = withContext(Dispatchers.IO){
        getDatabase().pushMessageDao.insert(obj)
    }

    /**
     * 查询最后一次公告信息
     */
    suspend fun queryNoteEntity() = withContext(Dispatchers.IO) {
         getDatabase().pushMessageDao.queryNote()
    }

    /**
     * 删除推送消息
     */
    suspend fun delete(obj: PushMessageEntity)  = withContext(Dispatchers.IO){
        getDatabase().pushMessageDao.delete(obj)
    }

    /**
     * 修改书籍详情修改时间
     */
    suspend fun updateBookInfoLastReadTime(bookid: String)  = withContext(Dispatchers.IO){
        getDatabase().bookInfoKSDao.updateLastReadTime(System.currentTimeMillis(), bookid)
    }

    /**
     * 快读源插入
     */
    suspend fun insertKuaiDuResouce(obj: BookResouceTypeKDEntity) = withContext(Dispatchers.IO) {
        val typeKDEntity = getDatabase().bookResouceTypeKDDao.query(obj.bookid)
        if (typeKDEntity == null) {
            getDatabase().bookResouceTypeKDDao.insert(obj)
            return@withContext
        }

        obj.id = typeKDEntity.id
        getDatabase().bookResouceTypeKDDao.update(obj)
    }

    /**
     * 删除指定小说记录
     */
    suspend fun deleteBookHistory(bookid: String) = withContext(Dispatchers.IO) {
        getDatabase().bookReadHistoryDao.clear(bookid)
    }

    /**
     * 删除小说内容
     */
    suspend fun deleteBookContent(bookid: String) = withContext(Dispatchers.IO) {
        getDatabase().bookContentKSDao.delete(bookid)
    }

    /**
     * 更改收藏
     */
    suspend fun updateCollection(obj: BookCollectionKSEntity)  = withContext(Dispatchers.IO){
        getDatabase().bookCollectionKSDao.update(obj)
    }

    /**
     * 获取快读渠道源
     */
    suspend fun getResouceTypeKd(bookid: String) = withContext(Dispatchers.IO) {
         getDatabase().bookResouceTypeKDDao.query(bookid)
    }

    /**
     * 快读精确求书
     */
    suspend fun getBookFeedback(bookName: String, author: String) = withContext(Dispatchers.IO){
        val map = hashMapOf<String, String>(
            "book" to bookName, "author" to author, "system" to "Android",
            "package" to "kuaidu.xiaoshuo.yueduqi", "version" to "1100"
        )
         getKuaiDuApi().getBookFeedback(niceBody(map))
    }

    /**
     * 获取所有下载的书籍
     */
    suspend fun queryDownloadBooks() = withContext(Dispatchers.IO) {
         getDatabase().bookDownloadDao.queryAll()
    }

    /**
     * 更新下载记录
     */
    suspend fun updateDownloadBook(
        bookid: String,
        bookName: String,
        resouce: String,
        progress: Int,
        total: Int,
        cover: String,
        author: String,
        uuid: String,
    )  = withContext(Dispatchers.IO){

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
            return@withContext
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
    suspend fun deleteDownload(bookid: String)  = withContext(Dispatchers.IO){
        getDatabase().bookDownloadDao.deleteDownload(bookid)
    }

    /**
     * 书城 -> 男生
     */
    suspend fun getStoreMan() = withContext(Dispatchers.IO) {
         getKSNetApi().getStoreMan()
    }

    /**
     * 书城 -> 女生
     */
    suspend fun getStoreLady() = withContext(Dispatchers.IO) {
         getKSNetApi().getStoreLady()
    }

    /**
     * 书城 -> 榜单 -> 男生
     */
    suspend fun getStoreRankingMan() = withContext(Dispatchers.IO) {
         getKSNetApi().getStoreRankingMan()
    }

    /**
     * 书城 -> 榜单 -> 女生
     */
    suspend fun getStoreRankingLady() = withContext(Dispatchers.IO){
         getKSNetApi().getStoreRankingLady()
    }

    /**
     * 获取书单
     *
     * 最新发布/本周最热/最多收藏/小编推荐
     */
    suspend fun getBooksList(gender: String, type: String, pageNum: Int) = withContext(Dispatchers.IO) {
         getKSNetApi().getBooksList(gender, type, pageNum.toString())
    }

    /**
     * 正版排行榜
     *
     * 起点/纵横/去起/若初/红薯/潇湘/逐浪
     */
    suspend fun getMoreRankingList(gender: String, type: Int, pageNum: Int) = withContext(Dispatchers.IO) {
         getKSNetApi().getMoreRankingList(gender, type, pageNum.toString())
    }

    /**
     * 看书神器 排行榜
     */
    suspend fun getKanShuRankingList(
        gender: String,
        type: String,
        date: String,
        pageNum: Int,
    ) = withContext(Dispatchers.IO) {
         getKSNetApi().getKanShuRankingList(gender, type, date, pageNum)
    }


    /**
     * 书单详情页
     */
    suspend fun getBookListDetail(id: String) = withContext(Dispatchers.IO) {
         getKSNetApi().getBookListDetail(id)
    }

    /**
     * 分类
     */
    suspend fun getCategoryList() = withContext(Dispatchers.IO){
         getKuaiDuApi().getCategoryList()
    }

    /**
     * 快读 男生分类
     */
    suspend fun queryCategoryMan() = withContext(Dispatchers.IO) {
         getDatabase().categoryKDDao.queryMan()
    }

    /**
     * 快读 女生分类
     */
    suspend fun queryCategoryLady() = withContext(Dispatchers.IO) {
         getDatabase().categoryKDDao.queryLady()
    }

    /**
     * 快读 出版分类
     */
    suspend fun queryCategoryPress() = withContext(Dispatchers.IO) {
         getDatabase().categoryKDDao.queryPress()
    }

    /**
     * 快读 插入分类
     */
    suspend fun insertCategoryList(obj: List<CategoryKDEntity>) = withContext(Dispatchers.IO) {
        if (obj.isEmpty()) return@withContext
        getDatabase().categoryKDDao.clear()
        getDatabase().categoryKDDao.insert(obj = obj)
    }

    /**
     * 分类详情
     */
    suspend fun getCategoryDetailList(
        gender: String,
        type: Int,
        major: String,
        pageNum: Int,
    ) = withContext(Dispatchers.IO) {
         getKuaiDuApi().getCategoryDetailList(gender, type, major, pageNum, 50)
    }

    /**
     * 获取游戏推荐
     */
    suspend fun getGameRecommentList(pageNum: Int) = withContext(Dispatchers.IO) {
         getNetApi().getGameRecommentList(pageNum, 20)
    }

    /**
     * 获取配置文件
     */
    suspend fun getAppConfig() = withContext(Dispatchers.IO) {
         getNetApi().getAppConfig()
    }

    /**
     * 查询配置
     */
    suspend fun queryConfig() = withContext(Dispatchers.IO) {
         getDatabase().configDao.query()
    }

    /**
     * 保存配置文件
     */
    suspend fun insertConfig(obj: ConfigEntity) = withContext(Dispatchers.IO) {
        getDatabase().configDao.insert(obj)
    }

    /**
     * 保存看书神器 排行榜
     */
    suspend fun insertStoreRanking(obj: StoreRankingEntity) = withContext(Dispatchers.IO) {
        getDatabase().storeRankingDao.insert(obj)
    }

    /**
     * 清空书城记录
     */
    suspend fun cleanStoreRanking(genderType: String) = withContext(Dispatchers.IO) {
        getDatabase().storeRankingDao.clean(genderType)
    }

    /**
     * 查询看书神器 排行榜
     */
    suspend fun queryStoreRanking(genderType: String) = withContext(Dispatchers.IO) {
         getDatabase().storeRankingDao.query(genderType)
    }

    /**
     * 看书神器 保存书城列表
     */
    suspend fun insertStoreEntity(obj: List<StoreEntity>) = withContext(Dispatchers.IO) {
        getDatabase().storeDao.insert(obj)
    }

    /**
     * 看书神器 查看看书列表
     */
    suspend fun queryStoreEntity(genderType: String) = withContext(Dispatchers.IO) {
         getDatabase().storeDao.query(genderType)
    }

    /**
     * 看书神器 删除书城列表
     */
    suspend fun deleteStoreEntity(genderType: String)  = withContext(Dispatchers.IO){
        getDatabase().storeDao.delete(genderType)
    }

    /**
     * 查询 书城 -> 点击更多
     */
    suspend fun queryBooksKSEntity(
        name: String,
        gender: String,
        type: String,
        date: String = "",
    ) = withContext(Dispatchers.IO) {
         getDatabase().booksKSDao.query(
            gender = gender,
            toobarName = name,
            type = type,
            date = date
        )
    }

    /**
     * 删除 书城 -> 点击更多
     */
    suspend fun deleteBooksKSEntity(name: String, gender: String, type: String, date: String = "") = withContext(Dispatchers.IO) {
        getDatabase().booksKSDao.delete(
            gender = gender,
            toobarName = name,
            type = type,
            date = date
        )
    }

    /**
     * 保存 书城 -> 点击更多
     * 保存 书城 -> 书单
     */
    suspend fun insertBooksKSEntity(obj: List<BooksKSEntity>) = withContext(Dispatchers.IO) {
        getDatabase().booksKSDao.insert(obj)
    }

    /**
     * 查询 书城 -> 书单
     */
    suspend fun queryShuDanEntity(name: String, gender: String, type: String) = withContext(Dispatchers.IO) {
         getDatabase().shuDanDao.query(gender = gender, name = name, type = type)
    }

    /**
     * 删除 书城 -> 书单
     */
    suspend fun deleteShuDanEntity(name: String, gender: String, type: String) = withContext(Dispatchers.IO) {
         getDatabase().shuDanDao.delete(gender = gender, name = name, type = type)
    }

    /**
     * 保存 书城 -> 书单
     */
    suspend fun insertShuDanEntity(list: List<ShuDanEntity>) = withContext(Dispatchers.IO) {
        getDatabase().shuDanDao.insert(list)
    }

    /**
     * 查询收藏列表  看书源
     */
    suspend fun queryCollectionKS() = withContext(Dispatchers.IO) {
         getDatabase().bookInfoKSDao.queryCollectionKS()
    }

    /**
     * 查询收藏列表  快读源
     */
    suspend fun queryCollectionKD() = withContext(Dispatchers.IO) {
         getDatabase().bookInfoKSDao.queryCollectionKD()
    }

    /**
     * 获取所有收藏的连载列表
     */
    suspend fun queryCollectionAllSerial() = withContext(Dispatchers.IO) {
         getDatabase().bookInfoKSDao.queryCollectionAllSerial()
    }

    /**
     * 随机获取小黄书列表
     */
    suspend fun getBookSexList(pageNum: Int) = withContext(Dispatchers.IO) {
        val map = hashMapOf<String, Int>("limit" to pageNum)
         getNetApi().getBookSexList(niceBody(map))
    }

    /**
     * 插入小黄书
     */
    suspend fun insert(list: List<SexBooksEntity>) = withContext(Dispatchers.IO) {
        getDatabase().sexBooksDao.insert(list)
    }

    /**
     * 删除小黄书列表
     */
    suspend fun cleanSexBooksEntity() = withContext(Dispatchers.IO) {
        getDatabase().sexBooksDao.clean()
    }

    /**
     * 获取小黄书列表
     */
    suspend fun querySexBooksEntityAll() = withContext(Dispatchers.IO) {
         getDatabase().sexBooksDao.queryAll()
    }

    /**
     * 更新 最后更新时间
     */
    suspend fun updateLastTime(bookid: String, lastTime: Long) {
        getDatabase().bookInfoKSDao.updateLastTime(bookid, lastTime)
    }
}