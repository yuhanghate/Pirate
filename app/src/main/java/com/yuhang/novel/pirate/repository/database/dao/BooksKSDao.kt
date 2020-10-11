package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.BooksKSEntity

@Dao
interface BooksKSDao {

    @Insert
    suspend fun insert(list:List<BooksKSEntity>)

    @Query("delete from BooksKSEntity where gender =:gender and toobarName =:toobarName and type =:type and date =:date")
    suspend fun delete(gender:String, toobarName:String, type:String, date:String = "")

    @Query("select * from BooksKSEntity where gender = :gender and toobarName = :toobarName and type = :type and date = :date")
    suspend fun query(gender:String, toobarName:String, type:String, date:String = ""):List<BooksKSEntity>
}