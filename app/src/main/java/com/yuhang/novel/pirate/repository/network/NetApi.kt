package com.yuhang.novel.pirate.repository.network

import com.yuhang.novel.pirate.repository.network.data.pirate.result.*
import io.reactivex.Flowable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*


interface NetApi {

    /**
     * 登陆
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
    fun deleteCollectList(@Query("bookid") bookid: Int, @Query("resouceType") resouceType: String): Flowable<StatusResult>

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
}