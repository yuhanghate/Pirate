package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.*
import com.yuhang.novel.pirate.repository.database.entity.BookCollectionKSEntity
import io.reactivex.Flowable
@Dao
interface BookCollectionKSDao {

    /**
     * 增加收藏
     */
    @Insert
    fun insert(obj:BookCollectionKSEntity)

    /**
     * 查询指定书籍收藏的名单
     */
    @Query("select * from bookcollectionksentity as c where c.bookid = :bookid order by  c.time desc limit 1")
    fun query(bookid:Long):BookCollectionKSEntity?

    /**
     * 查询所有收藏记录
     */
    @Query("select * from bookcollectionksentity as c group by c.bookid order by c.time desc")
    fun queryAll():List<BookCollectionKSEntity?>

    /**
     * 更新收藏时间
     * 可用于置顶功能
     */
    @Update
    fun udpate(vararg list:BookCollectionKSEntity)

    /**
     * 删除收藏时间
     */
    @Query("delete from bookcollectionksentity where bookid = :bookid")
    fun delete(bookid: Long)

    /**
     * 更新时间
     */
    @Query("update bookcollectionksentity set time = :time where bookid = :bookid")
    fun updateTime(bookid: Long, time:Long)

    @Query("delete from bookcollectionksentity")
    fun clear()
}