package com.yuhang.novel.pirate.ui.launch.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
            .fullScreen(true)
            .navigationBarColor(R.color.md_white_1000)
            .init()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        AppManagerUtils.getAppManager().addActivity(this)


        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(
                R.anim.slide_in_transparent_launch,
                R.anim.slide_out_transparent_launch
            )
        },2000)

    }


}