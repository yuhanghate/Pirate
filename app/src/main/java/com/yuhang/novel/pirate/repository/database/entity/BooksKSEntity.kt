package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class BooksKSEntity {

    @PrimaryKey(autoGenerate = true)
    var id : Int = 0

    /**
     * 封面
     */
    var Img: String = ""

    /**
     * 评分
     */
    var Score: Double = 0.0

    /**
     * 最后章节
     */
    var LastChapter: String = ""

    /**
     * 作者
     */
    var Author: String = ""

    /**
     * 小说id
     */
    var bookId: String = ""

    /**
     * 最后章节id
     */
    var LastChapterId: String = ""

    /**
     * 章节名称
     */
    var CName: String = ""

    /**
     * 简介
     */
    var Desc: String = ""

    /**
     * 小说名称
     */
    var Name: String = ""

    /**
     * 小说状态
     */
    var BookStatus:String = ""

    /**
     * 男生/女生
     */
    var gender:String = ""

    /**
     * 类型: hot/over/commend/collect/new/vote
     */
    var type:String = ""

    /**
     * 时间: 周榜/月榜/总榜
     */
    var date:String = ""

    /**
     * 标题栏名称
     */
    var toobarName :String = ""
}