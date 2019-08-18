package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.RankingListEntity

/**
 * 排行榜
 */
@Dao
interface RankingListDao {

    /**
     * 插入排行榜数据
     */
    @Insert
    fun insert(obj:List<RankingListEntity>)

    /**
     * 查询所有排行榜数据
     */
    @Query("select * from rankinglistentity group by bookdid order by `index` asc")
    fun queryAll():List<RankingListEntity>

    /**
     * 删除排行榜数据
     */
    @Query("delete from rankinglistentity")
    fun deleteAll()
}