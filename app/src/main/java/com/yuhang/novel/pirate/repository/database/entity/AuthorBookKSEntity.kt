package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 作者
 */
@Entity
data class AuthorBookKSEntity(
  @PrimaryKey(autoGenerate = true)
  var id: Int = 0,

  val cover: String = "",

  /**
   * 星级评分
   */
  val score: Int = 0,

  /**
   * 最后更新章节名称
   */
  val lastChapterName: String = "",

  /**
   * 作者
   */
  val author: String = "",

  /**
   * 小说id
   */
  val bookid: Int = 0,

  /**
   * 最后更新章节id
   */
  val lastChapterId: Int = 0,

  /**
   * 小说名称
   */
  val name: String = ""
)