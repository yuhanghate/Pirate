package com.yuhang.novel.pirate.extension

import android.text.TextUtils
import com.google.gson.Gson
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.repository.database.entity.*
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.*
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ChapterListDataResult
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.*
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.BookCategoryDataResult
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
        this.lastChapterName = obj.LastChapter
        this.bookStatus = obj.BookStatus
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
        this.lastChapterName = result.chapterName
        this.resouce = result.resouceType
    }
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
        this.lastChapterName = obj.lastChapter
        this.bookStatus = if (obj.isSerial) "连载" else "完结"
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
        this.author = obj.author ?: ""
        this.bookKdId = obj._id
        this.bookName = obj.title ?: ""
        this.cover = obj.cover ?: ""
        this.description = obj.longIntro ?: ""
        this.kind = obj.minorCate ?: ""
        this.resouce = "KD"
        this.typeKd = 1
        this.typeKs = 2
    }
}

fun BooksKSResult.niceBooksResult(): BooksResult {
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

/**
 * 收藏 -> 快读子渠道
 */
fun CollectionDataResult.niceBookResouceTypeKDEntity(): BookResouceTypeKDEntity? {
    val obj = this
    if (obj.resouceType != "KD" || TextUtils.isEmpty(obj.tocId)) return null
    return BookResouceTypeKDEntity().apply {
        if (obj.resouceType == "KD") {
            this.bookid = obj.bookid
            this.typeName = obj.tocName ?: ""
            this.tocId = obj.tocId ?: ""
            this.resouce = obj.resouceType
            this.bookName = obj.bookName ?: ""
        }
    }
}

fun ReadHistoryDataResult.niceBookResouceTypeKDEntity(): BookResouceTypeKDEntity? {
    val obj = this
    if (obj.resouceType != "KD" || TextUtils.isEmpty(obj.tocId)) return null
    return BookResouceTypeKDEntity().apply {
        if (obj.resouceType == "KD") {
            this.bookid = obj.bookid
            this.typeName = obj.tocName
            this.tocId = obj.tocId
            this.resouce = obj.resouceType
            this.bookName = obj.bookName ?: ""
        }
    }
}

fun CollectionDataResult.niceBooksResult(): BooksResult {
    val obj = this
    return BooksResult().apply {
        this.author = obj.author ?: ""
        this.bookName = obj.bookName ?: ""
        this.cover = obj.cover ?: ""
        this.resouce = obj.resouceType

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

/**
 * 收藏 -> 书籍
 */
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

/**
 *
 */
fun ChapterListDataKdResult.niceBookChapterKSEntity(bookid: String): BookChapterKSEntity {
    val obj = this
    return BookChapterKSEntity().apply {
        this.chapterId = obj.link
        this.resouce = "KD"
        this.bookId = bookid
        this.name = obj.title
    }
}

/**
 * 快读更新 -> 小说详情
 */
fun BookUpdateKdResult.niceBookInfoKSEntity(): BookInfoKSEntity {
    val obj = this
    return BookInfoKSEntity().apply {
        this.bookid = obj._id
        this.author = obj.author
        this.lastChapterName = obj.lastChapter
        this.lastTime = DateTime(obj.updated).millis
    }
}


fun BookDetailsDataResult.niceBooksResult(): BooksResult {
    val obj = this
    return BooksResult().apply {
        this.bookKsId = obj.Id
        this.typeKs = 1
        this.typeKd = 2
        this.resouce = "KS"
        this.author = obj.Author
        this.lastChapterName = obj.LastChapter
        this.lastTime = DateTime(obj.LastTime).millis
    }
}

fun BookDownloadEntity.niceBookResult(): BooksResult {
    val obj = this
    return BooksResult().apply {
        this.bookName = obj.bookName
        this.author = obj.author
        this.cover = obj.cover
        if (obj.resouce == "KS") {
            this.resouce = "KS"
            this.typeKs = 1
            this.typeKd = 2
            this.bookKsId = obj.bookId
        }
        if (obj.resouce == "KD") {
            this.resouce = "KD"
            this.typeKd = 1
            this.typeKs = 2
            this.bookKdId = obj.bookId
        }
    }
}

/**
 * 书单 -> 书本对象
 */
fun ShuDanDetailDataResult.niceBooksKSResult(): BooksKSResult {
    val obj = this
    return BooksKSResult().apply {
        this.Author = obj.Author
        this.BookStatus = ""
        this.CName = obj.CategoryName
        this.Desc = obj.Description
        this.Id = obj.BookId.toString()
        this.Img = obj.BookImage
        this.Score = obj.Score
        this.Name = obj.BookName
    }
}

fun BookCategoryDataResult.niceCategoryKDEntity(): CategoryKDEntity {
    val obj = this
    return CategoryKDEntity().apply {
        this.bookCover = Gson().toJson(obj.bookCover)
        this.count = obj.count
        this.gender = obj.gender
        this.majorCate = obj.majorCate
        this.order = obj.order
    }
}

fun BookInfoKSEntity.niceBooksResult(): BooksResult {
    val obj = this
    return BooksResult().apply {
        this.author = obj.author
        this.bookName = obj.bookName
        this.cover = obj.cover
        this.description = obj.description
        this.resouce = obj.resouce
        if (obj.resouce == "KS") {
            this.typeKd = 2
            this.typeKs = 1
            this.bookKsId = obj.bookid
        } else {
            this.typeKd = 1
            this.typeKs = 2
            this.bookKdId = obj.bookid
        }

    }
}

fun BooksKSResult.niceBooksKSEntity(): BooksKSEntity {
    val obj = this
    return BooksKSEntity().apply {
        this.Author = obj.Author
        this.BookStatus = obj.BookStatus
        this.CName = obj.CName
        this.Desc = obj.Desc
        this.bookId = obj.Id
        this.Img = obj.Img
        this.LastChapter = obj.LastChapter
        this.LastChapterId = obj.LastChapterId
        this.Name = obj.Name
        this.Score = obj.Score
    }
}

fun BooksKSEntity.niceBooksKSResult(): BooksKSResult {
    val obj = this
    return BooksKSResult().apply {
        this.Author = obj.Author
        this.BookStatus = obj.BookStatus
        this.CName = obj.CName
        this.Desc = obj.Desc
        this.Id = obj.bookId
        this.Img = obj.Img
        this.LastChapter = obj.LastChapter
        this.LastChapterId = obj.LastChapterId
        this.Name = obj.Name
        this.Score = obj.Score
    }
}