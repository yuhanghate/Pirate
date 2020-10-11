package com.yuhang.novel.pirate.repository.api

import com.yuhang.novel.pirate.repository.network.data.pirate.result.*
import okhttp3.RequestBody
import retrofit2.http.*


interface NetApi {

    /**
     * 登录
     */
    @POST("/api/m/user/login")
     suspend fun login(@Body body: RequestBody): UserResult

    /**
     * 注册
     */
    @POST("/api/m/user/register")
     suspend fun register(@Body body: RequestBody): UserResult

    /**
     * 添加收藏
     */
    @POST("/api/m/book/collection/add")
    suspend fun addCollection(@Body body: RequestBody): StatusResult

    /**
     * 获取收藏列表
     */
    @GET("/api/m/book/collection/list")
    suspend fun getCollectionList(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int): CollectionResult

    /**
     * 检测版本更新
     */
    @GET("/api/m/version/check/update")
    suspend fun checkVersion(@Query("versionName") versionName: String): VersionResult

    /**
     * 删除收藏
     */
    @DELETE("/api/m/book/collection/delete")
    suspend fun deleteCollectList(@Query("bookid") bookid: String, @Query("resouceType") resouceType: String): StatusResult

    /**
     * 最近浏览
     */
    @GET("/api/m/book/read/history/list")
    suspend fun getReadHistoryList(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int): ReadHistoryResult

    /**
     * 更新阅读记录
     */
    @POST("/api/m/book/read/history/update")
    suspend fun updateReadHistory(@Body body: RequestBody):StatusResult

    /**
     * 获取收藏列表的阅读记录
     */
    @GET("/api/m/book/read/collection/history/list")
    suspend fun getReadHistoryCollectionsList(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int): ReadHistoryResult

    /**
     * 获取指定小说的阅读记录
     */
    @GET("/api/m/book/read/history/book")
    suspend fun getReadHistoryCollectionsList(@Query("bookid") bookid: String): ReadHistoryBookResult

    /**
     * 发送邮箱验证码
     */
    @GET("/api/m/email/code/send")
    suspend fun getMailCode(@Query("mail") mail:String):EmailCodeResult

    /**
     * 检测用户邮箱是否存在
     */
    @GET("/api/m/email/user/check")
    suspend fun checkEmailEmpty(@Query("email") email:String):StatusResult

    /**
     * 检测邮箱验证码
     */
    @POST("/api/m/email/code/check")
    suspend fun checkEmailCode(@Body body: RequestBody):StatusResult

    /**
     * 修改密码
     */
    @POST("/api/m/email/update/password")
    suspend fun updatePassword(@Body body: RequestBody):UserResult

    /**
     * 书源列表
     */
    @POST("/api/m/resouce/list/get")
    suspend fun getResouceList(@Body body: RequestBody):BookResouceResult

    /**
     * 书名/作者搜索
     */
    @POST("/api/m/book/books/search")
    suspend fun getBookSearchList(@Body body: RequestBody):SearchSuggestResult

    /**
     * 作者所有作品
     */
    @POST("/api/m/book/books/author/all")
    suspend fun getAuthorBooksList(@Body body: RequestBody):AuthorBooksResult

    /**
     * 根据看书id查找
     */
    @POST("/api/m/book/books/search/bookid/ks")
    suspend fun getBooksSearch(@Body body: RequestBody):BookSearchKdResult

    /**
     * 获取游戏推荐
     */
    @Headers("Cache-Control: public, max-stale=86400")
    @GET("/api/m/ad/game/recomment")
    suspend fun getGameRecommentList(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int):GameRecommentResult

    /**
     * 获取配置文件
     */
    @POST("/api/m/user/config")
    suspend fun getAppConfig():AppConfigResult

    /**
     * 随机获取小黄书列表
     */
    @POST("/api/m/book/books/sex/rand/list")
    suspend fun getBookSexList(@Body body: RequestBody):ChapterSexResult

    /**
     * 小黄书章节
     */
    @POST("/api/m/book/books/sex/chapter/list")
     suspend fun getBookSexChapter(@Body body: RequestBody):SexChapterResult

    /**
     * 小黄书内容
     */
    @POST("/api/m/book/books/sex/content")
    suspend fun getBookSexContent(@Body body: RequestBody):SexContentResult
}