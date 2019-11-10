package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 小说信息表
 */
@Entity
class BookInfoKSEntity{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    /**
     * 小说id
     */
    var bookid: String = ""

    /**
     * 小说封面
     */
    var cover: String = ""

    /**
     * 小说名称
     */
    var bookName: String = ""

    /**
     * 作者
     */
    var author: String = ""

    /**
     * 简介
     */
    var description: String = ""

    /**
     * 最后更新时间
     */
    var lastTime: Long = System.currentTimeMillis()


    /**
     * 最后更新章节名称
     */
    var lastChapterName: String = ""

    var lastChapterId : String = ""


    /**
     * 小说更新状态: 连载/完结
     */
    var bookStatus: String = ""


    /**
     * 分类名称
     * 例如: 武侠仙侠/ 玄幻奇幻/ 历史军事/ 都市言情/ 同人小说
     */
    var classifyName: String = ""

    /**
     * 置顶时间
     * 用于使用置顶功能
     */
    var stickTime: Long = 0

    /**
     * 最后阅读时间
     */
    var lastReadTime :Long = 0

    /**
     * 用于View显示,
     * 是否显示标签
     */
    @Ignore
    var isShowLabel = false

    /**
     * 快读/看书
     */
    var resouce = "KS"
}