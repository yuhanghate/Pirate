package com.yuhang.novel.pirate.repository.api

import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.*
import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.http.*

interface KuaiDuNetApi {

    //搜索 http://api.gdugm.cn/book/search?key=%E5%A4%A9%E8%9A%95%E5%9C%9F%E8%B1%86&start=0&limit=100


    /**
     * 搜索
     */
    @GET("http://api.gdugm.cn/book/search")
    fun search(@QueryMap map: Map<String, String>): Flowable<SearchKdResult>

    /**
     * 书籍详情页
     */
    @GET("http://api.wgfgr.cn/book/info")
    fun getBookDetails(@Query("bookId") bookId: String): Flowable<BookDetailsKdResult>

    /**
     * 同类推荐
     */
    @GET("http://api.wgfgr.cn/book/{bookid}/recommend")
    fun getRecommendBook(@Path("bookid") bookid: String)

    /**
     * 作者所有作品
     */
    @GET("http://api.wgfgr.cn/book/accurate-search")
    fun getAuthorBookAll(@QueryMap map: Map<String, String>): Flowable<AuthorBooksKdResult>

    /**
     * 章节目录
     */
    @GET("http://api.wgfgr.cn/toc/mix")
    fun getChapterList(@Query("bookId") bookId: String): Flowable<ChapterListKdResult>

    /**
     * 获取内容
     */
    @Headers(
        value = [
            "Content-Type: application/json;charset=gbk",
            "Accept: */*"
        ]
    )
    @GET("http://chapter.baihangsou.cn/chapter/{link}")
    fun getResouceContent(@Path("link") link: String): Flowable<ContentKdResult>

    /**
     * 下载章节内容
     */
    @GET("http://chapter.baihangsou.cn/chapter/{link}")
    fun downloadChapterContent(@Path("link") link: String): Call<ContentKdResult>

    /**
     * 书本源列表
     */
    @GET("http://api.wgfgr.cn/toc/list")
    fun getResouceList(@Query("bookId") bookId: String): Flowable<List<ResouceListKdResult>>


    /**
     * 第三方源目录列表
     */
    @GET("http://api.wgfgr.cn/chapter/list")
    fun getResouceChapterList(@Query("tocId") tocId: String): Flowable<ChapterListKdResult>
}