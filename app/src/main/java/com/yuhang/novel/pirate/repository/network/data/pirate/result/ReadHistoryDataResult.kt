package com.yuhang.novel.pirate.repository.network.data.pirate.result

/**
 * bookName : 凡人修仙传仙界篇
 * bookid : 30594
 * author : 忘语
 * conver : https://bookcover.yuewen.com/qdbimg/349573/1010734492/180
 * resouceType : KS
 */
data class ReadHistoryDataResult(
    val author: String = "",
    val bookName: String = "",
    val bookid: String = "",
    val cover: String = "",
    val resouceType: String = "",
    val description: String = ""
)