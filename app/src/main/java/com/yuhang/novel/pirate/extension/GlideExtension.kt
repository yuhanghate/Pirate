package com.yuhang.novel.pirate.extension

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.app.PirateApp
import java.net.URLEncoder

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

//    val url = "https://imgapi.jiaston.com/BookFiles/BookImages/$conver"
    val url = "https://appbdimg.cdn.bcebos.com/BookFiles/BookImages/${URLEncoder.encode(conver, "utf-8")}"
    Logger.t("cover").i("$url")
    return url
}

fun Any.niceGlideInto(view: ImageView):CustomTarget<Drawable> {
    val value = object : CustomTarget<Drawable>() {
        override fun onLoadCleared(placeholder: Drawable?) {
        }

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            resource.setColorFilter(ContextCompat.getColor(view.context, R.color.image_tint), PorterDuff.Mode.DST_IN)
//                DrawableCompat.setTintMode(resource, PorterDuff.Mode.DARKEN)
            view.setImageDrawable(resource)
        }

    }
    return value
}