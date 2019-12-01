package com.yuhang.novel.pirate.extension

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
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

    val url = "http://appbdimg.cdn.bcebos.com/BookFiles/BookImages/${URLEncoder.encode(conver, "utf-8")}"
    Logger.t("cover").i("$url")
    return url
}

fun String.niceCoverPic(): String {
    if (this.startsWith("https://") || this.startsWith("http://")) {
        return this
    }

    val url = "http://appbdimg.cdn.bcebos.com/BookFiles/BookImages/${URLEncoder.encode(this, "utf-8")}"
    Logger.t("cover").i("$url")
    return url
}

fun Any.niceGlideInto(view: ImageView):CustomTarget<Drawable> {
    val value = object : CustomTarget<Drawable>() {
        override fun onLoadCleared(placeholder: Drawable?) {
        }

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            resource.setColorFilter(ContextCompat.getColor(view.context, R.color.image_tint), PorterDuff.Mode.DST_IN)
            view.setImageDrawable(resource)
        }

    }
    return value
}

/**
 * 200毫秒 动画
 */
fun Any.niceCrossFade(): DrawableTransitionOptions {
    val drawableCrossFadeFactory =
        DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build()
    return DrawableTransitionOptions.with(drawableCrossFadeFactory)
}

/**
 * Glide占位图 竖
 */
fun Any.niceDefaultImageVertical():RequestOptions {
    return RequestOptions()
        .placeholder(R.drawable.ic_default_img2)
        .error(R.drawable.ic_default_img2)
}

/**
 * Glide占位图 竖
 */
fun Any.niceDefaultAvatar():RequestOptions {
    return RequestOptions()
        .placeholder(R.color.divider)
        .error(R.color.divider)
}