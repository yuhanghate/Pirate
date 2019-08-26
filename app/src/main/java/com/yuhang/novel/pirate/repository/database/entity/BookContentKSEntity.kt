package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.yuhang.novel.pirate.widget.pageview.TextPageBean
import com.yuhang.novel.pirate.widget.pageview.TxtPage

@Entity
class BookContentKSEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var hasContent: Int = 0

    /**
     * 章节目录(篇)
     */
    var chapterDirName: String = ""

    /**
     * 章节名称
     */
    var chapterName: String = ""

    /**
     * 下一章节id
     */
    var nid: Int = -1

    /**
     * 上一章节id
     */
    var pid: Int = -1

    /**
     * 书籍id
     */
    var bookId: Long = 0

    /**
     * 章节内容
     */
    var content: String = ""

    /**
     * 章节id
     */
    var chapterId: Int = 0

    /**
     * 最后阅读时间
     * 每次打开章节内容进行更新
     */
    var lastOpenTime: Long = 0

    /**
     * 记录上一次阅读的章节的内容角标
     * 记录上一次阅读的内容角标位置
     */
    var lastContentPosition: Int = 0

    @Ignore
    var txtPage: TxtPage? = null

    @Ignore
    var textPageBean: TextPageBean? = null
}