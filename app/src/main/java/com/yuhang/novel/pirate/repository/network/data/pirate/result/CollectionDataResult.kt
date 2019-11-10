package com.yuhang.novel.pirate.repository.network.data.pirate.result

/**
 * bookName : 凡人修仙传
 * bookid : 2343434
 * author : 忘语
 * conver : https://bookcover.yuewen.com/qdbimg/349573/1010734492/180
 * resouceType : KS
 */
data class CollectionDataResult(
    val author: String? = null,
    val bookName: String? = null,
    val bookid: String,
    val cover: String? = null,
    val resouceType: String,
    val tocId:String? = null,
    val tocName:String? = null
)