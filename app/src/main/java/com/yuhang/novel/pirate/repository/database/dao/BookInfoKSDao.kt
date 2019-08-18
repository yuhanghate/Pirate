package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import io.reactivex.Flowable
import java.util.*

@Dao
interface BookInfoKSDao {

    @Insert
    fun insert(obj: BookInfoKSEntity)

    /**
     * 更新小说详情
     */
    @Update
    fun update(obj: BookInfoKSEntity)

    /**
     * 查询书本信息
     */
    @Query("select * from bookinfoksentity as b where b.bookid = :bookid order by b.id, b.stickTime desc limit 1")
    fun query(bookid: Int): BookInfoKSEntity?

    /**
     * 删除小说对应书本信息
     */
    @Query("delete from bookinfoksentity where bookid = :bookid")
    fun delete(bookid: Int)

    /**
     * 更新置顶时间戳
     */
    @Query("update bookinfoksentity set stickTime = :stickTime where bookid = :bookid")
    fun update(stickTime: Long, bookid: Int)

    /**
     * 查询所有收藏的书本信息
     */
    @Query("select * from bookinfoksentity as info where info.bookid in (select c.bookid from bookcollectionksentity as c group by c.bookid order by c.time desc) order by info.stickTime desc")
    fun queryCollectionAll(): List<BookInfoKSEntity?>

    /**
     * 查询所有收藏书本信息
     */
    @Query("select * from bookinfoksentity as info where  info.bookid in (:bookids) order by info.stickTime desc")
    fun queryCollectionAll(bookids: Array<Int>):List<BookInfoKSEntity?>


    /**
     * 查询阅读记录的书本信息
     */
    @Query("select * from bookinfoksentity as info where info.bookid in (select h.bookid from bookreadhistoryentity as h group by h.bookid order by h.lastReadTime desc limit 20 offset :pageNum)")
    fun queryReadHistoryList(pageNum:Int):List<BookInfoKSEntity?>

    /**
     * 获取最后更新时间
     */
    @Query("select max(lastTime) from bookinfoksentity")
    fun queryLastTime():Long

    @Query("delete from bookinfoksentity")
    fun clear()
}