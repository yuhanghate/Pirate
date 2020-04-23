package com.yuhang.novel.pirate.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.yuhang.novel.pirate.extension.niceAvatarUrl
import com.yuhang.novel.pirate.extension.niceToast

object IntentHelp {


    fun toTTSSetting(context: Context) {
        //跳转到文字转语音设置界面
        try {
            val intent = Intent()
            intent.action = "com.android.settings.TTS_SETTINGS"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (ignored: Exception) {
            context.niceToast("无法跳转到设置界面")
        }
    }

    fun toInstallUnknown(context: Context) {
        try {
            val intent = Intent()
            intent.action = "android.settings.MANAGE_UNKNOWN_APP_SOURCES"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (ignored: Exception) {
            context.niceToast("无法打开设置")
        }
    }

    inline fun <reified T> servicePendingIntent(context: Context, action: String): PendingIntent? {
        return PendingIntent.getService(
            context,
            0,
            Intent(context, T::class.java).apply { this.action = action },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    inline fun <reified T> activityPendingIntent(context: Context, action: String): PendingIntent? {
        return PendingIntent.getActivity(
            context,
            0,
            Intent(context, T::class.java).apply { this.action = action },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}