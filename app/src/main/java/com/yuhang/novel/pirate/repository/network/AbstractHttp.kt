package com.yuhang.novel.pirate.repository.network

import com.yuhang.novel.pirate.BuildConfig
import com.yuhang.novel.pirate.repository.network.interceptor.AddHeaderInterceptor
import com.yuhang.novel.pirate.utils.SSLSocketClient
import com.yuhang.novel.pirate.utils.application
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.File
import java.io.InputStream
import java.net.Proxy
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


abstract class AbstractHttp {

    /**
     * protocol + host + port
     */
    protected abstract val baseUrl: String

    /**
     * Headers
     */
    protected open val header: Map<String, String> = emptyMap()

    /**
     * 超时秒数
     */
    protected open val timeout = 60L

    /**
     * OkHttp的拦截器
     */
    protected open val interceptors: Iterable<Interceptor> = emptyList()

    /**
     * CallAdapter转换器
     */
    protected open val callAdapterFactories: Iterable<CallAdapter.Factory> = emptyList()

    /**
     * Converter转换器
     */
    protected open val convertersFactories: Iterable<Converter.Factory> = emptyList()

    /**
     * Https密钥
     */
    protected open val keyStore: InputStream? = null

    /**
     * Https密码
     */
    protected open val keyStorePassword: String? = null

    /**
     * Https证书
     */
    protected open val certificates: Array<InputStream> = emptyArray()

    /**
     * 最大缓存:10G
     */
    protected open val maxSize = 10 * 1024L * 1024L * 1204L

    /**
     * Okhttp缓存文件夹名称
     */
    protected open val cacheDir = "91Video"

    /**
     * 分页配置文件
     */
//    protected open  val pagingConfig = PagingConfig(
//        // 每页显示的数据的大小
//        pageSize = 20,
//
//        // 开启占位符
//        enablePlaceholders = false,
//
//        // 预刷新的距离，距离最后一个 item 多远时加载数据
//        prefetchDistance = 10,
//
//
//        /**
//         * 初始化加载数量，默认为 pageSize * 3
//         *
//         * internal const val DEFAULT_INITIAL_PAGE_MULTIPLIER = 3
//         * val initialLoadSize: Int = pageSize * DEFAULT_INITIAL_PAGE_MULTIPLIER
//         */
//
//        initialLoadSize = 60,
//
//        // 一次应在内存中保存的最大数据
//        maxSize = 400
//    )


    val retrofit by lazy(LazyThreadSafetyMode.SYNCHRONIZED, ::createRetrofit)

    val okHttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED, ::createOkHttpClient)

    val okGlide by lazy(LazyThreadSafetyMode.SYNCHRONIZED, ::createOkHttpClient)

    /**
     * 默认的构建Retrofit的方法，若无法满足需求，请重写此方法
     */
    protected open fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .apply {
                convertersFactories.map(::addConverterFactory)
                callAdapterFactories.map(::addCallAdapterFactory)
            }
            .client(okHttpClient)
            .build()
    }

     fun createRetrofit(url: String):Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .apply {
                convertersFactories.map(::addConverterFactory)
                callAdapterFactories.map(::addCallAdapterFactory)
            }
            .client(okHttpClient)
            .build()
    }

    /**
     * 根据已有的Retrofit构建一个新的Retrofit
     */
    fun createRetrofit(build: Retrofit.Builder.() -> Unit): Retrofit {
        return retrofit.newBuilder().also(build).build()
    }

    /**
     * 默认的构建OkHttpClient的方法，若无法满足需求，请重写此方法
     */
    protected open fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .dns(HttpDns())
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .addInterceptor(AddHeaderInterceptor(header))
            .retryOnConnectionFailure(true)
            .cache(Cache(File(application.cacheDir, cacheDir), maxSize))
            .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
            .sslSocketFactory(
                SSLSocketClient.getSSLSocketFactory(),
                SSLSocketClient.createTrustAllManager()
            )
            .proxy(if (BuildConfig.DEBUG) null else Proxy.NO_PROXY)//正式版本不走代理
            .apply {
                interceptors.map(::addInterceptor)
            }.build()


    }

    /**
     * 根据已有的OkHttpClient构建一个新的OkHttpClient
     */
    fun createOkHttpClient(build: OkHttpClient.Builder.() -> Unit): OkHttpClient {
        return okHttpClient.newBuilder().also(build).build()
    }

    fun OkHttpClient.Builder.installHttpsCertificates(): OkHttpClient.Builder {
        if (certificates.isEmpty()) return this
        val x509TrustManager: X509TrustManager = prepareTrustManager(*certificates).fetch()
        val keyManagers = prepareKeyManager(keyStore, keyStorePassword)
        val sslContext = SSLContext.getInstance("TLS")
        val trustManager = X509TrustManagerImpl(x509TrustManager)
        sslContext.init(keyManagers, arrayOf<TrustManager>(trustManager), null)
        return sslSocketFactory(sslContext.socketFactory, x509TrustManager)
    }

    private fun prepareKeyManager(keyInput: InputStream?, password: String?): Array<KeyManager>? {
        keyInput ?: return null
        password ?: return null
        val keyStore = KeyStore.getInstance("BKS")
        keyStore.load(keyInput, password.toCharArray())
        val keyManagerFactory =
            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, password.toCharArray())
        return keyManagerFactory.keyManagers
    }

    private fun prepareTrustManager(vararg certificates: InputStream): Array<TrustManager> {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null)
        certificates.forEachIndexed { index, certificate ->
            certificate.use {
                val certificateAlias = index.toString()
                keyStore.setCertificateEntry(
                    certificateAlias,
                    certificateFactory.generateCertificate(it)
                )
            }
        }
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        return trustManagerFactory.trustManagers
    }

    private inline fun <reified T> Array<*>.fetch(): T = first { it is T } as T

    private inner class X509TrustManagerImpl(private val localTrustManager: X509TrustManager) :
        X509TrustManager {

        private val defaultTrustManager: X509TrustManager

        init {
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            @Suppress("CAST_NEVER_SUCCEEDS")
            trustManagerFactory.init(null as? KeyStore)
            defaultTrustManager = trustManagerFactory.trustManagers.fetch()
        }

        override fun checkClientTrusted(chain: Array<out X509Certificate>, authType: String?) {
            println("checkClientTrusted\tchain:${chain.contentToString()}\tauthType:$authType")
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>, authType: String?) {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType)
            } catch (ce: CertificateException) {
                localTrustManager.checkServerTrusted(chain, authType)
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }

}