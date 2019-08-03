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
    var bookid: Int = 0

    /**
     * 小说封面
     */
    var cover: String = ""

    /**
     * 小说名称
     */
    var name: String = ""

    /**
     * 作者
     */
    var author: String = ""

    /**
     * 简介
     */
    var desc: String = ""

    /**
     * 最后更新时间
     */
    var lastTime: Date = Date(System.currentTimeMillis())

    /**
     * 上一章节id
     */
    var firstChapterId: Int = -1

    /**
     * 最后更新章节名称
     */
    var lastChapterName: String = ""

    /**
     * 最后更新章节id
     */
    var lastChapterId: Int = -1

    /**
     * 小说更新状态: 连载/完结
     */
    var bookStatus: String = ""

    /**
     * 分类id
     */
    var classifyId: Int = 0

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
     * 用于View显示,
     * 是否显示标签
     */
    @Ignore
    var isShowLabel = false
}