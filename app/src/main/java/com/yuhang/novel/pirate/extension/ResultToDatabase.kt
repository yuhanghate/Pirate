package com.yuhang.novel.pirate.extension

import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.database.entity.SearchHistoryKSEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookDetailsDataResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ChapterListDataResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ContentDataResult
import com.yuhang.novel.pirate.ui.search.result.SearchResult
import java.util.*

/**
 * 看书神器章节Bean转成本地数据库
 */
fun ChapterListDataResult.niceBookChapterKSEntity(): List<BookChapterKSEntity> {
    val list = arrayListOf<BookChapterKSEntity>()
    this.list?.forEach { dir ->
        dir?.list?.forEach { chapter ->
            chapter?.let {
                list.add(
                        BookChapterKSEntity(
                                dirName = dir.name,
                                name = chapter.name,
                                chapterId = chapter.id,
                                bookName = this.name,
                                bookId = this.id
                        )
                )
            }

        }
    }
    return list
}

/**
 * 看书神器内容Bean转成本地数据库
 */
fun ContentDataResult.niceBookContentKSEntity(): BookContentKSEntity {

    val obj = this
    return BookContentKSEntity().apply {
        hasContent = obj.hasContent
        chapterName = obj.cname
        nid = obj.nid
        pid = obj.pid
        bookId = obj.id
        content = obj.content
        chapterId = obj.cid
    }
}

fun ContentDataResult.niceBookInfoKSEntity(): BookInfoKSEntity {
    val obj = this
    return BookInfoKSEntity().apply {
        this.bookid = obj.id
        this.name = obj.name
        this.lastTime = Date()
        this.firstChapterId = obj.pid
        this.lastChapterId = obj.cid
        this.classifyId = -1
    }
}

/**
 * 看书神器 书籍详情Bean转成本地数据库
 */
fun BookDetailsDataResult.niceBookInfoKSEntity(): BookInfoKSEntity {
    val obj = this
    return BookInfoKSEntity().apply {
        this.bookid = obj.Id
        this.cover = obj.Img
        this.name = obj.Name
        this.author = obj.Author
        this.desc = obj.Desc
        this.lastTime = Date(obj.LastTime)
        this.firstChapterId = obj.FirstChapterId
        this.lastChapterName = obj.LastChapter
        this.lastChapterId = obj.LastChapterId
        this.bookStatus = obj.BookStatus
        this.classifyId = obj.CId
        this.classifyName = obj.CName
    }
}

/**
 * 看书神器 搜索对象转View搜索对象
 */
fun SearchHistoryKSEntity.niceSearchResult(): SearchResult {
    val keyword = this.keyword
    return SearchResult().apply {
        this.keyword = keyword
    }
}
