package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RankingListEntity {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    /**
     * 小说作者
     */
    var author: String = ""

    /**
     * 章节名称
     */
    var chapterName: String = ""

    /**
     * 详情
     */
    var desc: String = ""

    /**
     * 小说id
     */
    var bookdid: String = ""

    /**
     * 封面
     */
    var cover: String = ""

    /**
     * 小说名称
     */
    var bookName: String = ""

    /**
     * 评分
     */
    var score: Double = 0.0

    /**
     * 默认排序脚标
     */
    var index: Int = 0
}