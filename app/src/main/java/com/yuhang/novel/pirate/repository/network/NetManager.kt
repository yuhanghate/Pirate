package com.yuhang.novel.pirate.repository.network

import com.google.gson.Gson
import com.yuhang.novel.pirate.repository.api.KanShuNetApi
import com.yuhang.novel.pirate.repository.api.KuaiDuNetApi
import com.yuhang.novel.pirate.repository.api.NetApi
import com.yuhang.novel.pirate.repository.network.adapter.LiveDataCallAdapterFactory
import com.yuhang.novel.pirate.repository.network.rule.AnalyzeUrl
import com.yuhang.novel.pirate.utils.EncodeConverter
import com.yuhang.novel.pirate.utils.SSLSocketClient
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/03/08
 * desc   : 网络管理
 */
class NetManager {

    private val mOkHttpClient: OkHttpClient by lazy { createOkhttp() }

//    private val mRetrofit: Retrofit by lazy { createRetrofit2() }

    private val mNetApi: NetApi by lazy { createNetApi() }

    private val mZhuiShuApi: KanShuNetApi by lazy { createKanZhuNetApi() }

    private val mKuaiDuNetApi : KuaiDuNetApi by lazy { createKuaiDuNetApi() }

    private val mGson: Gson by lazy { Gson() }

    fun getGson() = mGson

//    fun getRetrofit() = mRetrofit

    fun getOkHttpClient() = mOkHttpClient

    fun getNetApi() = mNetApi

    fun getKanShuApi() = mZhuiShuApi

    fun getKuaiDuApi() = mKuaiDuNetApi

    private fun createNetApi(): NetApi {
        return Retrofit.Builder().baseUrl(NetURL.HOST)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(mGson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(mOkHttpClient).build()
            .create(NetApi::class.java)
    }

    /**
     * 看书神器
     */
    private fun createKanZhuNetApi(): KanShuNetApi {
        return Retrofit.Builder().baseUrl(NetURL.HOST_KANSHU)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(mGson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(mOkHttpClient).build()
            .create(KanShuNetApi::class.java)
    }

    /**
     * 快读
     */
    private fun createKuaiDuNetApi(): KuaiDuNetApi {
        return Retrofit.Builder().baseUrl(NetURL.HOST_KUAIDU)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(mGson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(mOkHttpClient).build()
            .create(KuaiDuNetApi::class.java)
    }


//    private fun createRetrofit2(): Retrofit {
//        return Retrofit.Builder().baseUrl(NetURL.HOST)
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create(mGson))
//            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(mOkHttpClient).build()
//    }

    private fun createOkhttp(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .dns(HttpDns())
            //增加Header头
            .addInterceptor(TokenInterceptor())
            .connectTimeout(60 * 5, TimeUnit.SECONDS)
            .readTimeout(60 * 5, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
            .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.createTrustAllManager())
            //日志拦截器
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            )
//
            .build()
    }

    private fun getHostnameVerifier(): HostnameVerifier {
        return HostnameVerifier { _, _ -> true }
    }


    private fun createCertificates(): SSLSocketFactory? {
        var sSLSocketFactory: SSLSocketFactory? = null

        val manager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }
        }

        try {
            var sc = SSLContext.getInstance("TLS")
            val array = arrayOf(manager)
            sc.init(null, array, SecureRandom())
            sSLSocketFactory = sc.socketFactory
        } catch (e: Exception) {
        }

        return sSLSocketFactory

    }

    /**
     * 获取resouce
     */
    fun getResponseO(analyzeUrl: AnalyzeUrl): Flowable<String> {
        when (analyzeUrl.getUrlMode()) {
            AnalyzeUrl.UrlMode.POST -> return getRetrofitString(analyzeUrl.getHost())
                .create(ResouceNetApi::class.java)
                .postMap(
                    analyzeUrl.getPath(),
                    analyzeUrl.getQueryMap(),
                    analyzeUrl.getHeaderMap()
                )
            AnalyzeUrl.UrlMode.GET -> return getRetrofitString(analyzeUrl.getHost())
                .create(ResouceNetApi::class.java)
                .getMap(
                    analyzeUrl.getPath(),
                    analyzeUrl.getQueryMap(),
                    analyzeUrl.getHeaderMap()
                )
            else -> return getRetrofitString(analyzeUrl.getHost())
                .create(ResouceNetApi::class.java)
                .get(
                    analyzeUrl.getPath(),
                    analyzeUrl.getHeaderMap()
                )
        }
    }

    fun getRetrofitString(url: String): Retrofit {
        return Retrofit.Builder().baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            //增加返回值为字符串的支持(以实体类返回)
            .addConverterFactory(EncodeConverter.create())
            //增加返回值为Observable<T>的支持
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(createOkhttp())
            .build()
    }

    fun getRetrofitString(url: String, encode: String): Retrofit {
        return Retrofit.Builder().baseUrl(url)
            //增加返回值为字符串的支持(以实体类返回)
            .addConverterFactory(EncodeConverter.create(encode))
            //增加返回值为Observable<T>的支持
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(createOkhttp())
            .build()
    }



}