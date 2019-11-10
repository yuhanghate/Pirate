package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PushMessageEntity {

    @PrimaryKey(autoGenerate = true)
    var id = 0

    /**
     * 标题
     */
    var title = ""

    /**
     * 消息内容
     */
    var message = ""

    /**
     * 推送类型
     * NOTE:公告
     */
    var type = "NOTE"

    /**
     * 是否已读
     * 0:未读  1:已读
     */
    var hasRead = 0

    /**
     * 扩展参数
     */
    var extra = ""
}