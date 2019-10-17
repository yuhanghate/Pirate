package com.yuhang.novel.pirate.repository.network.data.kuaidu.result

data class BookDetailsKdResult(
    var _id: String = "",
    var author: String = "",//作者
    var cat: String = "",//分类:古典仙侠
    var chaptersCount: Int = 0,//章节总数
    var cover: String = "",//封面
    var isSerial: Boolean = false,//是否连载
    var lastChapter: String = "",//最后章节名称
    var latelyFollower: Int = 0,//追更人数
    var longIntro: String  = "",//简介
    var majorCate: String = "",//男生分类:仙侠
    var minorCate: String = "",//女生分类:古典仙侠
    var retentionRatio: String = "",//留存率
    var title: String = "",//小说名称
    var updated: String = "",//更新时间
    var wordCount: Int = 0//书本总字数
)