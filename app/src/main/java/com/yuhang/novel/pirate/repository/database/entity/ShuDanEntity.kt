package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 书单
 */
@Entity
class ShuDanEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var AddTime: String = ""
    var BookCount: Int = 0
    var CollectionCount: Int = 0
    var CommendCount: Int = 0
    var Cover: String = ""
    var Description: String = ""
    var ForMan: Boolean = false
    var IsCheck: Boolean = false
    var ListId: Int = 0
    var Title: String = ""
    var UpdateTime: String = ""
    var UserName: String = ""

    var toolbarName:String = ""
    var type:String = ""
    var gender:String = ""
}