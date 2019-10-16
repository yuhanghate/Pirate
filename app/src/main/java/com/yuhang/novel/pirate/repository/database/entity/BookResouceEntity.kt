package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 书源
 */
@Entity
class BookResouceEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    /**
     * 书源名称
     */
    var title: String = ""

    /**
     * 书源地址
     */
    var website: String = ""

    /**
     * 热度
     */
    var hot: Int = 0

    /**
     * 状态:运行正常/无法访问
     */
    var status: String = ""

    /**
     * 是否选中源
     * 0:未选中  1:选中
     */
    var checkStatus: Int = 0

    /**
     * 检测时间
     */
    var checkTime: Long = 0

    /**
     * html提取规则
     */
    var resouceRule: String = ""

    /**
     * 更新规则时间
     */
    var updateTime: Long = 0

    /**
     * 源id
     */
    var resouceId :String = ""

}