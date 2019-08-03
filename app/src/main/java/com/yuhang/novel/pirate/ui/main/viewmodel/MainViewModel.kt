package com.yuhang.novel.pirate.ui.main.viewmodel

import android.annotation.SuppressLint
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.constant.BookKSConstant
import com.yuhang.novel.pirate.extension.niceBookChapterKSEntity
import com.yuhang.novel.pirate.extension.niceBookInfoKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookCollectionKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookReadHistoryEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookDetailsResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ChapterListResult
import com.yuhang.novel.pirate.ui.main.adapter.MainAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MainViewModel : BaseViewModel() {

    val adapter: MainAdapter  by lazy { MainAdapter() }

    /**
     * 获取本地所有书本详情
     */
    fun getBookInfoListLocal(): Flowable<List<BookInfoKSEntity?>> {
        return Flowable.just("")
            .map { mDataRepository.queryBookInfoAll() }
            .map {list ->
                return@map list.map {
                    if (it == null) return@map it
                    it.isShowLabel = isShowNewLabel()
                    return@map it
                }.toList()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取所有书本详情
     */
    @SuppressLint("CheckResult")
    fun getBookDetailsList(): Flowable<BookInfoKSEntity?> {
        return queryCollectionAll()
            .flatMap { Flowable.fromArray(* it.toTypedArray()) }
            .flatMap { mDataRepository.getBookDetails(it.bookid) }
            .filter { it.status == 1 }
            .map { it.data.niceBookInfoKSEntity() }
            .flatMap {
                val bookInfo = queryBookInfo(it.bookid)
//                val bookContent = mDataRepository.queryBookContent(it.bookid, it.lastChapterId)
//
//                //如果没有阅读过,就不显示标签
//                it.isShowLabel = bookContent != null

                if (bookInfo == null) {
                    //书籍信息插入本地
                    insertBookInfo(it)
                    return@flatMap Flowable.just(queryBookInfo(it.bookid))
                } else {
                    //更新本地数据
                    it.id = bookInfo.id
                    it.stickTime = bookInfo.stickTime
                    updateBookInfo(it)
                    return@flatMap Flowable.just(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 从本地查询书籍信息
     */
    private fun queryBookInfo(bookid: Int): BookInfoKSEntity? {
        return mDataRepository.queryBookInfo(bookid = bookid)
    }

    /**
     * 插入本地书籍信息
     */
    private fun insertBookInfo(obj: BookInfoKSEntity) {
        mDataRepository.insertBookInfo(obj)
    }

    /**
     * 更新本地书籍信息
     */
    private fun updateBookInfo(obj: BookInfoKSEntity) {
        mDataRepository.updateBookInfo(obj)
    }

    /**
     * 获取章节列表
     */
    private fun getChapterList(bookid: Int): Flowable<ChapterListResult> {
        return mDataRepository.getBookChapterList(bookid)
    }


    /**
     * 插入章节列表到本地
     */
    private fun insertChapterList(list: List<BookChapterKSEntity>) {
        mDataRepository.insertChapterList(list)
    }

    /**
     * 删除本地对应的书籍章节
     */
    private fun deleteChapterList(bookid: Int) {
        mDataRepository.deleteChapterList(bookid)
    }

    /**
     * 从服务器更新书籍章节到本地
     */
    fun updateChapterToDB(): Disposable {
        Logger.i("updateChapterToDB ====> ")
        return queryCollectionAll()
            .flatMap { Flowable.fromArray(*it.toTypedArray()) }
            .flatMap {

                Logger.i("updateChapterToDB = $it")
                getChapterList(it.bookid)
            }
            .filter { it.status == 1 }
            .map {
                deleteChapterList(it.data.id)
                val list = it.data.niceBookChapterKSEntity()
                insertChapterList(list)
                return@map list
            }
            .compose(mActivity?.bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Logger.i("")
            }, {
                it.message?.let { Logger.e(it) }
            })
    }

    /**
     * 查询所有收藏
     */
    private fun queryCollectionAll(): Flowable<List<BookCollectionKSEntity?>> {
        return Flowable.just("")
            .map { mDataRepository.queryCollectionAll() }
            .subscribeOn(Schedulers.io())
    }

    /**
     * 更新置顶时间戳
     */
    fun updateStickTime(bookid: Int) {
        thread { mDataRepository.updateBookInfoStickTime(bookid) }
    }

    /**
     * 删除收藏书箱
     */
    fun deleteCollection(bookid: Int) {
        thread { mDataRepository.deleteCollection(bookid) }
    }

    /**
     * 是否显示最新更新标签
     * true:更新
     * false:不更新
     */
    private fun isShowNewLabel(): Boolean {
        return mDataRepository.isShowUpdateLable() == 1

    }
}