package com.yuhang.novel.pirate.ui.settings.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.yuhang.novel.pirate.R

class TestBarActivity:AppCompatActivity() {

    companion object{
        fun start(context: Context) {
            val intent = Intent(context, TestBarActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_bar)
    }
}