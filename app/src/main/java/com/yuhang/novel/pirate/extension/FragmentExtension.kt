package com.yuhang.novel.pirate.extension

import android.app.Activity
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment

fun Fragment.findNavController(): NavController =
    NavHostFragment.findNavController(this)

fun Activity.findNavController(@IdRes viewId: Int): NavController =
    Navigation.findNavController(this, viewId)