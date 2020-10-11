@file:JvmName("ContextUtil")

package com.yuhang.novel.pirate.utils

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.os.Build
import android.view.Display
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.yuhang.novel.pirate.app.PirateApp


private class GlobalContext(context: Context) : ContextWrapper(context)


private lateinit var sApplication: PirateApp
private lateinit var sGlobalContext: GlobalContext


fun initContext(application: PirateApp) {
    if (!::sApplication.isInitialized) {
        sApplication = application
    }
    if (!::sGlobalContext.isInitialized) {
        sGlobalContext = GlobalContext(application.applicationContext)
    }
}


val context: Context
    get() = sGlobalContext


val application: PirateApp
    get() = sApplication


/* dp/px 相关工具 */
private val density by lazy {
    context.resources.displayMetrics.density
}
/* from dp to px */
val Int.dp: Int
    get() = (density * this + 0.5f).toInt()

val Float.dp: Float
    get() = density * this + 0.5f

/* from px to dp */
val Int.toDp: Int
    get() = (this / density + 0.5f).toInt()

val Float.toDp: Float
    get() = this / density + 0.5f


/* sp/px 相关工具 */
private val scaledDensity by lazy {
    context.resources.displayMetrics.scaledDensity
}

/* from sp to px */
val Int.sp: Int
    get() = (scaledDensity * this + 0.5f).toInt()

val Float.sp: Float
    get() = scaledDensity * this + 0.5f

/* from px to sp */
val Int.toSp: Float
    get() = this / scaledDensity + 0.5f

val Float.toSp: Float
    get() = this / scaledDensity + 0.5f

/* Toast 相关 */

private val globalToast by lazy {
    Toast.makeText(context, null, Toast.LENGTH_SHORT)
}

/**
 * 及时的、尽快的显示一个Toast，多次调用此方法的Toast会被后调用的覆盖
 * @param text String 要显示的文本
 * @return Toast 单例的Toast对象
 */
fun timelyToast(text: String): Toast {
    globalToast.duration = Toast.LENGTH_SHORT
    globalToast.setText(text)
    globalToast.show()
    return globalToast
}

/**
 * 同上，但是显示的时间稍长一点
 */
fun timelyLongToast(text: String): Toast {
    globalToast.duration = Toast.LENGTH_LONG
    globalToast.setText(text)
    globalToast.show()
    return globalToast
}

inline fun <reified T : Context> T.toast(text: String): Toast {
    return Toast.makeText(this, text, Toast.LENGTH_SHORT).apply(Toast::show)
}

inline fun <reified T : Context> T.longToast(text: String): Toast {
    return Toast.makeText(this, text, Toast.LENGTH_LONG).apply(Toast::show)
}

/**
 * 获取Color
 */
inline fun <reified T:Context> T.getColorCompat(@ColorRes colorInt: Int): Int =
    ContextCompat.getColor(this, colorInt)

inline fun <reified T : Context> T.getDisplay(): Display {
    val windowManager = this.getSystemService(
        Context.WINDOW_SERVICE
    ) as (WindowManager)
    return windowManager.defaultDisplay
}

/**
 * 获取屏幕宽度
 */
@RequiresApi(Build.VERSION_CODES.R)
inline fun <reified T : Context> T.getScreenRealWidth():Int {
    val display = this.display
    val outSize = Point()
    display?.getRealSize(outSize)
    return outSize.x
}