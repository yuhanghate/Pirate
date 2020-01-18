package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SexBooksEntity {

    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    /**
     * 作者
     */
    var author: String = ""

    /**
     * 小说id
     */
    var bookId: Int = 0

    /**
     * 小说名称
     */
    var bookName: String = ""
    var bookUrl: String = ""

    /**
     * 简介
     */
    var description: String = ""
    var lastChapterName: String = ""
}