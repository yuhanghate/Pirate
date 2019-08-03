package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.*
import com.yuhang.novel.pirate.repository.database.entity.SearchHistoryKSEntity
import io.reactivex.Flowable

@Dao
interface SearchHistoryKSDao {

  @Query("select * from searchhistoryksentity as s order by s.updateTime desc limit 10")
  fun query():List<SearchHistoryKSEntity?>

  @Insert
  fun insert(obj:SearchHistoryKSEntity)

  @Update
  fun update(obj:SearchHistoryKSEntity)

  /**
   * 查询关键字
   */
  @Query("select * from searchhistoryksentity as s where s.keyword = :keyword order by s.updateTime limit 1")
  fun query(keyword:String):SearchHistoryKSEntity?

  @Delete
  fun delete(obj:SearchHistoryKSEntity)
}