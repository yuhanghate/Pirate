package com.yuhang.novel.pirate.extension

import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.repository.database.entity.*
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.*
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.AuthorBooksDataKdResult
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.BookDetailsKdResult
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ChapterListKdResult
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.SearchDataKdResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BookResouceListResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.CollectionDataResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.ReadHistoryDataResult
import com.yuhang.novel.pirate.ui.search.result.SearchResult
import org.joda.time.DateTime
import java.util.*

/**
 * 看书神器章节Bean转成本地数据库
 */
fun ChapterListDataResult.niceBookChapterKSEntity(): List<BookChapterKSEntity> {
    val list = arrayListOf<BookChapterKSEntity>()
    val obj = this
    this.list?.forEach { dir ->
        dir?.list?.forEach { chapter ->
            chapter?.let {
                list.add(
                    BookChapterKSEntity().apply {
                        this.name = chapter.name
                        this.chapterId = chapter.id
                        this.bookId = obj.id
                        this.resouce = "KS"
                    }
                )
            }

        }
    }
    return list
}

fun ChapterListKdResult.niceBookChapterKSEntity(): List<BookChapterKSEntity> {
    val list = arrayListOf<BookChapterKSEntity>()
    val obj = this
    this.chapters.forEach {
        list.add(BookChapterKSEntity().apply {
            this.name = it.title
            this.chapterId = it.link
            this.bookId = obj.book
            this.resouce = "KD"
        })
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
        this.classifyId = 0
    }
}

/**
 * 看书神器 书籍详情Bean转成本地数据库
 */
fun BookDetailsDataResult.niceBookInfoKSEntity(): BookInfoKSEntity {
    val obj = this
    val date = Date(obj.LastTime).time
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
    return RankingDataListResult(
        Author = this.author, Name = this.bookName, Id = this.bookdid, CName = this.chapterName,
        Img = this.cover, Desc = this.desc, Score = this.score
    )
}

/**
 * 最近阅读对象互转
 */
fun ReadHistoryDataResult.niceBookInfoKSEntity(): BookInfoKSEntity {
    val result = this
    return BookInfoKSEntity().apply {
        this.bookName = result.bookName
        this.bookid = result.bookid
        this.description = result.description
        this.author = result.author
        this.cover = result.cover
        this.lastChapterId = result.chapterid
        this.lastChapterName = result.chapterName
    }
}

/**
 * 书籍详情转在线收藏对象
 */
fun BookInfoKSEntity.niceCollectionDataResult(): CollectionDataResult {
    return CollectionDataResult(
        author = this.author, bookName = this.bookName,
        bookid = this.bookid, cover = this.cover, resouceType = "KS"
    )
}

/**
 * 源对象转换
 */
fun BookResouceListResult.niceBookResouceEntity(): BookResouceEntity {
    val result = this
    return BookResouceEntity().apply {
        this.hot = if (result.heat == null) 0 else result.heat!!
        this.checkStatus = result.isCheck
        this.status = if (result.status == null) "" else result.status!!
        this.title = if (result.title == null) "" else result.title!!
        this.website = if (result.websiteUrl == null) "" else result.websiteUrl!!
        this.checkTime = if (result.checkTime == null) 0 else result.checkTime!!
        this.updateTime = if (result.updateTime == null) 0 else result.updateTime!!
        this.resouceRule = if (result.resouceRule == null) "" else result.resouceRule!!
        this.resouceId = result.id
    }
}

/**
 * 源对象转换
 */
fun BookResouceEntity.niceBookResouceListResult(): BookResouceListResult {

    return BookResouceListResult(
        heat = this.hot, isCheck = this.checkStatus, status = this.status,
        title = this.status, websiteUrl = this.website, checkTime = this.checkTime,
        updateTime = this.updateTime, resouceRule = this.resouceRule, id = this.resouceId
    )

}

/**
 * 搜索: 快读->看书
 */
