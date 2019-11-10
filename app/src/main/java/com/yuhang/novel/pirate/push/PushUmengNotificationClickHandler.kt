package com.yuhang.novel.pirate.push

import android.content.Context
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.entity.UMessage

class PushUmengNotificationClickHandler: UmengNotificationClickHandler() {

    override fun openActivity(p0: Context?, p1: UMessage?) {
        super.openActivity(p0, p1)

    }
}