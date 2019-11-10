package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 快读源的子渠道
 * 记录每次快读源的子渠道
 */
@Entity
class BookResouceTypeKDEntity {

    @PrimaryKey(autoGenerate = true)
    var id :Int = 0

    var bookid:String = ""

    /**
     * 书名
     */
    var bookName = ""

    /**
     * 快读源id
     */
    var tocId :String = ""

    /**
     * 快读源名称
     */
    var typeName = ""

    /**
     * 快读/看书
     */
    var resouce = "KD"
}