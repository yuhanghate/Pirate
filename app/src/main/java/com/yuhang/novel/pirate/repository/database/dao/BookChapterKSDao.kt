package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity

@Dao
interface BookChapterKSDao {

  /**
   * 根据书籍id查找所有章节
   */
  @Query("select * from bookchapterksentity as c where c.bookId = :bookid ")
  suspend fun queryObj(bookid:String):List<BookChapterKSEntity>

  /**
   * 插入章节列表
   */
  @Insert
  suspend fun insert(list:List<BookChapterKSEntity>)

  /**
   * 删除书籍对应章节
   */
  @Query("delete from bookchapterksentity where bookId = :bookid")
  suspend fun delete(bookid: String)

  @Update
  suspend fun update(obj:BookChapterKSEntity)

  /**
   * 根据小说id
   */
  @Query("select * from bookchapterksentity where bookId=:bookid")
  suspend fun query(bookid: String):BookChapterKSEntity?

  /**
   * 获取第一章节id
   */
  @Query("select c.chapterId from bookchapterksentity as c where c.bookId = :bookid limit 1")
  suspend fun queryFirstChapterid(bookid: String):String

  /**
   * 获取最后一章,章节ID
   */
  @Query("select c.chapterId from bookchapterksentity as c where c.bookId = :bookid limit 1")
  suspend fun queryLastChapterid(bookid: String):String

  @Query("delete from bookchapterksentity")
  suspend fun clear()

}