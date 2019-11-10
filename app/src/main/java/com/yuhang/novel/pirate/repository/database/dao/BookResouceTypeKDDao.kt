package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yuhang.novel.pirate.repository.database.entity.BookResouceTypeKDEntity

@Dao
interface BookResouceTypeKDDao {

    @Insert
    fun insert(obj: BookResouceTypeKDEntity)

    @Query("select * from bookresoucetypekdentity where bookid = :bookid limit 1")
    fun query(bookid:String):BookResouceTypeKDEntity?

    @Query("select * from bookresoucetypekdentity where tocId = :tocid limit 1")
    fun queryByTocid(tocid:String):BookResouceTypeKDEntity?

    @Update
    fun update(obj: BookResouceTypeKDEntity)

    @Query("delete from bookresoucetypekdentity")
    fun clear()
}