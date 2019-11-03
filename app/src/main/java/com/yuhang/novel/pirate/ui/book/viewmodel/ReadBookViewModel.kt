package com.yuhang.novel.pirate.ui.book.viewmodel

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.umeng.analytics.MobclickAgent
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.constant.UMConstant.TypeBook.BOOK_BACKGROUND
import com.yuhang.novel.pirate.constant.UMConstant.TypeBook.BOOK_CHANPTER
import com.yuhang.novel.pirate.constant.UMConstant.TypeBook.BOOK_FONT
import com.yuhang.novel.pirate.constant.UMConstant.TypeBook.BOOK_NAME
import com.yuhang.novel.pirate.constant.UMConstant.TypeBook.BOOK_READ_TIME
import com.yuhang.novel.pirate.constant.UMConstant.TypeBook.BOOK_READ_TIME_STAMP
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookReadHistoryEntity
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ResouceListKdResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.StatusResult
import com.yuhang.novel.pirate.ui.book.adapter.ReadBookAdapter
import com.yuhang.novel.pirate.widget.pageview.TextPagerView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class ReadBookViewModel : BaseViewModel() {

    val TAG = ReadBookViewModel::class.java.simpleName
    val adapter: ReadBookAdapter by lazy { ReadBookAdapter() }


    /**
     * 是否收藏
     */
    var isCollection = false

    /**
     * 小说id
     */
    var bookid: String = ""

    /**
     * 小说名称
     */
    var bookName: String = ""

    /**
     * 当前章节id
     */
    var chapterid = ""

    /**
     * 当前章节名称
     */
    var chapterName = ""

    /**
     * 当前角标
     */
    var currentPosition = -1

    /**
     * 章节列表
     */
    var chapterList: List<BookChapterKSEntity> = arrayListOf()

    /**
     * 源列表
     */
    var resouceList : ArrayList<ResouceListKdResult> = arrayListOf()

    var chapterMap = linkedMapOf<String, BookChapterKSEntity>()

    /**
     * 初始化书名信息
     */
    fun initBookNameData(obj: BooksResult) {
        thread { bookName = mDataRepository.queryBookInfo(obj.getBookid())?.bookName ?: "" }
    }

    /**
     * 根据章节id查询内容
     */
    fun getBookContent(
        obj: BooksResult,
        chapter: BookChapterKSEntity
    ): Flowable<BookContentKSEntity> {

        return Flowable.just(chapter).flatMap {
            //返回本地数据
            val contentKSEntity = mDataRepository.queryBookContent(bookid, it.chapterId)
            if (contentKSEntity != null) return@flatMap Flowable.just(contentKSEntity)

            //返回服务器数据
            return@flatMap mConvertRepository.getChapterContent(obj, it)
                .map { mDataRepository.insertBookContent(it.apply { lastOpenTime = 0 }) }
                .compose(io_main())
        }.compose(io_main())
    }

    /**
     * 最后一次阅读内容或第一章节内容
     */
    @SuppressLint("CheckResult")
    fun getLastBookContent(obj: BooksResult): Flowable<BookContentKSEntity> {
        return Flowable.just(obj)
            .flatMap {

                //查询上次章节内容
                val historyEntity = mDataRepository.queryBookReadHistoryEntity(bookid)
                if (historyEntity != null) return@flatMap getBookContent(
                    obj,
                    getChapterEntity(historyEntity.chapterid)
                )

                //默认返回第一章内容
                return@flatMap getBookContent(obj, chapterList[0])
            }.compose(io_main())
    }

    /**
     * 预加载后面章节内容
     */
    @SuppressLint("CheckResult")
    fun preloadBookContents(obj: BooksResult) {

        Flowable.just(obj)
            //查询上次章节内容
            .map {
                return@map mDataRepository.queryBookReadHistoryEntity(bookid)
                    ?: return@map BookReadHistoryEntity().apply {
                        chapterid = chapterList[0].chapterId
                        bookid = obj.getBookid()
                    }
            }
            .map { chapterMap.keys.indexOf(it.chapterid) }
            .flatMap {
                val list = (it until chapterList.size).map { chapterList[it] }.toList()
                Flowable.fromIterable(list)
            }
            .flatMap {

                //返回本地数据
                val contentKSEntity = mDataRepository.queryBookContent(bookid, it.chapterId)
                if (contentKSEntity != null) return@flatMap Flowable.just(contentKSEntity)

                try {
                    //返回服务器数据
                    val entity = mConvertRepository.downloadChapterContent(obj, it)
                    mDataRepository.insertBookContent(entity.apply { lastOpenTime = 0 })
                } catch (e: Exception) {
                }

                return@flatMap Flowable.just("")

            }.subscribeOn(Schedulers.io()).compose(mActivity?.bindToLifecycle())
            .subscribe({}, {}, {})

    }

    /**
     * 初始化源列表
     */
    @SuppressLint("CheckResult")
    fun initResouceList() {

        mConvertRepository.getResouceList(bookid)
            .compose(io_main())
            .compose(mActivity?.bindToLifecycle())
            .subscribe({
                resouceList.clear()
                resouceList.addAll(it)
            },{})
    }


    /**
     * 更新最后打开内容时间和显示的角标
     */
    @SuppressLint("SimpleDateFormat")
    fun updateLastOpenTimeAndPosition(chapterid: String, lastContentPosition: Int) {
        thread {
            mDataRepository.updateLocalREadHistory(bookid, chapterid)
            mDataRepository.updateLastOpenContent(bookid, chapterid, lastContentPosition)
        }

    }


    /**
     * 当前列表中是否包括指定章节
     * 用来过滤重复加载
     */
    fun isLoadAdapter(chapterid: String): Boolean {
        adapter.getList().forEach {
            if (it.chapterId == chapterid) {
                return true
            }
        }
        return false
    }

    /**
     * 最后一个列表角标
     */
    fun getLastItemPosition(): Int {
        return if (adapter.getList().isEmpty()) 0 else adapter.getList().size - 1
    }

    /**
     * 章节根据内容分成小页
     */
    fun getTxtPageList(
        pagerView: TextPagerView,
        obj: BookContentKSEntity
    ): List<BookContentKSEntity> {


        val margin = mActivity?.niceDp2px(20f) ?: return emptyList()
        pagerView.textSize = BookConstant.getPageTextSize()
        postUM()



        return pagerView
            .setTitle(obj.chapterName)
            .setMargin(margin, margin, ImmersionBar.getStatusBarHeight(mActivity!!), 0)
            .setContent(obj.content)
            .build2().map {
                BookContentKSEntity().apply {
                    chapterName = obj.chapterName
                    bookId = obj.bookId
                    content = obj.content
                    chapterId = obj.chapterId
                    textPageBean = it
                }
            }.toList()
    }

    /**
     * 除掉-1和最后一页
     * 以外都是有效范围
     */
    fun getIndexValid(position: Int): Int {

        return when {
            position < 0 -> 0
            position > getLastItemPosition() -> getLastItemPosition()
            else -> position
        }
    }

    /**
     * 第一个可见的
     */
    fun getFirstVisiblePosition(recyclerview: RecyclerView): Int {
        val manager = recyclerview.layoutManager as LinearLayoutManager
        return manager.findFirstVisibleItemPosition()
    }

    fun getLastVisiblePosition(recyclerview: RecyclerView): Int {
        val manager = recyclerview.layoutManager as LinearLayoutManager
        return manager.findLastVisibleItemPosition()
    }

    /**
     * 是否收藏书箱
     */
    @SuppressLint("CheckResult")
    fun isCollectionBook() {
        Flowable.just("")
            .map {
                mDataRepository.queryCollection(bookid) != null
            }
            .compose(io_main())
            .compose(mActivity?.bindToLifecycle())
            .subscribe({
                isCollection = it
            }, {})

    }

    /**
     * 增加收藏
     */
    @SuppressLint("CheckResult")
    fun insertCollection(obj: BooksResult): Flowable<Boolean> {
        return Flowable.just(obj.getBookid())
            .map {
                mDataRepository.insertCollection(obj)
                return@map true
            }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    /**
     * 收藏到服务器
     */
    @SuppressLint("CheckResult")
    fun postCollection() {
        if (TextUtils.isEmpty(PirateApp.getInstance().getToken())) return
        Flowable.just(bookid)
            .map { mDataRepository.queryBookInfo(bookid) }
            .compose(mActivity?.bindToLifecycle())
            .flatMap {
                mDataRepository.addCollection(
                    bookid = it.bookid.toString(),
                    bookName = it.bookName,
                    author = it.author,
                    cover = it.cover,
                    description = it.description,
                    bookStatus = it.bookStatus,
                    classifyName = it.classifyName,
                    resouceType = "KS"
                )
            }
            .compose(io_main())
            .subscribe({
                Logger.i("")
            }, {
                Logger.i("")
            })
    }

    /**
     * 更新阅读记录
     */
    @SuppressLint("CheckResult")
    fun updateReadHistory(chapterid: String, chapterName: String): Flowable<StatusResult> {
        return Flowable.just("")
            .map { mDataRepository.queryBookInfo(bookid) }
            .flatMap {

                mDataRepository.updateReadHistory(
                    bookName = it.bookName,
                    bookid = it.bookid,
                    chapterid = chapterid,
                    chapterName = chapterName,
                    author = it.author,
                    cover = it.cover,
                    description = it.description,
                    resouceType = "KS",
                    content = mDataRepository.queryBookContent(bookid, chapterid)?.content!!
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }


    /**
     * 获取当前章节在章节列表进度
     */
    fun getChapterIndex(): Int {
        chapterList.forEachIndexed { index, bookChapterKSEntity ->
            if (bookChapterKSEntity.chapterId == chapterid) {
                return index
            }
        }
        return 1
    }

    /**
     * 重围更新标签
     */
    fun clearLable() {
        mDataRepository.clearLable(bookid)
    }

    /**
     * 友盟自定义统计
     */
    @SuppressLint("SimpleDateFormat")
    fun postUM() {
        thread {
            val info = mDataRepository.queryBookInfo(bookid)
            val time = Date()
            val book = HashMap<String, Any>()
            book[BOOK_NAME] = info?.bookName ?: bookid.toString() //小说名称
            book[BOOK_CHANPTER] = chapterName  //章节名称
            book[BOOK_READ_TIME] = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(time) //阅读时间格式化
            book[BOOK_READ_TIME_STAMP] = time.time //阅读时间戳
            book[BOOK_BACKGROUND] = BookConstant.getPageBackground() //页面背景
            book[BOOK_FONT] = BookConstant.getPageTextSize() //小说字体
            mActivity?.runOnUiThread {
                MobclickAgent.onEventObject(mActivity, UMConstant.TYPE_BOOK, book)
            }
        }
    }


    /**
     * 加载章节列表
     */
    @SuppressLint("CheckResult")
    fun initChapterList(obj: BooksResult): Flowable<Unit> {

        return Flowable.just(obj.getBookid())
            .flatMap {

                //从本地获取
                val list = mDataRepository.queryChapterObjList(obj.getBookid())
                if (list.isNotEmpty()) return@flatMap Flowable.just(list)

                //从服务器获取
                return@flatMap mConvertRepository.getChapterList(obj)
            }
            .map {
                chapterList = it
                it.forEach { chapterMap[it.chapterId] = it }
            }
            .compose(io_main())
    }


    /**
     * 获取章节名称
     */
    fun getChapterEntity(chapterid: String): BookChapterKSEntity {
        chapterList.filter { it.chapterId == chapterid }.forEach { return it }
        return chapterList[0]
    }

    /***
     * 上一页
     */
    fun getPreviousPage(chapterid: String): BookChapterKSEntity {
        val index = chapterMap.values.indexOf(chapterMap[chapterid]) - 1
        if (index < 0) {
            return chapterList[0]
        }
        return chapterList[index]
    }

    /**
     * 下一页
     */
    fun getNextPage(chapterid: String): BookChapterKSEntity {
        val index = chapterMap.values.indexOf(chapterMap[chapterid]) + 1
        if (index > chapterMap.size - 1) {
            return chapterList[0]
        }
        return chapterList[index]
    }

    /**
     * 是否还有下一页
     */
    fun hasNextPage(chapterid: String): Boolean {
        val index = chapterMap.values.indexOf(chapterMap[chapterid]) + 1
        return index <= chapterMap.size - 1
    }
}