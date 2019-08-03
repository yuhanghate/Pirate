package com.gyb.live.mitang.extension

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Activity.niceToast(msg : String) {
    Toast.makeText(this,msg, Toast.LENGTH_LONG).show()
}

fun Fragment.niceToast(msg : String) {
    Toast.makeText(activity,msg, Toast.LENGTH_LONG).show()
}