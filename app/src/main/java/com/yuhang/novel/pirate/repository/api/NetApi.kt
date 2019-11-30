package com.yuhang.novel.pirate.repository.api

import com.yuhang.novel.pirate.repository.network.data.pirate.result.*
import io.reactivex.Flowable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*


interface NetApi {

    /**
     * 登录
     */
    @POST("/api/m/user/login")
    fun login(@Body body: RequestBody): Flowable<UserResult>

    /**
     * 注册
     */
    @POST("/api/m/user/register")
    fun register(@Body body: RequestBody): Flowable<UserResult>

    /**
     * 添加收藏
     */
    @POST("/api/m/book/collection/add")
    fun addCollection(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 获取收藏列表
     */
    @GET("/api/m/book/collection/list")
    fun getCollectionList(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int): Flowable<CollectionResult>

    /**
     * 检测版本更新
     */
    @GET("/api/m/version/check/update")
    fun checkVersion(@Query("versionName") versionName: String): Flowable<VersionResult>

    /**
     * 下载apk文件
     *
     * @param fileUrl
     * @return
     */
    @Streaming //大文件时要加不然会OOM
    @GET
    fun downloadFile(@Url fileUrl: String): retrofit2.Call<ResponseBody>


    /**
     * 删除收藏
     */
    @DELETE("/api/m/book/collection/delete")
    fun deleteCollectList(@Query("bookid") bookid: String, @Query("resouceType") resouceType: String): Flowable<StatusResult>

    /**
     * 最近浏览
     */
    @GET("/api/m/book/read/history/list")
    fun getReadHistoryList(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int): Flowable<ReadHistoryResult>

    /**
     * 更新阅读记录
     */
    @POST("/api/m/book/read/history/update")
    fun updateReadHistory(@Body body: RequestBody):Flowable<StatusResult>

    /**
     * 获取收藏列表的阅读记录
     */
    @GET("/api/m/book/read/collection/history/list")
    fun getReadHistoryCollectionsList(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int): Flowable<ReadHistoryResult>

    /**
     * 获取指定小说的阅读记录
     */
    @GET("/api/m/book/read/history/book")
    fun getReadHistoryCollectionsList(@Query("bookid") bookid: String): Flowable<ReadHistoryBookResult>

    /**
     * 发送邮箱验证码
     */
    @GET("/api/m/email/code/send")
    fun getMailCode(@Query("mail") mail:String):Flowable<EmailCodeResult>

    /**
     * 检测用户邮箱是否存在
     */
    @GET("/api/m/email/user/check")
    fun checkEmailEmpty(@Query("email") email:String):Flowable<StatusResult>

    /**
     * 检测邮箱验证码
     */
    @POST("/api/m/email/code/check")
    fun checkEmailCode(@Body body: RequestBody):Flowable<StatusResult>

    /**
     * 修改密码
     */
    @POST("/api/m/email/update/password")
    fun updatePassword(@Body body: RequestBody):Flowable<UserResult>

    /**
     * 书源列表
     */
    @POST("/api/m/resouce/list/get")
    fun getResouceList(@Body body: RequestBody):Flowable<BookResouceResult>

    /**
     * 书名/作者搜索
     */
    @POST("/api/m/book/books/search")
    fun getBookSearchList(@Body body: RequestBody):Flowable<SearchSuggestResult>

    /**
     * 作者所有作品
     */
    @POST("/api/m/book/books/author/all")
    fun getAuthorBooksList(@Body body: RequestBody):Flowable<AuthorBooksResult>

    /**
     * 根据看书id查找
     */
    @POST("/api/m/book/books/search/bookid/ks")
    fun getBooksSearch(@Body body: RequestBody):Flowable<BookSearchKdResult>
}