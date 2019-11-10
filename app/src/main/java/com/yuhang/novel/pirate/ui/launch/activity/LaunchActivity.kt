package com.yuhang.novel.pirate.ui.launch.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.umeng.analytics.MobclickAgent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.main.activity.MainActivity
import com.yuhang.novel.pirate.ui.main.activity.checkVersionWithPermissionCheck
import com.yuhang.novel.pirate.ui.settings.activity.PrivacyActivity
import com.yuhang.novel.pirate.utils.StatusBarUtil
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * 启动页
 */
class LaunchActivity : AppCompatActivity() {

    companion object {
        const val MAX_TIME: Long = 0
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
//        StatusBarUtil.setColor(this, ContextCompat.getColor(this, android.R.color.white),
//                StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA
//        )

        ImmersionBar.with(this)
            .transparentStatusBar()
            .hideBar(BarHide.FLAG_HIDE_BAR)
//            .fullScreen(false)
            .init()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_launch)



        Flowable.timer(MAX_TIME, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //第一次安装弹出隐私条款
//                if (!PreferenceUtil.getBoolean("privacy", false)) {
//                    Handler().postDelayed({ showPrivacyDialog() }, 1500)
//                } else {
//                    startActivity(Intent(this, MainActivity::class.java))
//                    finish()
//                }

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }




    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("启动页")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("启动页")
    }

    private fun showPrivacyDialog() {
        MaterialDialog(this).show {
            title(text = "隐私政策")
            message(text =
            "本应用会按照本隐私权政策的规定使用和披露您的个人信息。\n\n但本应用将以高度的勤勉、审慎义务对待这些信息。除本隐私权政策另有规定外，在未征得您事先许可的情况下，本应用不会将这些信息对外披露或向第三方提供。\n\n本应用会不时更新本隐私权政策。\n \n您在同意本应用服务使用协议之时，即视为您已经同意本隐私权政策全部内容"
            )
            positiveButton(text = "确定", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    PreferenceUtil.commitBoolean("privacy", true)
                    p1.dismiss()
                    startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
                    finish()
                }
            })
        }
    }
}