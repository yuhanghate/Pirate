package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.yuhang.novel.pirate.widget.pageview.TextPageBean
import com.yuhang.novel.pirate.widget.pageview.TxtPage

@Entity(indices = [Index(value = ["bookId"])])
class BookContentKSEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    /**
     * 章节名称
     */
    var chapterName: String = ""


    /**
     * 书籍id
     */
    var bookId: String = ""

    /**
     * 章节内容
     */
    var content: String = ""

    /**
     * 章节id
     */
    var chapterId: String = ""

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
    var textPageBean: TextPageBean? = null

    /**
     * KS:看书
     * KD:快读
     * SEX:小黄书
     */
    var resouce : String = "KS"

    /**
     * 是否还有下一页
     */
    @Ignore
    var hasNext = true


}