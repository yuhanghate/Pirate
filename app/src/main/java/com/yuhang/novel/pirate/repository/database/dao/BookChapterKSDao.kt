package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import io.reactivex.Flowable

@Dao
interface BookChapterKSDao {

  /**
   * 根据书籍id查找所有章节
   */
  @Query("select * from bookchapterksentity as c where c.bookId = :bookid group by c.chapterId order by c.chapterId asc ")
  fun query(bookid:Int):Flowable<List<BookChapterKSEntity>>

  /**
   * 根据书籍id查找所有章节
   */
  @Query("select * from bookchapterksentity as c where c.bookId = :bookid group by c.chapterId order by c.chapterId asc ")
  fun queryObj(bookid:Int):List<BookChapterKSEntity>

  /**
   * 插入章节列表
   */
  @Insert
  fun insert(list:List<BookChapterKSEntity>)

  /**
   * 删除书籍对应章节
   */
  @Query("delete from bookchapterksentity where bookId = :bookid")
  fun delete(bookid: Int)

  /**
   * 获取第一章节id
   */
  @Query("select c.chapterId from bookchapterksentity as c where c.bookId = :bookid order by c.chapterId asc limit 1")
  fun queryFirstChapterid(bookid: Int):Int

}