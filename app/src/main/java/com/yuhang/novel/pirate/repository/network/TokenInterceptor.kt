package com.yuhang.novel.pirate.repository.network

import com.tamsiree.rxkit.RxDeviceTool
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
    val deviceId = RxDeviceTool.getAndroidId(PirateApp.getInstance())
    val agent = System.getProperty("http.agent")?:""

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

//        val originalHttpUrl = original.newBuilder()
//            .header("token", PirateApp.getInstance().getToken())
//            .header("X-Device-Id", deviceId)
//            .header("User-Agent", agent)
//            .build()
//        return chain.proceed(originalHttpUrl)

        val originalHttpUrl = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("User-Agent", agent)

        if (original.url.host == "www.suibiankanshu.com") {
            originalHttpUrl.header("token", PirateApp.getInstance().getToken())

        }
        return chain.proceed(originalHttpUrl.build())

//        return chain.proceed(original)

        // Request customization: add request headers


    }

}