package com.yuhang.novel.pirate.extension

import com.yuhang.novel.pirate.repository.database.entity.*
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookDetailsDataResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ChapterListDataResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ContentDataResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.RankingDataListResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.CollectionDataResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.ReadHistoryDataResult
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
        this.bookName = obj.name
        this.lastTime = System.currentTimeMillis()
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
//    val date = Date(obj.LastTime)
    val date = Date.parse(obj.LastTime)
    return BookInfoKSEntity().apply {
        this.bookid = obj.Id
        this.cover = obj.Img
        this.bookName = obj.Name
        this.author = obj.Author
        this.description = obj.Desc
        this.lastTime = date
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

/**
 * 排行榜对象转数据库
 */
fun RankingDataListResult.niceRankingListEntity(): RankingListEntity {

    val result = this
    return RankingListEntity().apply {
        author = result.Author
        bookName = result.Name
        bookdid = result.Id
        chapterName = result.CName
        cover = result.Img
        desc = result.Desc
        score = result.Score
    }
}

/**
 * 数据库对象转排行榜
 */
fun RankingListEntity.niceRankingDataListResult(): RankingDataListResult {
    return RankingDataListResult(Author = this.author, Name = this.bookName, Id = this.bookdid, CName = this.chapterName,
            Img = this.cover, Desc = this.desc, Score = this.score)
}

/**
 * 最近阅读对象互转
 */
fun ReadHistoryDataResult.niceBookInfoKSEntity(): BookInfoKSEntity {
    val result = this
    return BookInfoKSEntity().apply {
        this.bookName = result.bookName
        this.bookid = result.bookid.toLong()
        this.description = result.description
        this.author = result.author
        this.cover = result.cover
        this.lastChapterId = result.chapterid.toInt()
        this.lastChapterName = result.chapterName
    }
}

/**
 * 书籍详情转在线收藏对象
 */
fun BookInfoKSEntity.niceCollectionDataResult(): CollectionDataResult {
    return CollectionDataResult(author = this.author, bookName = this.bookName,
            bookid = this.bookid.toString(), cover = this.cover, resouceType = "KS")
}