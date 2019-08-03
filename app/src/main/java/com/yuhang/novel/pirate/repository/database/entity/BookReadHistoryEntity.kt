package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 阅读历史
 */
@Entity
class BookReadHistoryEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    /**
     * 章节id
     */
    var chapterid = -1

    /**
     * 小说ID
     */
    var bookid = -1

    /**
     * 更新最新的阅读时间
     */
    var lastReadTime = System.currentTimeMillis() / 1000
}