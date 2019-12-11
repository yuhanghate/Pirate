package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ConfigEntity {

    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    /**
     * 推荐游戏
     * true:显示  false:不显示
     */
    var showGameRecommended:Boolean = false

    /**
     * 小黄书
     * true:显示  false:不显示
     */
    var showSexBook:Boolean = false

    /**
     * 是否开启vip
     */
    var isOpenVip:Boolean = false
}