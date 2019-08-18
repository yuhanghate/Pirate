package com.yuhang.novel.pirate.ui.book.viewmodel

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.trello.rxlifecycle2.android.ActivityEvent
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.constant.BookKSConstant
import com.yuhang.novel.pirate.extension.niceBookContentKSEntity
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import com.yuhang.novel.pirate.ui.book.adapter.ReadBookAdapter
import com.yuhang.novel.pirate.widget.pageview.TextPagerView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
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
    var bookid = -1

    /**
     * 当前章节id
     */
    var chapterid = -1

    /**
     * 当前章节名称
     */
    var chapterName = ""

    /**
     * 上一页
     */
    var pid = -1

    /**
     * 下一页
     */
    var nid = -1

    /**
     * 当前角标
     */
    var currentPosition = -1


    /**
     * 根据id获取内容
     */
    @SuppressLint("CheckResult")
    fun getContentFromChapterid(chapterid: Int): Flowable<BookContentKSEntity> {

        return Flowable.just(chapterid)
            .flatMap {
                val contentKSEntity = mDataRepository.queryBookContent(bookid, chapterid)

                //如果本地没有数据或者没有下一页.进行网络获取.避免脏数据
                if (contentKSEntity == null || contentKSEntity.nid == -1) {
                    //从服务器获取章节内容
                    return@flatMap mDataRepository.getChapterContent(bookid, chapterid).filter { it.status == 1 }
                        .map {
                            it.data.niceBookContentKSEntity().apply {
                                this.lastOpenTime = Date(946713600)
                            }
                        }
                        .map {
                            //服务器获取的数据保存到数据库
                            mDataRepository.insertBookContent(it)
                            return@map it
                        }
                } else {
                    //从本地获取第一章节内容
                    return@flatMap Flowable.just(contentKSEntity)
                }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }


    /**
     * 最近打开的阅读章节内容
     */
    fun getLastOpenContent(): Flowable<BookContentKSEntity> {


        return Flowable.just("")
            .flatMap {
                val lastOpenChapter = mDataRepository.queryLastOpenChapter(bookid)
                if (lastOpenChapter == null) {
                    val firstChapterid = mDataRepository.queryFirstChapterid(bookid)
                    val contentKSEntity = mDataRepository.queryBookContent(bookid, firstChapterid)
                    if (contentKSEntity == null) {
                        //从服务器获取章节内容
                        return@flatMap mDataRepository.getChapterContent(bookid, firstChapterid)
                            .filter { it.status == 1 }
                            .map {
                                it.data.niceBookContentKSEntity().apply {
                                    this.lastOpenTime = Date(946713600)
                                }
                            }.map {
                                //第一次从服务器获取的数据放入数据库保存
                                mDataRepository.insertBookContent(it)
                                return@map it
                            }
                    } else {
                        //从本地获取第一章节内容
                        return@flatMap Flowable.just(contentKSEntity)
                    }
                } else {
                    if (lastOpenChapter.nid == -1) {
                        //每次最后一次都重新加载
                        return@flatMap getContentFromChapterid(lastOpenChapter.chapterId)
                    } else {
                        //从本地获取最近一次打开的章节内容
                        return@flatMap Flowable.just(lastOpenChapter)
                    }

                }


            }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }


    /**
     * 更新最后打开内容时间和显示的角标
     */
    @SuppressLint("SimpleDateFormat")
    fun updateLastOpenTimeAndPosition(chapterid: Int, lastContentPosition: Int) {
        thread {
            mDataRepository.updateLastOpenContent(chapterid, lastContentPosition)
        }

    }

    /**
     * 上一页/下一页.进行预加载灵气.不刷新
     */
    @SuppressLint("CheckResult")
    fun preloadedChapterContent(chapterid: Int) {

        //如果没有下一页.不进行预加载
        if (chapterid == BookKSConstant.NOT_CHAPTER) {
            return
        }
        getContentFromChapterid(chapterid)
            .compose(mActivity?.bindUntilEvent(ActivityEvent.PAUSE))
            .subscribe({
            }, {})
    }

    /**
     * 当前列表中是否包括指定章节
     * 用来过滤重复加载
     */
    fun isLoadAdapter(chapterid: Int): Boolean {
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
        return adapter.getList().size - 1
    }

    /**
     * 章节根据内容分成小页
     */
    fun getTxtPageList(pagerView: TextPagerView, obj: BookContentKSEntity): List<BookContentKSEntity> {
        val margin = mActivity?.niceDp2px(20f) ?: return emptyList()
        pagerView.textSize = BookConstant.TEXT_PAGE_SIZE
        return pagerView
            .setTitle(obj.chapterName)
            .setMargin(margin, margin, 0, 0)
            .setContent(obj.content)
            .build().map {
                BookContentKSEntity().apply {
                    chapterDirName = obj.chapterDirName
                    chapterName = obj.chapterName
                    nid = obj.nid
                    pid = obj.pid
                    bookId = obj.bookId
                    content = obj.content
                    chapterId = obj.chapterId
                    txtPage = it
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
            .subscribeOn(Schedulers.io())
            .compose(mActivity?.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                isCollection = it
            }, {})


    }

    /**
     * 增加收藏
     */
    @SuppressLint("CheckResult")
    fun insertCollection(): Flowable<Boolean> {
        return Flowable.just(bookid)
            .map {
                mDataRepository.insertCollection(it)
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
    fun updateReadHistory() {
        Flowable.just("")
            .map { mDataRepository.queryBookInfo(bookid) }
            .flatMap {
                mDataRepository.updateReadHistory(
                    bookName = it.bookName,
                    bookid = it.bookid.toString(),
                    chapterid = chapterid.toString(),
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
            .subscribe({}, {})
    }
}