package com.yuhang.novel.pirate.listener

import android.view.View

/**
 * 所有RecyclerView点击事件
 */
interface OnClickItemListener {

    /**
     * Item点击事件
     */
    fun onClickItemListener(view:View, position:Int)
}