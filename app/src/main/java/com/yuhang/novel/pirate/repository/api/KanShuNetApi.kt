package com.yuhang.novel.pirate.repository.api

import com.yuhang.novel.pirate.repository.network.data.kanshu.result.*
import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KanShuNetApi {

    // 推荐 -> 周榜 https://appbdsc.cdn.bcebos.com/top/man/top/commend/week/1.html
    // 推荐 -> 月榜 https://appbdsc.cdn.bcebos.com/top/man/top/commend/month/1.html
    // 男生 -> 分类 https://appbdsc.cdn.bcebos.com/BookCategory.html
    // 书本详情 https://infos.xllxdg.com/BookFiles/Html/413/412435/info.html
    // 书本详情 -> 异世界的美食家 https://infos.xllxdg.com/BookFiles/Html/88/87101/info.html
    // 书本详情 -> 修真聊天群 https://infos.xllxdg.com/BookFiles/Html/37/36007/info.html
    // 目录 -> 修真聊天群 -> https://infos.xllxdg.com/BookFiles/Html/37/36007/index.html

    //章节 -> 内容 https://content.xllxdg.com/BookFiles/Html/37/36007/1902544.html
    //缓存书 -> 每个章节进行下载缓存 https://codeapibak.jiaston.com/BookFiles/Html/37/36007/2610933.html
    //缓存书 -> https://downbak.1122dh.com/BookFiles/Html/7/6493/3170867.html

    /**
     * 看书站内搜索
     */
    @GET("https://sou.jiaston.com/search.aspx")
    fun searchBook(
        @Query("key") key: String, @Query("page") page: Int = 1, @Query("siteid") siteid: String = "app2"
    ): Flowable<BookSearchResult>

    /**
     * 男生 -> 分类
     */
    @GET("https://appbdsc.cdn.bcebos.com/BookCategory.html")
    fun getBookCategory(): Flowable<BookCategoryResult>

    /**
     * 书本详情
     * dirId = (bookId - 后三位) + 1
     * bookId: 书本id
     */
    @GET("https://infos.1122dh.com/BookFiles/Html/{dirId}/{bookId}/info.html")
    fun getBookDetails(
        @Path("dirId") dirId: Int, @Path(
            "bookId"
        ) bookId: Long
    ): Flowable<BookDetailsResult>

    /**
     * 书本章节目录
     */
    @GET("https://infos.1122dh.com/BookFiles/Html/{dirId}/{bookId}/index.html")
    fun getBookChapterList(
        @Path("dirId") dirId: Int, @Path(
            "bookId"
        ) bookId: Long
    ): Flowable<String>

    /**
     * 获取章节内容
     */
    @GET("https://content.1122dh.com/BookFiles/Html/{dirId}/{bookId}/{chapterId}.html")
    fun getChapterContent(
        @Path("dirId") dirId: Int, @Path(
            "bookId"
        ) bookId: Long, @Path("chapterId") chapterId: Int
    ): Flowable<ContentResult>

    /**
     * 下载章节内容
     * 不知道为什么要区分,可能这个域名是CDN
     */
    @GET("https://downbak.1122dh.com/BookFiles/Html/{dirId}/{bookId}/{chapterId}.html")
    fun downloadChapterContent(
        @Path("dirId") dirId: Int, @Path(
            "bookId"
        ) bookId: Long, @Path("chapterId") chapterId: Int
    ): Flowable<ContentResult>

    /**
     * 下载章节内容
     * 不知道为什么要区分,可能这个域名是CDN
     */
    @GET("https://downbak.1122dh.com/BookFiles/Html/{dirId}/{bookId}/{chapterId}.html")
    fun downloadNovel(
        @Path("dirId") dirId: Int, @Path(
            "bookId"
        ) bookId: Long, @Path("chapterId") chapterId: Int
    ): Call<ContentResult>




    /**
     * 排行榜
     * gender >  man:男的  lady:女生
     * type -> hot:最热  commend:推荐  over:完结  collect:收藏  new:新书  vote:评分
     * date -> week:周榜 month:月榜  total:总榜
     * pageNum : 分页
     */
    @GET("https://quapp.1122dh.com/top/{gender}/top/{type}/{date}/{pageNum}.html")
    fun getRankingList(@Path("gender") gender: String, @Path("type") type: String, @Path("date") date: String, @Path("pageNum") pageNum: Int): Flowable<RankingListResult>
}