package com.yuhang.novel.pirate.repository.network.convert

import com.google.gson.Gson
import com.tamsiree.rxkit.RxEncodeTool
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.extension.*
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ChapterListResult
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ResouceListKdResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.SearchSuggestResult

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
     * 随便看书
     */
    val mPirateNetApi by lazy { PirateApp.getInstance().getDataRepository().getNetApi() }

    /**
     * 数据库
     */
    private val mDatabase by lazy { PirateApp.getInstance().getDataRepository().getDatabase() }

    /**
     * 搜索
     */
    suspend fun getSearchResult(type: String, keyword: String): List<BooksResult> {
        return when (type) {
            "KS" -> mKanShuNetApi.searchBook(keyword).data.map { it.niceBooksResult() }.toList()
            "KD" -> mKuaiDuNetApi.search(hashMapOf("key" to keyword,
                "start" to "0",
                "limit" to "100")
            ).books.map { it.niceBooksResult() }.toList()
            else -> arrayListOf()
        }

    }

    /**
     * 获取详情页
     */
    suspend fun getDetailsInfo(obj: BooksResult): BookInfoKSEntity {
        return when {
            //看书源
            obj.isKanShu() -> mKanShuNetApi.getBookDetails(
                dirId = niceDir(obj.bookKsId),
                bookId = obj.bookKsId.toLong()
            ).data.niceBookInfoKSEntity()
            //快读源
            obj.isKuaiDu() -> mKuaiDuNetApi.getBookDetails(obj.bookKdId).niceBookInfoKSEntity()
            else -> BookInfoKSEntity()
        }
    }


    /**
     *获取章节列表
     */
    suspend fun getChapterList(obj: BooksResult): List<BookChapterKSEntity> {
        return when {

            //看书
            obj.isKanShu() -> {

                //数据格式有问题,需要转换
                val data = mKanShuNetApi.getBookChapterList(
                    dirId = niceDir(obj.bookKsId),
                    bookId = obj.bookKsId.toLong()
                ).replace("},]}}", "}]}}")

                //序列化
                val result = Gson().fromJson(data, ChapterListResult::class.java)
                result.data.niceBookChapterKSEntity()
            }

            //快读
            obj.isKuaiDu() -> {
                arrayListOf()
            }

            //小黄书
            obj.isSex() -> {
                mPirateNetApi.getBookSexChapter(niceBody(hashMapOf("bookId" to obj.bookSexId)))
                    .data.list.map { it.niceBookChapterKSEntity() }.toList()
            }

            else -> arrayListOf()
        }
    }

    /**
     * 获取章节对应的内容
     */
    suspend fun getChapterContent(
        obj: BooksResult,
        chapter: BookChapterKSEntity,
    ): BookContentKSEntity {
        return when {
            //看书
            obj.isKanShu() -> mKanShuNetApi.getChapterContent(
                niceDir(obj.bookKsId),
                obj.bookKsId.toLong(),
                chapter.chapterId
            ).niceBookContentKSEntity(obj, chapter)

            //快读
            obj.isKuaiDu() -> mKuaiDuNetApi.getResouceContent(RxEncodeTool.urlEncode(chapter.chapterId))
                .niceBookContentKSEntity(obj, chapter)
            //小黄书
            obj.isSex() -> mPirateNetApi.getBookSexContent(niceBody(hashMapOf("chapterId" to chapter.chapterId)))
                .data.niceBookContentKSEntity()
            else -> BookContentKSEntity()
        }
    }

    /**
     * 下载内容
     */
    @Synchronized
    fun downloadChapterContent(
        obj: BooksResult,
        chapter: BookChapterKSEntity,
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
                    this.chapterName = content.data?.cname!!
                    this.resouce = obj.resouce
                }
            }
            obj.isKuaiDu() -> {
                val content =
                    mKuaiDuNetApi.downloadChapterContent(RxEncodeTool.urlEncode(chapter.chapterId))
                        .execute().body()
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
    suspend fun getAuthorBooksList(obj: BooksResult): List<BooksResult> {
        return when {
            //看书源
            obj.isKanShu() ->
                mKanShuNetApi.getBookDetails(
                    dirId = niceDir(obj.bookKsId),
                    bookId = obj.bookKsId.toLong()
                ).data.SameUserBooks?.map {
                    it.niceBooksResult()
                }?.toList() ?: arrayListOf()

            //快读
            obj.isKuaiDu() -> mKuaiDuNetApi.getAuthorBookAll(
                hashMapOf(
                    "author" to obj.author.trim(),
                    "start" to "0",
                    "limit" to "50"
                )
            ).books.filter {
                it.title != obj.bookName
            }.map {
                it.niceBooksResult()
            }.toList()
            else -> arrayListOf()
        }
    }


    /**
     * 源列表
     */
    suspend fun getResouceList(obj: BooksResult): List<ResouceListKdResult> {
        if (obj.isKanShu()) {
            //如果是看书,去服务器转换一下
            val result =
                mPirateNetApi.getBooksSearch(niceBody(hashMapOf("bookid" to obj.getBookid())))
            mKuaiDuNetApi.getResouceList(result.data.bookKdId)
        }
        return mKuaiDuNetApi.getResouceList(obj.getBookid())
    }


    /**
     * 第三方源目录列表
     */
    suspend fun getResouceChapterList(
        tocId: String, bookid: String,
    ): List<BookChapterKSEntity> {
        return mKuaiDuNetApi.getResouceChapterList(tocId).chapters.map {
            it.niceBookChapterKSEntity(bookid)
        }.toList()
    }



    /**
     * 主页小说刷新 看书源
     */
    suspend fun updateBookKS(bookid: String): BookInfoKSEntity {
        return mKanShuNetApi.getBookDetails(
            dirId = niceDir(bookid),
            bookId = bookid.toLong()
        ).data.niceBookInfoKSEntity()
    }

    /**
     * 搜索模糊匹配
     */
    suspend fun searchSuggest(keyword: String): List<SearchSuggestResult> {
        return  mKuaiDuNetApi.searchSuggest(keyword).keywords
    }

}


