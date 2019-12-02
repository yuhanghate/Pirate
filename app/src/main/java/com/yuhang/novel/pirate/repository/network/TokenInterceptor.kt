package com.yuhang.novel.pirate.repository.network

import com.yuhang.novel.pirate.app.PirateApp
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
            ?.header("token", PirateApp.getInstance().getToken())
//            ?.addHeader("Keep-Alive", "300")
//            ?.addHeader("Connection", "Keep-Alive")
//            ?.addHeader("Cache-Control", "no-cache")
//            ?.addHeader("Accept", "*/*")
//            ?.addHeader("Accept-Encoding", "gzip, deflate")
//            ?.addHeader("Accept-Language", "zh-CN,en-US;q=0.8,en;q=0.6")
//            ?.addHeader("Content-Type", "application/json;charset=gbk")
            ?.build()

        // Request customization: add request headers

        return chain?.proceed(originalHttpUrl!!)!!

    }

}