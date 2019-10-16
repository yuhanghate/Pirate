package com.yuhang.novel.pirate.repository.network

import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * 第三方网站源爬虫
 */
interface ResouceNetApi {

    @FormUrlEncoded
    @POST
    fun postMap(
        @Url url: String,
        @FieldMap(encoded = true) fieldMap: Map<String, String>,
        @HeaderMap headers: Map<String, String>
    ): Flowable<String>

    @POST
    abstract fun postJson(
        @Url url: String,
        @Body body: RequestBody,
        @HeaderMap headers: Map<String, String>
    ): Flowable<String>

    @GET
    abstract operator fun get(
        @Url url: String,
        @HeaderMap headers: Map<String, String>
    ): Flowable<String>

    @GET
    abstract fun getMap(
        @Url url: String,
        @QueryMap(encoded = true) queryMap: Map<String, String>,
        @HeaderMap headers: Map<String, String>
    ): Flowable<String>
}