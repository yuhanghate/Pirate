package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity

@Dao
interface BookInfoKSDao {

    @Insert
    suspend fun insert(obj: BookInfoKSEntity)

    /**
     * 更新小说详情
     */
    @Update
    suspend fun update(obj: BookInfoKSEntity)

    /**
     * 查询书本信息
     */
    @Query("select * from bookinfoksentity as b where b.bookid = :bookid order by b.id, b.stickTime desc limit 1")
    suspend fun query(bookid: String): BookInfoKSEntity?

    /**
     * 删除小说对应书本信息
     */
    @Query("delete from bookinfoksentity where bookid = :bookid")
    suspend fun delete(bookid: String)

    /**
     * 更新置顶时间戳
     */
    @Query("update bookinfoksentity set stickTime = :stickTime where bookid = :bookid")
    suspend fun update(stickTime: Long, bookid: String)

    /**
     * 修改最后阅读时间
     */
    @Query("update bookinfoksentity set lastReadTime = :lastReadTime where bookid = :bookid")
    suspend fun updateLastReadTime(lastReadTime : Long, bookid: String)

    /**
     * 查询所有收藏的书本信息
     */
    @Query("select info.* from bookinfoksentity info, bookcollectionksentity c where info.bookid = c.bookid and c.resouce != 'KD' group by c.bookid order by info.stickTime DESC, info.lastReadTime DESC")
    suspend fun queryCollectionAll(): List<BookInfoKSEntity?>

    /**
     * 查询所有收藏的书  看书源
     */
    @Query("select info.* from bookinfoksentity info, bookcollectionksentity c where info.bookid = c.bookid  and info.resouce = 'KS' group by c.bookid order by info.stickTime DESC, info.lastReadTime DESC ")
    suspend fun queryCollectionKS():List<BookInfoKSEntity>

    /**
     * 查询所有收藏的书 快读源
     */
    @Query("select info.* from bookinfoksentity info, bookcollectionksentity c where info.bookid = c.bookid  and info.resouce = 'KD' group by c.bookid order by info.stickTime DESC, info.lastReadTime DESC ")
    suspend fun queryCollectionKD():List<BookInfoKSEntity>

    /**
     * 查询所有连载的收藏
     */
    @Query("select info.* from bookinfoksentity info, bookcollectionksentity c where info.bookid = c.bookid and info.bookStatus != '完结' group by c.bookid order by info.stickTime DESC, info.lastReadTime DESC")
    suspend fun queryCollectionAllSerial():List<BookInfoKSEntity>

    /**
     * 查询阅读记录的书本信息
     */
    @Query("select * from bookinfoksentity as info where info.bookid in (select h.bookid from bookreadhistoryentity as h group by h.bookid order by h.lastReadTime desc limit 20 offset :pageNum)")
    suspend fun queryReadHistoryList(pageNum:Int):List<BookInfoKSEntity?>

    /**
     * 获取最后更新时间
     */
    @Query("select max(lastTime) from bookinfoksentity where bookid = :bookid")
    suspend fun queryLastTime(bookid: String):Long

    /**
     * 更新 最后更新时间
     */
    @Query("update bookinfoksentity set lastTime = :lastTime where bookid = :bookid")
    suspend fun updateLastTime(bookid: String, lastTime: Long)


    @Query("delete from bookinfoksentity")
    suspend fun clear()

    @Query("select * from bookinfoksentity")
    suspend fun queryAll():List<BookInfoKSEntity>
}