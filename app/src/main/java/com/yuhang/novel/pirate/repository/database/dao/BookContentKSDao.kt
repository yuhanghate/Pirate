package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import io.reactivex.Flowable
import java.util.*

@Dao
interface BookContentKSDao {

    /**
     * 插入章节内容
     */
    @Insert
    fun insert(obj: BookContentKSEntity)

    /**
     * 查找章节内容
     */
    @Query("select * from bookcontentksentity as c where c.chapterId = :chapterid and c.bookId = :bookid limit 1")
    fun query(bookid: Int, chapterid: Int): BookContentKSEntity?

    /**
     * 查询章节内容
     */
    @Query("select * from bookcontentksentity as c where c.chapterId = :chapterid and c.bookId = :bookid limit 1")
    fun queryObj(bookid: Int, chapterid: Int): BookContentKSEntity?

    /**
     * 查询书箱章节所有的更新时间
     */
    @Query("select c.lastOpenTime from bookcontentksentity as c where c.bookId = :bookid order by c.lastOpenTime desc")
    fun queryTimeList(bookid: Int): List<Long>

    /**
     * 更新时间+章节id
     */
    @Query("select * from bookcontentksentity as c where c.bookId=:bookid order by c.lastOpenTime desc")
    fun queryTimeAndChapterid(bookid: Int): List<BookContentKSEntity?>


    /**
     * 更新最后一次打开的时间和内容角标
     */
    @Query("update bookcontentksentity set lastOpenTime = :lastOpenTime, lastContentPosition = :lastContentPosition where chapterId = :chapterid")
    fun updateLastOpenContent(chapterid: Int, lastOpenTime: Date, lastContentPosition: Int)

    /**
     * 根据小说id查询最近阅读章节
     */
    @Query("select * from bookcontentksentity as c  where c.bookId = :bookid  order by c.lastOpenTime desc limit 1")
    fun queryLastOpenChapter(bookid: Int): BookContentKSEntity?

    /**
     * 是否显示更新
     * 对比最近更新时间和上次阅读时间对比
     * 1:显示  2:不显示
     */
    @Query("select  case  when c.lastOpenTime == null then 1 when  i.lastTime > c.lastOpenTime   then 1 else 2 end from bookcontentksentity as c left join bookinfoksentity as i where c.bookId = i.bookid")
    fun isShowUpdateLable(): Int

}