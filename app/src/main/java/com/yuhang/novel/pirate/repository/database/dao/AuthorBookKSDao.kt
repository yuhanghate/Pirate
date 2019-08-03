package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yuhang.novel.pirate.repository.database.entity.AuthorBookKSEntity
import io.reactivex.Flowable

/**
 * 作者书籍表
 */
@Dao
interface AuthorBookKSDao {

  @Insert
  fun insert(obj:List<AuthorBookKSEntity>)

  @Update
  fun update(obj:List<AuthorBookKSEntity>)

  /**
   * 查询作者所有书籍
   */
  @Query("select * from authorbookksentity as a where a.author = :author order by a.name desc ")
  fun query(author:String):Flowable<AuthorBookKSEntity>
}