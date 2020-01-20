package com.yuhang.novel.pirate.ui.book.viewmodel

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gyf.immersionbar.ImmersionBar
import com.hunter.library.debug.HunterDebug
import com.orhanobut.logger.Logger
import com.umeng.analytics.MobclickAgent
import com.vondear.rxtool.RxNetTool
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
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.StatusResult
import com.yuhang.novel.pirate.ui.book.adapter.ReadBookAdapter
import com.yuhang.novel.pirate.ui.book.fragment.DrawerLayoutLeftFragment
import com.yuhang.novel.pirate.utils.LogUtils
import com.yuhang.novel.pirate.widget.pageview.TextPagerView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


open class ReadBookViewModel : BaseViewModel() {

    val adapter: ReadBookAdapter by lazy { ReadBookAdapter() }

    val log = LogUtils()
    /**
     * 请求参数
     */
    var mBooksResult: BooksResult? = null
    /**
     * 是否收藏
     */
    var isCollection = true


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
    var chapterList: ArrayList<BookChapterKSEntity> = arrayListOf()


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
    fun getBookContent(obj: BooksResult,chapter: BookChapterKSEntity, isCache: Boolean = true
    ): Flowable<BookContentKSEntity> {

        return Flowable.just(chapter).flatMap {
            //返回本地数据
            val contentKSEntity =
                mDataRepository.queryBookContent(mBooksResult?.getBookid()!!, it.chapterId)

            val lastEntity = chapterList.last()
            //如果本地有缓存并且缓存不是最后一章
            //有些章节会出现处理中的内容,进行强制刷新
            //可以手动指定是否缓存
            if (contentKSEntity != null &&  (contentKSEntity.chapterId != lastEntity.chapterId || isCache)) return@flatMap Flowable.just(contentKSEntity)

            //返回服务器数据
            return@flatMap mConvertRepository.getChapterContent(obj, it)
                .map { mDataRepository.insertBookContent(it.apply { lastOpenTime = 0 }) }
                .compose(io_main())
        }.compose(io_main())
    }

    /**
     * 最后一次阅读内容或第一章节内容
     * 可以手动进行强制刷新,不读缓存
     */
    @SuppressLint("CheckResult")
    fun getLastBookContent(obj: BooksResult, isCache:Boolean = true): Flowable<BookContentKSEntity> {
        return Flowable.just(obj)
            .flatMap {
                //查询上次章节内容
                val historyEntity =
                    mDataRepository.queryBookReadHistoryEntity(mBooksResult?.getBookid()!!)


                if (historyEntity != null) {
                    return@flatMap getBookContent(obj, getChapterEntity(historyEntity.chapterid), isCache)
                }

                //默认返回第一章内容
                return@flatMap getBookContent(obj, chapterList[0])
            }.compose(io_main())
    }

    /**
     * 预加载后面章节内容
     * 后台操作,不影响前台显示
     */
    @SuppressLint("CheckResult")
    fun preloadBookContents(obj: BooksResult) {

        Flowable.just(obj)
                //延迟2秒,防止加载死锁
            .delay(10, TimeUnit.SECONDS)
            //查询上次章节内容
            .map {
                return@map mDataRepository.queryBookReadHistoryEntity(mBooksResult?.getBookid()!!)
                    ?: return@map BookReadHistoryEntity().apply {
                        chapterid = chapterList[0].chapterId
                        bookid = obj.getBookid()
                    }
            }
            .map {
                chapterMap.keys.indexOf(it.chapterid)
            }
            .flatMap {
                val list = (it until chapterList.size).map { chapterList[it] }.toList()
                Flowable.fromIterable(list)
            }
            .flatMap {

                //返回本地数据
                val contentKSEntity =
                    mDataRepository.queryBookContent(mBooksResult?.getBookid()!!, it.chapterId)
                if (contentKSEntity != null) return@flatMap Flowable.just(contentKSEntity)

                try {
                    //返回服务器数据
                    val entity = mConvertRepository.downloadChapterContent(obj, it)
                    mDataRepository.insertBookContent(entity.apply { lastOpenTime = 0 })
                } catch (e: Exception) {
                }

                return@flatMap Flowable.just("")

            }
            .compose(io_main())
            .compose(mActivity?.bindToLifecycle())
            .subscribe({}, {}, {})
    }


