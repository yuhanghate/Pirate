package com.yuhang.novel.pirate.repository.api

import com.yuhang.novel.pirate.repository.network.data.kanshu.result.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
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

    //笔趣阁 精选  https://quapp.anchengcn.com/v5/base/lady.html
    //看书神器 精选 https://appbdsc.cdn.bcebos.com/v6/base/man.html

    /**
     * 看书站内搜索
     */
    @GET("https://souxs.pigqq.com/search.aspx")
    suspend fun searchBook(
        @Query("key") key: String, @Query("page") page: Int = 1, @Query("siteid") siteid: String = "app2"
    ): BookSearchResult

    /**
     * 男生 -> 分类
     */
    @GET("https://scxs.pigqq.com/BookCategory.html")
    suspend fun getBookCategory(): BookCategoryResult

    /**
     * 书本详情
     * dirId = (bookId - 后三位) + 1
     * bookId: 书本id
     */
    @GET("https://infosxs.pigqq.com/BookFiles/Html/{dirId}/{bookId}/info.html")
    suspend fun getBookDetails(
        @Path("dirId") dirId: Int, @Path(
            "bookId"
        ) bookId: Long
    ): BookDetailsResult

    /**
     * 书本章节目录
     */
    @Headers("Content-Type: application/json")
    @GET("https://contentxs.pigqq.com/BookFiles/Html/{dirId}/{bookId}/index.html")
    suspend fun getBookChapterList(
        @Path("dirId") dirId: Int, @Path(
            "bookId"
        ) bookId: Long
    ): String

    /**
     * 获取章节内容
     */
    @GET("https://contentxs.pigqq.com/BookFiles/Html/{dirId}/{bookId}/{chapterId}.html")
    suspend fun getChapterContent(
        @Path("dirId") dirId: Int, @Path(
            "bookId"
        ) bookId: Long, @Path("chapterId") chapterId: String
    ): ContentResult


    /**
     * 下载章节内容
     * 不知道为什么要区分,可能这个域名是CDN
     */
    @GET("https://contentxs.pigqq.com/BookFiles/Html/{dirId}/{bookId}/{chapterId}.html")
    fun downloadChapterContent(
        @Path("dirId") dirId: Int, @Path(
            "bookId"
        ) bookId: Long, @Path("chapterId") chapterId: String
    ): Call<ContentResult>

    /**
     * 下载章节内容
     * 不知道为什么要区分,可能这个域名是CDN
     */
    @GET("https://contentxs.pigqq.com/BookFiles/Html/{dirId}/{bookId}/{chapterId}.html")
    fun downloadNovel(
        @Path("dirId") dirId: Int, @Path(
            "bookId"
        ) bookId: Long, @Path("chapterId") chapterId: String
    ): Call<ContentResult>




    /**
     * 排行榜
     * gender >  man:男的  lady:女生
     * type -> hot:最热  commend:推荐  over:完结  collect:收藏  new:新书  vote:评分
     * date -> week:周榜 month:月榜  total:总榜
     * pageNum : 分页
     */
    @GET("https://scxs.pigqq.com/top/{gender}/top/{type}/{date}/{pageNum}.html")
    suspend fun getRankingList(
        @Path("gender") gender: String, @Path("type") type: String, @Path("date") date: String, @Path(
            "pageNum"
        ) pageNum: Int
    ): RankingListResult

    /**
     * 书城 -> 男生
     */
    @GET("https://scxs.pigqq.com/v6/base/man.html")
    suspend fun getStoreMan(): StoreManResult

    /**
     * 书城 -> 女生
     */
    @GET("https://scxs.pigqq.com/v6/base/lady.html")
    suspend fun getStoreLady(): StoreManResult

    /**
     * 书城 -> 榜单 -> 男生
     */
    @GET("https://scxs.pigqq.com/top/man/index.html")
    suspend fun getStoreRankingMan(): StoreRankingResult

    /**
     * 书城 -> 榜单 -> 女生
     */
    @GET("https://scxs.pigqq.com/top/lady/index.html")
    suspend fun getStoreRankingLady(): StoreRankingResult

    /**
     * 获取书单
     *
     * 最新发布/本周最热/最多收藏/小编推荐
     */
    @GET("https://scxs.pigqq.com/shudan/{gender}/all/{type}/{pageNum}.html")
    suspend fun getBooksList(@Path("gender") gender: String, @Path("type") type: String, @Path("pageNum") pageNum: String): BooksListResult

    /**
     * 正版排行榜
     *
     * 起点/纵横/去起/若初/红薯/潇湘/逐浪
     */
    @GET("https://scxs.pigqq.com/top/{gender}/more/{type}/{pageNum}.html")
    suspend fun getMoreRankingList(@Path("gender") gender: String, @Path("type") type: Int, @Path("pageNum") pageNum: String):MoreRankingResult

    /**
     * 看书神器 排行榜
     */
    @GET("https://scxs.pigqq.com/top/{gender}/top/{type}/{date}/{pageNum}.html")
    suspend fun getKanShuRankingList(@Path("gender") gender:String, @Path("type") type:String, @Path("date") date:String, @Path("pageNum") pageNum:Int) : KanShuRankingResult

    /**
     * 书单详情
     */
    @GET("https://scxs.pigqq.com/shudan/detail/{id}.html")
    suspend fun getBookListDetail(@Path("id") id:String):ShuDanDetailResult
}