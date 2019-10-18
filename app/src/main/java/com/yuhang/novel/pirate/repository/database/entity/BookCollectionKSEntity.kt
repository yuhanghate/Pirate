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
  val bookid: String = "",

  /**
   * 数据源类型: 看书神器/快读
   */
  val resouce_type :String = "",

  /**
   * 收藏时间
   */
  val time:Long = System.currentTimeMillis()

)