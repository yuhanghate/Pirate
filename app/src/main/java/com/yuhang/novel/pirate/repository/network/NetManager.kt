package com.yuhang.novel.pirate.repository.network

import com.google.gson.Gson
import com.netease.nim.demo.koltinapplication.repository.network.NetURL
import com.yuhang.novel.pirate.repository.network.adapter.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
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

    private val mRetrofit: Retrofit by lazy { createRetrofit2() }

    private val mNetApi: NetApi by lazy { createNetApi() }

    private val mZhuiShuApi:KanShuNetApi by lazy { createKanZhuNetApi() }

    private val mGson: Gson by lazy { Gson() }

    fun getGson() = mGson

    fun getRetrofit() = mRetrofit

    fun getOkHttpClient() = mOkHttpClient

    fun getNetApi() = mNetApi

    fun  getKanShuApi() = mZhuiShuApi

    private fun createNetApi(): NetApi {
        return mRetrofit.create(NetApi::class.java)
    }

    /**
     * 看书神器
     */
    private fun createKanZhuNetApi(): KanShuNetApi {
        return Retrofit.Builder().baseUrl(NetURL.HOST_KANSHU)
            .addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(GsonConverterFactory.create(mGson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(mOkHttpClient).build().create(KanShuNetApi::class.java)
    }


    private fun createRetrofit2(): Retrofit {
        return Retrofit.Builder().baseUrl(NetURL.HOST)
                .addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(mOkHttpClient).build()
    }

    private fun createOkhttp(): OkHttpClient {
        return OkHttpClient().newBuilder()
                //增加Header头
                .addInterceptor(TokenInterceptor())
                .connectTimeout(1000 * 15, TimeUnit.MILLISECONDS)
                .readTimeout(1000 * 15, TimeUnit.MILLISECONDS)
                .writeTimeout(1000 * 15, TimeUnit.MILLISECONDS)
            .hostnameVerifier(getHostnameVerifier()).sslSocketFactory(createCertificates())
                //日志拦截器
                .addInterceptor(
                        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
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
}