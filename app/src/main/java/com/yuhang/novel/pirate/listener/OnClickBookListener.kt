package com.yuhang.novel.pirate.listener

import android.view.View
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult

interface OnClickBookListener {

    fun onClickBookListener(view:View, obj:BooksKSResult, position:Int)
}