fun SearchDataKdResult.niceBookSearchDataResult(): BookSearchDataResult {

    return BookSearchDataResult(
        Desc = longIntro,
        Img = cover,
        LastChapter = lastChapter,
        Author = author,
        Id = _id,
        CName = cat,
        resouceType = BookConstant.RESOUCE_TYPE_KD,
        Name = title
    )
}

/**
 * 书本详情: 快读 -> 看书
 */
fun BookDetailsKdResult.niceBookDetailsDataResult(): BookDetailsDataResult {
    val status = if (isSerial) "连载" else "完结"
    return BookDetailsDataResult(
        Img = cover,
        Name = title,
        BookStatus = status,
        Desc = longIntro,
        LastChapter = lastChapter,
        Author = author,
        Id = _id,
        CName = cat,
        LastTime = updated,
        SameUserBooks = arrayListOf(),
        BookVote = BookVote(),
        SameCategoryBooks = arrayListOf()
    )
}


fun BookDetailsKdResult.niceBookInfoKSEntity(): BookInfoKSEntity {
    val obj = this
    val date = DateTime(obj.updated).millis

    return BookInfoKSEntity().apply {
        this.bookid = obj._id
        this.cover = obj.cover
        this.bookName = obj.title
        this.author = obj.author
        this.description = obj.longIntro
        this.lastTime = date
        this.firstChapterId = ""
        this.lastChapterName = obj.lastChapter
        this.lastChapterId = ""
        this.bookStatus = if (obj.isSerial) "连载" else "完结"
        this.classifyId = -1
        this.classifyName = obj.cat
    }
}

/**
 * 看书 -> 搜索
 */
fun BookSearchDataResult.niceBooksResult(): BooksResult {
    val obj = this
    return BooksResult().apply {
        this.author = obj.Author
        this.bookKsId = obj.Id
        this.bookName = obj.Name
        this.cover = obj.Img
        this.description = obj.Desc
        this.kind = obj.CName
        this.resouce = "KS"
        this.typeKd = 2
        this.typeKs = 1
    }
}


/**
 * 快读 -> 搜索
 */
fun SearchDataKdResult.niceBooksResult(): BooksResult {
    val obj = this
    return BooksResult().apply {
        this.author = obj.author
        this.bookKdId = obj._id
        this.bookName = obj.title
        this.cover = obj.cover
        this.description = obj.longIntro
        this.kind = obj.cat
        this.resouce = "KD"
        this.typeKd = 1
        this.typeKs = 2
    }
}

fun AuthorBooksDataKdResult.niceBooksResult(): BooksResult {
    val obj = this
    return BooksResult().apply {
        this.author = obj.author
        this.bookKdId = obj._id
        this.bookName = obj.title
        this.cover = obj.cover
        this.description = obj.longIntro
        this.kind = obj.minorCate
        this.resouce = "KD"
        this.typeKd = 1
        this.typeKs = 2
    }
}

fun RecommendBooksResult.niceBooksResult(): BooksResult {
    val obj = this
    return BooksResult().apply {
        this.author = obj.Author
        this.bookKsId = obj.Id
        this.bookName = obj.Name
        this.cover = obj.Img
        this.resouce = "KS"
        this.typeKs = 1
        this.typeKd = 2
    }
}

fun CollectionDataResult.niceBooksResult(): BooksResult {
    val obj = this
    return BooksResult().apply {
        this.author = obj.author
        this.bookName = obj.bookName
        this.cover = obj.cover

        if (obj.resouceType == "KS") {
            this.typeKs = 1
            this.typeKd = 2
            this.bookKsId = obj.bookid
        }
        if (obj.resouceType == "KD") {
            this.typeKd = 1
            this.typeKs = 2
            this.bookKdId = obj.bookid
        }
    }
}

fun BookCollectionKSEntity.niceBooksResult(): BooksResult {
    val obj = this
    return BooksResult().apply {
        if (obj.resouce == "KS") {
            this.bookKsId = obj.bookid
            this.typeKs = 1
            this.typeKd = 2
        }
        if (obj.resouce == "KD") {
            this.bookKdId = obj.bookid
            this.typeKd = 1
            this.typeKs = 2
        }
        this.resouce = obj.resouce
    }

}