package com.yuhang.novel.pirate.service

import com.yuhang.novel.pirate.repository.network.data.pirate.result.BookResouceResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.UserResult
import io.reactivex.Flowable

/**
 * 用户服务
 */
interface UsersService {

    /**
     * 更新线上收藏记录到本地
     */
    fun updateCollectionToLocal(): Flowable<BooksResult>


    /**
     * 更新小说章节列表到本地
     */
    fun updateChapterListToLocal(bookid: BooksResult): Flowable<BooksResult>

    /**
     * 更新最后阅读章节到本地
     */
    fun updateReadHistoryToLocal(obj:BooksResult):Flowable<BooksResult>

    /**
     * 更新小说详情到本地
     */
    fun updateBookInfoToLocal(obj:BooksResult): Flowable<BooksResult>

    /**
     * 更新用户信息到本地
     */
    fun updateUsersToLocal(userResult: UserResult): Flowable<Unit>

    /**
     * 更新内容到本地,更新第一章内容
     */
    fun updateContentToLocal(bookid: String):Flowable<String>
}