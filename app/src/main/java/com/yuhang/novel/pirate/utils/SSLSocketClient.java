package com.yuhang.novel.pirate.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import com.yuhang.novel.pirate.constant.ConfigConstant;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GKF on 2018/3/1.
 * 忽略证书
 */

public class SSLSocketClient {
    //获取这个SSLSocketFactory
    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{createTrustAllManager()}, new SecureRandom());

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static X509TrustManager createTrustAllManager() {
        if (Build.VERSION.SDK_INT < 29) {
            return new MyX509();
        }
        return new MyX509N();
    }


    //获取HostnameVerifier
    public static HostnameVerifier getHostnameVerifier() {
        return (s, sslSession) -> {
//            for (String hosts : ConfigConstant.INSTANCE.getHTTPS_HOSTS()) {
//                if (s.equals(hosts)) {
//                    return true;
//                }
//            }
//            return false;

            return true;
        };
    }
}
