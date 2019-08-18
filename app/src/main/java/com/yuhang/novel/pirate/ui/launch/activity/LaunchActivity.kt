package com.yuhang.novel.pirate.ui.launch.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.content.ContextCompat
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.ui.main.activity.MainActivity
import com.yuhang.novel.pirate.utils.StatusBarUtil
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit


/**
 * 启动页
 */
class LaunchActivity : AppCompatActivity() {

    companion object {
        const val MAX_TIME: Long = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, android.R.color.white),
                StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA
        )

        super.onCreate(savedInstanceState)

//        setContentView(R.layout.activity_launch)

        if (savedInstanceState == null) {
            val inflater = AsyncLayoutInflater(this);
            inflater.inflate(R.layout.activity_launch, null, callback)
        } else {
            val view = layoutInflater.inflate(R.layout.activity_launch, null)
            callback.onInflateFinished(view, R.layout.activity_launch, null)
        }


    }

    val callback: AsyncLayoutInflater.OnInflateFinishedListener = AsyncLayoutInflater.OnInflateFinishedListener { view, resid, parent ->
        // setup here

        setContentView(view)
        Flowable.timer(MAX_TIME, TimeUnit.SECONDS)
                .subscribe {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

    }
}