package com.yuhang.novel.pirate.extension

import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.app.PirateApp

fun BaseViewHolder<*, *>.niceAvatarUrl(avatar:String):String {
    var avatar = PirateApp.getInstance().getImgServer() + avatar
    avatar = avatar.replace("//", "/").replace(":/", "://")
    return avatar
}


fun Any.niceAvatarUrl(avatar:String):String {
    var avatar = PirateApp.getInstance().getImgServer() + avatar
    avatar = avatar.replace("//", "/").replace(":/", "://")
    return avatar
}

fun Any.niceCoverPic(conver: String):String {
    if (conver.startsWith("https://") || conver.startsWith("http://")) {
        return conver
    }

    return "https://imgapi.jiaston.com/BookFiles/BookImages/$conver"
}