    /**
     * 更新最后打开内容时间和显示的角标
     */
    @SuppressLint("SimpleDateFormat")
    fun updateLastOpenTimeAndPosition(chapterid: String, lastContentPosition: Int) {
        thread {
            mDataRepository.updateLocalREadHistory(mBooksResult?.getBookid()!!, chapterid)
            mDataRepository.updateLastOpenContent(
                mBooksResult?.getBookid()!!,
                chapterid,
                lastContentPosition
            )
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
            .setPageType(getPageType())
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
                mDataRepository.queryCollection(mBooksResult?.getBookid()!!) != null
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
    fun postCollection(obj: BooksResult) {
        if (TextUtils.isEmpty(PirateApp.getInstance().getToken())) return
        Flowable.just(mBooksResult?.getBookid()!!)
            .map { mDataRepository.queryBookInfo(mBooksResult?.getBookid()!!) }

            .flatMap {
                mDataRepository.addCollection(
                    bookid = it.bookid,
                    bookName = it.bookName,
                    author = it.author,
                    cover = it.cover,
                    description = it.description,
                    bookStatus = it.bookStatus,
                    classifyName = it.classifyName,
                    resouceType = obj.resouce
                )
            }
            .compose(mActivity?.bindToLifecycle())
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
    fun updateReadHistory(
        chapterid: String,
        chapterName: String,
        obj: BooksResult
    ): Flowable<StatusResult> {
        return Flowable.just("")
            .map { mDataRepository.queryBookInfo(mBooksResult?.getBookid()!!) }
            .flatMap {
                mDataRepository.updateBookInfoLastReadTime(it.bookid)
                mDataRepository.updateReadHistory(
                    bookName = it.bookName,
                    bookid = it.bookid,
                    chapterid = chapterid,
                    chapterName = chapterName,
                    author = it.author,
                    cover = it.cover,
                    description = it.description,
                    resouceType = obj.resouce,
                    content = ""
                )
            }.compose(io_main())

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
        return chapterList.size - 1
    }

    /**
     * 重围更新标签
     */
    fun clearLable() {
        mDataRepository.clearLable(mBooksResult?.getBookid()!!)
    }

    /**
     * 友盟自定义统计
     */
    @SuppressLint("SimpleDateFormat")
    fun postUM() {
        thread {
            val info = mDataRepository.queryBookInfo(mBooksResult?.getBookid()!!)
            val time = Date()
            val book = HashMap<String, Any>()
            book[BOOK_NAME] = info?.bookName ?: mBooksResult?.getBookid()!! //小说名称
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
    fun initChapterList(obj: BooksResult, reload: Boolean): Flowable<Unit> {

        return Flowable.just("")
            .flatMap {
                //重新从服务器加载
                if (reload) {
                    return@flatMap mConvertRepository.getChapterList(obj).map {
                        mDataRepository.deleteChapterList(it[0].bookId)
                        mDataRepository.insertChapterList(it)
                        it
                    }.compose(io_main())
                }
                //从本地获取
                val list = mDataRepository.queryChapterObjList(obj.getBookid())

                //如果已经看到最后章节记录了,每次进去都刷新章节
                //如果没有看到最后章节,就不加载.等更新了加载.
                //快读更新不及时,部分源更新了,他的还没
                mDataRepository.queryBookReadHistoryEntity(mBooksResult?.getBookid()!!)
                    ?.let { entity ->
                        list.forEachIndexed { index, bookChapterKSEntity ->
                            if (bookChapterKSEntity.chapterId == entity.chapterid && index >= list.size - 1) {

                                //没有网络,返回本地章节数据
                                if (RxNetTool.isAvailable(mActivity)) {
                                    return@flatMap mConvertRepository.getChapterList(obj).map {
                                        mDataRepository.deleteChapterList(it[0].bookId)
                                        mDataRepository.insertChapterList(it)
                                        it
                                    }.compose(io_main())
                                }
                                return@flatMap Flowable.just(list)

                            }
                        }
                    }

                if (list.isNotEmpty()) return@flatMap Flowable.just(list)

                //从服务器获取
                return@flatMap mConvertRepository.getChapterList(obj).map {
                    mDataRepository.deleteChapterList(it[0].bookId)
                    mDataRepository.insertChapterList(it)
                    mDataRepository.queryChapterObjList(obj.getBookid())
                }.compose(io_main())
            }
            .map {
                chapterMap.clear()
                chapterList.clear()
                chapterList.addAll(it)
                it.forEach { chapterMap[it.chapterId] = it }
            }
            .compose(io_main())
    }

    /**
     * 初始化缓存状态
     * 后台刷新状态
     * 不跟前台UI抢资源
     */
    fun setCacheChapter(fragment: DrawerLayoutLeftFragment?) {
        //单独查询.懒加载.防止多线程锁死
        Flowable.just("")
            .delay(5, TimeUnit.SECONDS)
            .map {
                chapterList.forEach {
                    val content = mDataRepository.queryBookContent(it.bookId, it.chapterId)
                    if (content != null) it.hasContent = 1 else it.hasContent = 0
                    chapterMap[it.chapterId] = it
                    mDataRepository.updateChapter(it)
                }
            }
            .compose(io_main())
            .compose(mActivity?.bindToLifecycle())
            .subscribe({
                fragment?.chapterList = chapterList
                fragment?.setRefreshView()
            }, {})
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
        if (index > chapterList.size - 1) {
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

    /**
     * 是否还有上一页
     * true:有上一页
     * false:没有上一页
     */
    fun hasPreviousPage(chapterid: String): Boolean {
        val index = chapterMap.values.indexOf(chapterMap[chapterid]) + 1
        return index > 0
    }

    /**
     * newIntent进来的.数据都重新初始化
     */
    fun clearData() {

        adapter.getList().clear()
        chapterList.clear()
        chapterMap.clear()
    }

    /**
     * 下载小说
     */
    fun downloadBook(obj: BooksResult, chapterid: String = "") {
        mDataRepository.startWorker(obj.apply { this.lastChapterId = chapterid })
    }

    /**
     * 左右翻页/上下翻页
     */
    private fun getPageType(): Int {
        return PirateApp.getInstance().getPageType()
    }
}