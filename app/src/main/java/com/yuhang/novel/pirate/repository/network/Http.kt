package com.yuhang.novel.pirate.repository.network

import com.tamsiree.rxkit.RxDeviceTool
import com.yuhang.novel.pirate.utils.application
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


object Http : AbstractHttp() {

    override val baseUrl: String = NetURL.HOST

    override val header: Map<String, String>
        get() = mapOf("Accept-Language" to "zh-cn,zh;q=0.8",
            "X-Device-Id" to RxDeviceTool.getAndroidId(
                application),
        "User-Agent" to System.getProperty("http.agent"))

    override val convertersFactories: Iterable<Converter.Factory> = listOf(
        ScalarsConverterFactory.create(),
        GsonConverterFactory.create()
    )

    override val callAdapterFactories: Iterable<CallAdapter.Factory>
        get() = listOf(
//            RxJava2CallAdapterFactory.create()
//            LiveDataCallAdapterFactory.create()
        )

    override val interceptors: Iterable<Interceptor>
        get() {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            return arrayListOf(httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            })
        }

//    public override val pagingConfig: PagingConfig
//        get() = super.pagingConfig
//
//    /**
//     * 不预加载
//     */
//    public val pagingConfigOnePage: PagingConfig = PagingConfig(
//        // 每页显示的数据的大小
//        pageSize = 20
//    )
}