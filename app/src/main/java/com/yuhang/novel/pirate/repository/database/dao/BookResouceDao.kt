package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yuhang.novel.pirate.repository.database.entity.BookResouceEntity

@Dao
interface BookResouceDao {

    @Insert
    fun insert(obj:BookResouceEntity)

    /**
     * 修改选中状态
     */
    @Query("update bookresouceentity set checkStatus = :isCheck where resouceId =:resouceid")
    fun update(resouceid: String, isCheck:Int)

    /**
     * 根据热度获取选中的源
     */
    @Query("select * from bookresouceentity where checkStatus = 1 order by hot desc")
    fun queryCheckList():List<BookResouceEntity>

    @Query("select * from bookresouceentity where resouceId =:resouceid limit 1")
    fun query(resouceid:String):BookResouceEntity?

    /**
     * 取消所有的选中状态
     */
    @Query("update bookresouceentity set checkStatus = 0")
    fun clearCheck()
}