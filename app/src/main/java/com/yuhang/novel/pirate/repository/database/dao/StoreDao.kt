package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.StoreEntity

@Dao
interface StoreDao {

    @Insert
    fun insert(obj:List<StoreEntity>)

    @Query("select * from storeentity where genderType = :genderType")
    fun query(genderType:String):List<StoreEntity>

    @Query("delete from StoreEntity where genderType = :genderType")
    fun delete(genderType:String)
}