package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.*
import com.yuhang.novel.pirate.repository.database.entity.PushMessageEntity

@Dao
interface PushMessageDao {

    @Insert
    fun insert(obj:PushMessageEntity)

    @Update
    fun update(obj:PushMessageEntity)

    /**
     * 查找最近的公告信息
     */
    @Query("select * from pushmessageentity where type = 'NOTE' and hasRead = 0 order by id desc limit 1")
    fun queryNote():PushMessageEntity

    @Delete
    fun delete(obj:PushMessageEntity)
}