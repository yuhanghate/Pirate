package com.yuhang.novel.pirate.service.impl

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.yuhang.novel.pirate.service.ReadAloudService

/**
 * 朗读模块
 */
class ReadAloudServiceImpl : Service(), ReadAloudService {
    override fun onBind(intent: Intent?): IBinder? {
        return builder
    }

    val builder :Binder  = object : Binder(){

    }

    override fun start() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun stop() {
    }
}