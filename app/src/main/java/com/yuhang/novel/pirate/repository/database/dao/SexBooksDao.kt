package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.SexBooksEntity

@Dao
interface SexBooksDao {

    @Insert
    suspend fun insert(list: List<SexBooksEntity>)

    @Query("select * from sexbooksentity")
    suspend fun queryAll():List<SexBooksEntity>

    @Query("delete from sexbooksentity")
    suspend fun clean()
}