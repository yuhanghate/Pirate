package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yuhang.novel.pirate.repository.database.entity.BookReadHistoryEntity

@Dao
interface BookReadHistoryDao {

    @Insert
    fun insert(entity: BookReadHistoryEntity)

    @Update
    fun update(entity: BookReadHistoryEntity)

    /**
     * 查询指定章节小说最后一次阅读内容
     */
    @Query("select * from bookreadhistoryentity where bookid = :bookid and chapterid = :chapterid order by lastReadTime desc limit 1")
    fun queryBookReadHistoryEntity(bookid:String, chapterid:String):BookReadHistoryEntity?

    /**
     * 获取指定书最后章节
     */
    @Query("select * from bookreadhistoryentity where bookid = :bookid order by lastReadTime desc limit 1")
    fun queryLastChanpterEntity(bookid:String):BookReadHistoryEntity?

    @Query("select * from bookreadhistoryentity order by lastReadTime desc")
    fun queryAll():List<BookReadHistoryEntity>

    /**
     * 删除指定小说章节
     */
    @Query("delete from bookreadhistoryentity where bookid = :bookid")
    fun clear(bookid: String)

    @Query("delete from bookreadhistoryentity")
    fun clear()
}