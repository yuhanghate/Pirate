package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * 小说章节
 */
@Entity
class BookChapterKSEntity{
  @PrimaryKey(autoGenerate = true)
  var id: Int = 0


  /**
   * 章节名称
   */
  var name: String = ""

  /**
   * 章节id
   */
  var chapterId: String = ""


  /**
   * 书籍id
   */
  var bookId: String = ""

  /**
   * 数据源类型: 看书神器/快读
   */
  var resouce :String = ""

  /**
   * 是否有章节缓存
   * 1:缓存
   * 0:未缓存
   */
  var hasContent : Int = 0
}