package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity

@Dao
interface CategoryKDDao {

    @Insert
    fun insert(obj:List<CategoryKDEntity>)

    /**
     * 获取男生
     */
    @Query("select * from categorykdentity where gender = 'male' order by `order` desc")
    fun queryMan():List<CategoryKDEntity>

    /**
     * 获取女生
     */
    @Query("select * from categorykdentity where gender = 'female' order by `order` desc")
    fun queryLady():List<CategoryKDEntity>

    /**
     * 获取出版
     */
    @Query("select * from categorykdentity where gender = 'press' order by `order` desc")
    fun queryPress():List<CategoryKDEntity>

    @Query("select * from categorykdentity")
    fun queryAll():List<CategoryKDEntity>

    @Query("delete from categorykdentity")
    fun clear()
}