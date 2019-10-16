package com.yuhang.novel.pirate.repository.network.data.kuaidu.result

data class SearchDataKdResult(
    var _id: String = "",
    var author: String = "",//作者
    var cat: String = "",//类型:奇幻
    var cover: String = "",//封面
    var lastChapter: String = "",//最后更新章节
    var latelyFollower: Int = 0,//追更人数
    var longIntro: String = "",//简介
    var majorCate: String = "",//男生类型:奇幻
    var minorCate: String = "",//女生类型
    var retentionRatio: Double = 0.0,//读者留存率
    var title: String = "",//小说名称
    var wordCount: Int = 0//小说字数: 278万
)