package com.yuhang.novel.pirate.ui.common.model

import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult

data class StoreRankingModel(val name: String, val desc:String,  val list: List<BooksKSResult>, val background:Int)