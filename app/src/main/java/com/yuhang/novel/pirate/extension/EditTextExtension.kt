package com.yuhang.novel.pirate.extension

import android.app.Activity
import android.widget.EditText

fun EditText.niceFocusable(activity: Activity) {
    this.isFocusable = true
    this.isFocusableInTouchMode = true
    this.requestFocus()
    //显示软键盘
//    activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    //如果上面的代码没有弹出软键盘 可以使用下面另一种方式
//    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.showSoftInput(editText, 0)

//    if (this.requestFocus()) {
//        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
//    }
}