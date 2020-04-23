package com.yuhang.novel.pirate.extension

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yuhang.novel.pirate.R
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip

fun Activity.niceToast(msg : String) {
    Toast.makeText(this,msg, Toast.LENGTH_LONG).show()
}

fun Fragment.niceToast(msg : String) {
    Toast.makeText(activity,msg, Toast.LENGTH_LONG).show()
}

fun Context.niceToast(msg : String) {
    Toast.makeText(this,msg, Toast.LENGTH_LONG).show()
}


fun Activity.niceTipTop(view:View, message:String) {
    Tooltip.Builder(this)
            .anchor(view, 0, 0, true)
            .text(message)
            .styleId(R.style.ToolTipAltStyle)
            .typeface(null)
            .maxWidth(resources.displayMetrics.widthPixels / 2)
            .arrow(true)
            .floatingAnimation(Tooltip.Animation.DEFAULT)
            .closePolicy(ClosePolicy.Builder().inside(false).outside(false).consume(false).build())
            .showDuration(2000)
            .overlay(false)
            .create()?.show(view, Tooltip.Gravity.TOP, true)
}

fun Fragment.niceTipTop(view:View, message:String) {
    Tooltip.Builder(context!!)
        .anchor(view, 0, 0, false)
        .text(message)
        .styleId(null)
        .typeface(null)
        .maxWidth(resources.displayMetrics.widthPixels / 2)
        .arrow(true)
        .floatingAnimation(Tooltip.Animation.DEFAULT)
        .closePolicy(ClosePolicy.Builder().inside(false).outside(false).consume(false).build())
        .showDuration(2000)
        .overlay(false)
        .create()?.show(view, Tooltip.Gravity.TOP, true)
}