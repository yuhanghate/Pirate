package com.yuhang.novel.pirate.repository.network.data.kanshu.result

/**
 * 评论
 * {
"BookId": 191602,
"TotalScore": 899,
"VoterCount": 96,
"Score": 9.4
}
 */
data class BookVote(
  /**
   * 评分
   */
  val score: Double = 0.0,
  /**
   * 小说id
   */
  val bookId: Long = 0,
  val voterCount: Int = 0,
  val totalScore: Int = 0
)