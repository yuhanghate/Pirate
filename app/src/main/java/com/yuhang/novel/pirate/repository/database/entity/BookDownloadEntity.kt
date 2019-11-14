package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 下载
 */
@Entity
class BookDownloadEntity {

    @PrimaryKey(autoGenerate = true)
    var id : Int = 0

    /**
     * 书名
     */
    var bookName:String = ""

    /**
     * 进度
     */
    var progress :Int = 0

    /**
     * 总数
     */
    var total : Int = 0

    /**
     * 源
     */
    var resouce :String = ""

    /**
     * 小说id
     */
    var bookId:String = ""

    /**
     * 封面图
     */
    var cover:String = ""

    /**
     * 作者
     */
    var author:String = ""
}