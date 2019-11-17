package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity

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
    fun query(bookid: String, chapterid: String): BookContentKSEntity?

    /**
     * 查询章节内容
     */
    @Query("select * from bookcontentksentity as c where c.chapterId = :chapterid and c.bookId = :bookid limit 1")
    fun queryObj(bookid: String, chapterid: String): BookContentKSEntity?

    /**
     * 获取书本所有章节内容
     */
    @Query("select * from bookcontentksentity where bookId = :bookid")
    fun queryAll(bookid:String):List<BookContentKSEntity>

//    /**
//     * 查询书箱章节所有的更新时间
//     */
//    @Query("select c.lastOpenTime from bookcontentksentity as c where c.bookId = :bookid order by c.lastOpenTime desc")
//    fun queryTimeList(bookid: Long): List<Long>

//    /**
//     * 更新时间+章节id
//     */
//    @Query("select * from bookcontentksentity as c where c.bookId=:bookid order by c.lastOpenTime desc")
//    fun queryTimeAndChapterid(bookid: Long): List<BookContentKSEntity?>


    /**
     * 更新最后一次打开的时间和内容角标
     */
    @Query("update bookcontentksentity set lastOpenTime = :lastOpenTime, lastContentPosition = :lastContentPosition where chapterId = :chapterid and bookId = :bookid")
    fun updateLastOpenContent(bookid: String, chapterid: String, lastOpenTime: Long, lastContentPosition: Int)

    /**
     * 根据小说id查询最近阅读章节
     */
    @Query("select * from bookcontentksentity as c  where c.bookId = :bookid  order by c.lastOpenTime desc limit 1")
    fun queryLastOpenChapter2(bookid: String): BookContentKSEntity?


    /**
     * 根据小说id查询最近阅读章节
     */
    @Query("select * from bookcontentksentity as c where c.bookId = :bookid and chapterId = (select r.chapterid from bookreadhistoryentity r where r.bookid = :bookid order by r.lastReadTime desc limit 1) order by lastOpenTime desc limit 1")
    fun queryLastOpenChapter(bookid: String): BookContentKSEntity?


    /**
     * 删除指定的章节内容
     */
    @Query("delete from bookcontentksentity where bookId = :bookid and chapterId = :chapterid")
    fun delete(bookid: String, chapterid: String)

    /**
     * 删除小说内容
     */
    @Query("delete from bookcontentksentity where bookId = :bookid")
    fun delete(bookid: String)


    @Query("delete from bookcontentksentity")
    fun clear()

}