package com.yuhang.novel.pirate.repository.audio.model

import android.os.Parcelable
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

/**
 * 朗读传值对象
 */
@Parcelize
 class AloudModel(val title:String, val subTitle:String, val text:String, val bookid:String, val chapterid:String) : Parcelable{
    companion object{
        val TAG = "AloudModel"
    }
}