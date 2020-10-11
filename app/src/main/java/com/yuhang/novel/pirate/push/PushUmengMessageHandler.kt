package com.yuhang.novel.pirate.push

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.umeng.message.UmengMessageHandler
import com.umeng.message.entity.UMessage
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.repository.database.entity.PushMessageEntity
import com.yuhang.novel.pirate.ui.main.activity.MainActivity
import com.yuhang.novel.pirate.utils.application
import kotlinx.coroutines.*
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

        context ?: return
        message ?: return

        val msg = getCustomPush(message) ?: return
        GlobalScope.launch(start = CoroutineStart.UNDISPATCHED) {
            coroutineScope{
                //async 函数启动新的协程
                application.getDataRepository().insert(PushMessageEntity().apply {
                    this.message = msg.message
                    this.title = msg.title
                    this.type = msg.type
                    this.hasRead = 0
                    this.extra = ""
                })
            }

        }


    }

    /**
     * 自定义消息
     */
    private fun getCustomPush(message: UMessage): CustomPush? {
        if (TextUtils.isEmpty(message.custom)) return null
        return Gson().fromJson<CustomPush>(message.custom, CustomPush::class.java)
    }
}