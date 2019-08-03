package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 小说章节
 */
@Entity
data class BookChapterKSEntity(

  @PrimaryKey(autoGenerate = true)
  var id: Int = 0,

  /**
   * 章节目录(篇)
   */
  val dirName: String = "",

  /**
   * 章节名称
   */
  val name: String = "",

  /**
   * 章节id
   */
  val chapterId: Int = 0,

  /**
   * 书籍名称
   */
  val bookName: String = "",

  /**
   * 书籍id
   */
  val bookId: Int = 0
)