package com.yuhang.novel.pirate.repository.api

import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface KuaiDuNetApi {


    /**
     * 搜索
     */
    @GET("http://api.gdugm.cn/book/search")
    suspend fun search(@QueryMap map: Map<String, String>): SearchKdResult

    /**
     * 书籍详情页
     */
    @Headers("Cache-Control: public, max-stale=5")
    @GET("http://api.wgfgr.cn/book/info")
    suspend fun getBookDetails(@Query("bookId") bookId: String): BookDetailsKdResult

    /**
     * 同类推荐
     */
    @GET("http://api.wgfgr.cn/book/{bookid}/recommend")
    suspend fun getRecommendBook(@Path("bookid") bookid: String)

    /**
     * 作者所有作品
     */
    @GET("http://api.wgfgr.cn/book/accurate-search")
    suspend fun getAuthorBookAll(@QueryMap map: Map<String, String>): AuthorBooksKdResult

    /**
     * 章节目录
     */
    @GET("http://api.wgfgr.cn/toc/mix")
    suspend fun getChapterList(@Query("bookId") bookId: String): ChapterListKdResult

    /**
     * 获取内容
     * path一定要自己编码,不然请求速度很慢
     */
    @GET("http://chapter.baihangsou.cn/chapter/{link}")
    suspend fun getResouceContent(@Path("link", encoded = true) link: String): ContentKdResult

    /**
     * 下载章节内容
     * path一定要自己编码,不然请求速度很慢
     */
    @GET("http://chapter.baihangsou.cn/chapter/{link}")
    fun downloadChapterContent(@Path("link", encoded = true) link: String): Call<ContentKdResult>

    /**
     * 书本源列表
     */
    @GET("http://api.wgfgr.cn/toc/list")
    suspend fun getResouceList(@Query("bookId") bookId: String): List<ResouceListKdResult>


    /**
     * 第三方源目录列表
     */
    @GET("http://api.wgfgr.cn/chapter/list")
    suspend fun getResouceChapterList(@Query("tocId") tocId: String): ChapterListKdResult

    /**
     * 小说更新
     */
    @GET("http://api.gdugm.cn/book/update")
    suspend fun getBookUpdate(@Query("id") id: String): List<BookUpdateKdResult>

    /**
     * 搜索模糊匹配
     */
    @GET("http://api.wgfgr.cn/search/suggest")
    suspend fun searchSuggest(@Query("key") key: String): SearchSuggestKdResult

    /**
     * 精确求书
     */
    @POST("http://api.wgfgr.cn/bookfeedback")
    suspend fun getBookFeedback(@Body body: RequestBody): String

    /**
     * 分类
     * 男生/女生/出版
     */
    @GET("http://api.gdugm.cn/category/all")
    suspend fun getCategoryList(): List<BookCategoryDataResult>

    /**
     * gender  press=出版  female=女生  male=男生
     * type 热门=1 好评=2 连载=3 完结=4
     */
    @GET("http://api.gdugm.cn/book/list")
    suspend fun getCategoryDetailList(
        @Query("gender") gender: String, @Query("type") type: Int,
        @Query("major") major: String,
        @Query("start") start: Int, @Query("limit") limit: Int
    ):CategoryDetailResult
}