package com.yuhang.novel.pirate.listener

/**
 * 加载更多
 */
interface OnLoadMoreListener {

    fun onLoadMore(firstVisibleItemPosition:Int, lastVisibleItemPosition:Int)
}