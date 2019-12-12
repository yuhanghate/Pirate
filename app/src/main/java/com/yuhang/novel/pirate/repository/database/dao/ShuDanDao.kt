package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.ShuDanEntity

@Dao
interface ShuDanDao {

    @Insert
    fun insert(list:List<ShuDanEntity>)

    @Query("delete from ShuDanEntity where toolbarName = :name and gender = :gender and type = :type")
    fun delete(name:String, gender:String, type:String)

    @Query("select * from ShuDanEntity where toolbarName = :name and gender = :gender and type = :type")
    fun query(name:String, gender:String, type:String):List<ShuDanEntity>
}