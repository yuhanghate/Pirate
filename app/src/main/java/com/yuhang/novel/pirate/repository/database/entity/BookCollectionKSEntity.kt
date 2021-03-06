package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 收藏的书架表
 */
@Entity
data class BookCollectionKSEntity(
  @PrimaryKey(autoGenerate = true)
  var id: Int = 0,

  /**
   * 小说id
   */
  var bookid: String = "",

  /**
   * 数据源类型: 看书神器/快读
   */
  var resouce :String = "",

  /**
   * 收藏时间
   */
  var time:Long = System.currentTimeMillis()

)