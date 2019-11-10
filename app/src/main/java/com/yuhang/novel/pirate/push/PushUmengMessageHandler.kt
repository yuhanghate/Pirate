package com.yuhang.novel.pirate.push

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.umeng.message.UmengMessageHandler
import com.umeng.message.entity.UMessage
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.repository.database.entity.PushMessageEntity
import com.yuhang.novel.pirate.ui.main.activity.MainActivity
import kotlin.concurrent.thread

/**
 * 推送
 */
class PushUmengMessageHandler : UmengMessageHandler() {

    override fun dealWithNotificationMessage(p0: Context?, p1: UMessage?) {
        super.dealWithNotificationMessage(p0, p1)
    }


    override fun dealWithCustomMessage(context: Context?, message: UMessage?) {
        super.dealWithCustomMessage(context, message)
        //自定义推送

        context?:return
        message?:return

        getCustomPush(message)?.let {obj ->
            thread {
                PirateApp.getInstance().getDataRepository().insert(PushMessageEntity().apply {
                    this.message = obj.message
                    this.title = obj.title
                    this.type = obj.type
                    this.hasRead = 0
                    this.extra = ""
                })
            }
        }


    }

    /**
     * 自定义消息
     */
    private fun getCustomPush(message: UMessage):CustomPush? {
        if (TextUtils.isEmpty(message.custom))return null
        return Gson().fromJson<CustomPush>(message.custom, CustomPush::class.java)
    }
}