package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.*
import com.yuhang.novel.pirate.repository.database.entity.PushMessageEntity

@Dao
interface PushMessageDao {

    @Insert
    suspend fun insert(obj:PushMessageEntity)

    @Update
    suspend fun update(obj:PushMessageEntity)

    /**
     * 查找最近的公告信息
     */
    @Query("select * from pushmessageentity where type = 'NOTE' and hasRead = 0 order by id desc limit 1")
    suspend fun queryNote():PushMessageEntity

    @Delete
    suspend fun delete(obj:PushMessageEntity)
}