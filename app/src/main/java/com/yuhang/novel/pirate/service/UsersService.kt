package com.yuhang.novel.pirate.service

import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.UserResult

/**
 * 用户服务
 */
interface UsersService {

    /**
     * 更新线上收藏记录到本地
     */
    suspend fun updateCollectionToLocal(): List<BooksResult>


    /**
     * 更新小说章节列表到本地
     */
    suspend fun updateChapterListToLocal(bookid: BooksResult): BooksResult

    /**
     * 更新最后阅读章节到本地
     */
    suspend fun updateReadHistoryToLocal(obj: BooksResult): BooksResult

    /**
     * 更新小说详情到本地
     */
    suspend fun updateBookInfoToLocal(obj: BooksResult): BooksResult

    /**
     * 更新用户信息到本地
     */
    suspend fun updateUsersToLocal(userResult: UserResult)

    /**
     * 更新内容到本地,更新第一章内容
     */
    suspend fun updateContentToLocal(bookid: String): String
}