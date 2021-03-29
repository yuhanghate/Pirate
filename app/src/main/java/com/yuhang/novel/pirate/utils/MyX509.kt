package com.yuhang.novel.pirate.utils

import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class MyX509 : X509TrustManager {

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }

    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
    }
}