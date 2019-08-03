package com.yuhang.novel.pirate.repository.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/03/10
 * desc   : 增加头部分信息
 */
class TokenInterceptor : Interceptor {
    val token: String = ""
    override fun intercept(chain: Interceptor.Chain?): Response {

        val original = chain?.request()

        val originalHttpUrl = original?.newBuilder()
//                ?.header("source", "2")
//                ?.header("channel", "android")
//                ?.header("application", "yyx")
//                ?.header("Accept", "application/json")
                ?.build()

        // Request customization: add request headers


        return chain?.proceed(originalHttpUrl)!!

    }

}