package com.yuhang.novel.pirate.ui.webview.activity

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.yuhang.novel.pirate.R

class WebViewActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        findViewById<WebView>(R.id.webview).loadUrl("file:///android_asset/openapp.html")
    }

}