package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.ConfigEntity

@Dao
interface ConfigDao {

    @Insert
    fun insert(obj:ConfigEntity)

    @Query("select * from configentity order by id desc limit 1")
    fun query():ConfigEntity
}