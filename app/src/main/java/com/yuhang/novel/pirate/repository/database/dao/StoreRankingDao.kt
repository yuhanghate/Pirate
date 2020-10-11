package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.StoreRankingEntity


@Dao
interface StoreRankingDao {

    @Insert
    suspend fun insert(obj: StoreRankingEntity)

    /**
     * 查询
     */
    @Query("select * from storerankingentity where genderType = :genderType order by id desc limit 1")
    suspend fun query(genderType: String): StoreRankingEntity?

    /**
     * 清空数据
     */
    @Query("delete from storerankingentity where genderType = :genderType")
    suspend fun clean(genderType: String)
}