package com.yuhang.novel.pirate.repository.network

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.yuhang.novel.pirate.BuildConfig
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.repository.api.KanShuNetApi
import com.yuhang.novel.pirate.repository.api.KuaiDuNetApi
import com.yuhang.novel.pirate.repository.api.NetApi
import com.yuhang.novel.pirate.utils.SSLSocketClient
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.net.InetAddress
import java.net.Proxy
import java.net.Socket
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


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

    private val mKuaiDuNetApi: KuaiDuNetApi by lazy { createKuaiDuNetApi() }

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
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(mOkHttpClient).build()
            .create(NetApi::class.java)
    }

    /**
     * 看书神器
     */
    private fun createKanZhuNetApi(): KanShuNetApi {
        return Retrofit.Builder().baseUrl(NetURL.HOST_KANSHU)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(mGson))
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(mOkHttpClient).build()
            .create(KanShuNetApi::class.java)
    }

    /**
     * 快读
     */
    private fun createKuaiDuNetApi(): KuaiDuNetApi {
        return Retrofit.Builder().baseUrl(NetURL.HOST_KUAIDU)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(mGson))
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(mOkHttpClient).build()
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
//        val localhost: String = InetAddress.getByName("localhost").getCanonicalHostName()
//        val localhostCertificate: HeldCertificate = HeldCertificate.Builder()
//            .addSubjectAlternativeName(localhost)
//            .build()
//
//        val clientCertificates: HandshakeCertificates = HandshakeCertificates.Builder()
//            .addTrustedCertificate(localhostCertificate.certificate)
//            .build()

        val builder = OkHttpClient().newBuilder()
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            //缓存1g
            .cache(Cache(File(PirateApp.getInstance().cacheDir, "priate"), 1024 * 1024 * 1204))
            .dns(HttpDns())
            //增加Header头
            .addInterceptor(TokenInterceptor())
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(15L, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
//            .sslSocketFactory(clientCertificates.sslSocketFactory(),
//                clientCertificates.trustManager
//            )
            .sslSocketFactory(
                SSLSocketClient.getSSLSocketFactory(),
                SSLSocketClient.createTrustAllManager()
            )
            //日志拦截器
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            )

//        //忽略ssl证书,android10及以上的版本就不用了
//        if (Build.VERSION.SDK_INT < 29) {
//            builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(),SSLSocketClient.createTrustAllManager())
//        }

        if (!BuildConfig.DEBUG) {
            //不能代理
            return builder.proxy(Proxy.NO_PROXY).build()
        }
        return builder.build()
    }


}