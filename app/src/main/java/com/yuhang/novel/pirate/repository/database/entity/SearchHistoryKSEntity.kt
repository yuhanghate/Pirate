package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 搜索过的历史记录
 */
@Entity
data class SearchHistoryKSEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    /**
     * 关键字
     */
    val keyword: String = "",

    /**
     * 搜索的更新时间
     */
    val updateTime: Long = System.currentTimeMillis() / 1000
)