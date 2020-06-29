package com.yuhang.novel.pirate.ui.webview.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yuhang.novel.pirate.databinding.ActivityWebviewBinding

class WebViewActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.webview.loadUrl("file:///android_asset/openapp.html")

    }

}