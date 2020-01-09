package com.yuhang.novel.pirate.repository.network.data.kuaidu.result

data class AuthorBooksDataKdResult(
    var _id: String = "",
    var author: String? = "",
    var cover: String? = "",
    var latelyFollower: Int = 0,
    var longIntro: String? = "",
    var majorCate: String? = "",
    var minorCate: String? = "",
    var retentionRatio: Double = 0.0,
    var title: String? = ""
)