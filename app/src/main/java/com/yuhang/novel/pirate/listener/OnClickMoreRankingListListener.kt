package com.yuhang.novel.pirate.listener

import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult

/**
 * 正版榜单点击事件
 */
interface OnClickMoreRankingListListener {

    fun onClickMoreRankingListListener(obj: BooksKSResult, position:Int)
}