package com.yuhang.novel.pirate.listener

/**
 * 下拉刷新
 */
interface OnRefreshListener {

    fun onRefresh(firstVisibleItemPosition:Int, lastVisibleItemPosition:Int)
}