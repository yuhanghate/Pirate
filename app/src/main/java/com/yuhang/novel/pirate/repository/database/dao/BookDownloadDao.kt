package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity

@Dao
interface BookDownloadDao {

    @Insert
    suspend fun insert(obj:BookDownloadEntity)

    @Update
    suspend fun update(obj:BookDownloadEntity)

    @Query("select * from bookdownloadentity where bookId = :bookid")
    suspend fun query(bookid:String):BookDownloadEntity?

    /**
     * 获取所有下载书籍
     */
    @Query("select * from bookdownloadentity group by bookId order by id desc")
    suspend fun queryAll():List<BookDownloadEntity>

    /**
     * 删除指定缓存
     */
    @Query("delete from bookdownloadentity where bookId = :bookid")
    suspend fun deleteDownload(bookid: String)

    @Query("delete from bookdownloadentity")
    suspend fun clear()
}