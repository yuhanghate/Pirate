package com.yuhang.novel.pirate.repository.network.convert

import com.google.gson.Gson
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.extension.niceBookChapterKSEntity
import com.yuhang.novel.pirate.extension.niceBookInfoKSEntity
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.extension.niceDir
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ChapterListResult
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ChapterListKdResult
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ResouceListKdResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import io.reactivex.Flowable

class ConvertRepository {

    /**
     * 看书
     */
    val mKanShuNetApi by lazy { PirateApp.getInstance().getDataRepository().getKSNetApi() }

    /**
     * 快读
     */
    val mKuaiDuNetApi by lazy { PirateApp.getInstance().getDataRepository().getKuaiDuApi() }

    /**
     * 搜索
     */
    fun getSearchResult(type: String, keyword: String): Flowable<List<BooksResult>> {
        return when (type) {
            "KS" -> mKanShuNetApi.searchBook(keyword).map {
                it.data.map { it.niceBooksResult() }.toList()
            }
            "KD" -> mKuaiDuNetApi.search(
                hashMapOf(
                    "key" to keyword,
                    "start" to "0",
                    "limit" to "100"
                )
            )
                .map { it.books.map { it.niceBooksResult() }.toList() }
            else -> Flowable.error<List<BooksResult>> { NullPointerException("搜索信息异常") }
        }

    }

    /**
     * 获取详情页
     */
    fun getDetailsInfo(obj: BooksResult): Flowable<BookInfoKSEntity> {
        return when {
            //看书源
            obj.isKanShu() -> mKanShuNetApi.getBookDetails(
                dirId = niceDir(obj.bookKsId),
                bookId = obj.bookKsId.toLong()
            )
                .map { it.data.niceBookInfoKSEntity() }
            //快读源
            obj.isKuaiDu() -> mKuaiDuNetApi.getBookDetails(obj.bookKdId).map { it.niceBookInfoKSEntity() }
            else -> Flowable.error<BookInfoKSEntity> { NullPointerException("没有源信息") }
        }
    }


    /**
     *获取章节列表
     */
    fun getChapterList(obj: BooksResult): Flowable<List<BookChapterKSEntity>> {
        return when {
            obj.isKanShu() -> mKanShuNetApi.getBookChapterList(
                dirId = niceDir(obj.bookKsId),
                bookId = obj.bookKsId.toLong()
            )
                .map { it.replace("},]}}", "}]}}") }
                .flatMap { Flowable.just(Gson().fromJson(it, ChapterListResult::class.java)) }
                .map { it.data.niceBookChapterKSEntity() }
            obj.isKuaiDu() -> mKuaiDuNetApi.getChapterList(obj.bookKdId)
                .map { it.niceBookChapterKSEntity() }
            else -> Flowable.error<List<BookChapterKSEntity>> { NullPointerException("没有章节信息") }
        }
    }

    /**
     * 获取章节对应的内容
     */
    fun getChapterContent(
        obj: BooksResult,
        chapter: BookChapterKSEntity
    ): Flowable<BookContentKSEntity> {
        return when {
            obj.isKanShu() -> mKanShuNetApi.getChapterContent(
                niceDir(obj.bookKsId),
                obj.bookKsId.toLong(),
                chapter.chapterId
            ).map {
                BookContentKSEntity().apply {
                    this.chapterId = chapter.chapterId
                    this.content = it.data.content
                    this.bookId = obj.bookKsId
                    this.lastContentPosition = 0
                    this.chapterName = it.data.cname
                    this.resouce = obj.resouce
                }
            }
            obj.isKuaiDu() -> mKuaiDuNetApi.getResouceContent(chapter.chapterId)
                .map {
                    BookContentKSEntity().apply {
                        this.chapterId = chapter.chapterId
                        this.content = it.chapter.body!!
                        this.bookId = obj.bookKdId
                        this.lastContentPosition = 0
                        this.chapterName = chapter.name
                        this.resouce = obj.resouce
                    }
                }
            else -> Flowable.error<BookContentKSEntity> { NullPointerException("没有内容信息") }
        }
    }

    /**
     * 下载内容
     */
    fun downloadChapterContent(
        obj: BooksResult,
        chapter: BookChapterKSEntity
    ): BookContentKSEntity {
        return when {
            obj.isKanShu() -> {
                val content = mKanShuNetApi.downloadChapterContent(
                    niceDir(obj.bookKsId),
                    obj.bookKsId.toLong(),
                    chapter.chapterId
                ).execute().body()
                return BookContentKSEntity().apply {
                    this.chapterId = chapter.chapterId
                    this.content = content?.data?.content!!
                    this.bookId = obj.bookKsId
                    this.lastContentPosition = 0
                    this.chapterName = content?.data?.cname!!
                    this.resouce = obj.resouce
                }
            }
            obj.isKuaiDu() -> {
                val content = mKuaiDuNetApi.downloadChapterContent(chapter.chapterId).execute().body()
                return BookContentKSEntity().apply {
                    this.chapterId = chapter.chapterId
                    this.content = content?.chapter?.body!!
                    this.bookId = obj.bookKdId
                    this.lastContentPosition = 0
                    this.chapterName = chapter.name
                    this.resouce = obj.resouce
                }
            }


            else -> BookContentKSEntity()
        }
    }

    /**
     * 作者相关作品
     */
    fun getAuthorBooksList(obj: BooksResult): Flowable<List<BooksResult>> {
        return when {
            //看书源
            obj.isKanShu() -> mKanShuNetApi.getBookDetails(
                dirId = niceDir(obj.bookKsId),
                bookId = obj.bookKsId.toLong()
            ).map { it.data.SameUserBooks.filterNotNull().map { it.niceBooksResult() }.toList() }

            //快读
            obj.isKuaiDu() -> mKuaiDuNetApi.getAuthorBookAll(
                hashMapOf(
                    "author" to obj.author.trim(),
                    "start" to "0",
                    "limit" to "50"
                )
            ).map {
                it.books.filter {
                    it.title != obj.bookName
                }.map {
                    it.niceBooksResult()
                }.toList()
            }
            else -> Flowable.error<List<BooksResult>> { NullPointerException("作者全部作品失败") }
        }
    }


    /**
     * 源列表
     */
    fun getResouceList(bookid: String):Flowable<List<ResouceListKdResult>> {
        return mKuaiDuNetApi.getResouceList(bookid)
    }


    /**
     * 第三方源目录列表
     */
    fun getResouceChapterList(tocId:String):Flowable<ChapterListKdResult> {
        return mKuaiDuNetApi.getResouceChapterList(tocId)
    }

}


