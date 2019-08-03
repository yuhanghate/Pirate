package com.yuhang.novel.pirate.extension

import android.content.Context
import androidx.fragment.app.Fragment

fun Context.niceDp2px(dpValue: Float): Int {
  val scale = resources.displayMetrics.density
  return (dpValue * scale + 0.5f).toInt()
}

fun Context.nicePx2dp(pxValue: Float): Int {
  val scale = resources.displayMetrics.density
  return (pxValue / scale + 0.5f).toInt()
}

fun Context.niceSp2px(spValue: Float): Int {
  val scale = resources.displayMetrics.scaledDensity
  return (spValue * scale + 0.5f).toInt()
}

fun Context.nicePx2sp(pxValue: Float): Int {
  val scale = resources.displayMetrics.scaledDensity
  return (pxValue / scale + 0.5f).toInt()
}


fun Fragment.niceDp2px(dpValue: Float): Int {
  val scale = resources.displayMetrics.density
  return (dpValue * scale + 0.5f).toInt()
}

fun Fragment.nicePx2dp(pxValue: Float): Int {
  val scale = resources.displayMetrics.density
  return (pxValue / scale + 0.5f).toInt()
}

fun Fragment.niceSp2px(spValue: Float): Float {
  val scale = resources.displayMetrics.scaledDensity
  return (spValue * scale + 0.5f)
}

fun Fragment.nicePx2sp(pxValue: Float): Float {
  val scale = resources.displayMetrics.scaledDensity
  return (pxValue / scale + 0.5f)
}