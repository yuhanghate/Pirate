package com.yuhang.novel.pirate.repository.network.data.kuaidu.result

data class BookUpdateKdResult(
    var _id: String = "",//小说id
    var author: String = "",//作者
    var chaptersCount: Int = 0,//章节字数
    var lastChapter: String = "",//最后章节名称
    var updated: String = ""//更新时间
)