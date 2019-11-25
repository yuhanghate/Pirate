package com.yuhang.novel.pirate.ui.launch.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.ui.main.activity.MainActivity
import com.yuhang.novel.pirate.utils.AppManagerUtils


/**
 * 启动页
 */
class LaunchActivity : AppCompatActivity() {

    companion object {
        const val MAX_TIME: Long = 0
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {

        ImmersionBar.with(this)
//            .transparentStatusBar()
//            .hideBar(BarHide.FLAG_HIDE_BAR)
            .fullScreen(true)
            .init()

        super.onCreate(savedInstanceState)


        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(
            R.anim.slide_in_transparent_launch,
            R.anim.slide_out_transparent_launch
        )
    }


}