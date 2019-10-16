package com.yuhang.novel.pirate.repository.api

import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.BookDetailsKdResult
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.SearchKdResult
import io.reactivex.Flowable
import org.intellij.lang.annotations.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface KuaiDuNetApi {

    //搜索 http://api.gdugm.cn/book/search?key=%E5%A4%A9%E8%9A%95%E5%9C%9F%E8%B1%86&start=0&limit=100


    /**
     * 搜索
     */
    @GET("http://api.gdugm.cn/book/search")
    fun search(@QueryMap map:Map<String,Any>):Flowable<SearchKdResult>

    /**
     * 书籍详情页
     */
    @GET("http://api.wgfgr.cn/book/info")
    fun getBookDetails(@QueryMap map: Map<String, Any>):Flowable<BookDetailsKdResult>

    /**
     * 同类推荐
     */
    @GET("http://api.wgfgr.cn/book/{bookid}/recommend")
    fun getRecommendBook(@Path("bookid") bookid:String)
}