package com.yuhang.novel.pirate.ui.main.viewmodel

import android.annotation.SuppressLint
import android.text.TextUtils
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceBookChapterKSEntity
import com.yuhang.novel.pirate.extension.niceBookInfoKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookCollectionKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ChapterListResult
import com.yuhang.novel.pirate.ui.main.adapter.MainAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.concurrent.thread

class MainViewModel : BaseViewModel() {

    val adapter: MainAdapter by lazy { MainAdapter() }

    /**
     * 获取本地所有书本详情
     */
    fun getBookInfoListLocal(): Flowable<List<BookInfoKSEntity?>> {
        return Flowable.just("")
                .flatMap { getCollectionId() }
                .map {
                    mDataRepository.queryCollectionAll(it.toTypedArray())
                }
                .map { list ->
                    return@map list.map {
                        if (it == null) return@map it

                        it.isShowLabel = isShowNewLabel(it.bookid)
                        return@map it
                    }.toList()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 查询所有收藏的bookid
     * 如果登陆数据从服务器获取,未登陆数据从本地获取
     */
    private fun getCollectionId(): Flowable<List<Long>> {
        return Flowable.just("")
                .flatMap {
                    val user = mDataRepository.getLastUser()
                    if (user != null) {
                        return@flatMap mDataRepository.getCollectionList(1)
                                .map { it.data.list.map { it.bookid.toLong() }.toList() }
                    } else {
                        return@flatMap queryCollectionAll().map {
                            it.map {
                                val bookid = it?.bookid!!
                                //从服务器获取收藏列表并插入本地
                                mDataRepository.insertCollection(bookid)
                                bookid
                            }.toList()
                        }
                    }
                }
    }


    /**
     * 获取所有书本详情
     */
    @SuppressLint("CheckResult")
    fun getBookDetailsList(): Flowable<BookInfoKSEntity?> {
        return getCollectionId()
                .flatMap { Flowable.fromArray(* it.toTypedArray()) }
                .flatMap { mDataRepository.getBookDetails(it) }
                .filter { it.status == 1 }
                .map { it.data.niceBookInfoKSEntity() }
                .flatMap {
                    val bookInfo = queryBookInfo(it.bookid)
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
    private fun queryBookInfo(bookid: Long): BookInfoKSEntity? {
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
    private fun getChapterList(bookid: Long): Flowable<ChapterListResult> {
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
    private fun deleteChapterList(bookid: Long) {
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
    fun updateStickTime(bookid: Long) {
        thread { mDataRepository.updateBookInfoStickTime(bookid) }
    }

    /**
     * 删除收藏书箱
     */
    @SuppressLint("CheckResult")
    fun deleteCollection(bookid: Long) {
        //删除线上收藏
        if (!TextUtils.isEmpty(PirateApp.getInstance().getToken())) {
            mDataRepository.deleteNetCollect(bookid, "KS")
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .compose(mFragment?.bindToLifecycle())
                    .subscribe({}, {})
        }


        //删除本地收藏
        thread { mDataRepository.deleteCollection(bookid) }
    }

    /**
     * 是否显示最新更新标签
     * true:更新
     * false:不更新
     */
    private fun isShowNewLabel(bookid: Long): Boolean {
        return mDataRepository.isShowUpdateLable(bookid = bookid)

    }
}