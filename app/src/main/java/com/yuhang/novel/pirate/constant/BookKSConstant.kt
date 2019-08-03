package com.yuhang.novel.pirate.constant

/**
 * 看书神器常量
 */
object BookKSConstant {

  /**
   * 测试使用的小说
   */
  val BOOKRACK = arrayOf(87101, 36007, 191602)

  /**
   * 没有下一章节
   */
  val NOT_CHAPTER = -1

  /**
   * 过滤-性别
   */
  val FILTER_GENDER = arrayListOf<String>("man", "lady")

  /**
   * 过滤-类型
   */
  val FILTER_TYPE = arrayListOf<String>("hot", "commend", "over", "collect", "new", "vote")

  /**
   * 过滤-日期
   */
  val FILTER_DATE = arrayListOf<String>("week", "month", "total")
}