package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.StoreEntity

@Dao
interface StoreDao {

    @Insert
    suspend fun insert(obj:List<StoreEntity>)

    @Query("select * from storeentity where genderType = :genderType")
    suspend fun query(genderType:String):List<StoreEntity>

    @Query("delete from StoreEntity where genderType = :genderType")
    suspend fun delete(genderType:String)
}