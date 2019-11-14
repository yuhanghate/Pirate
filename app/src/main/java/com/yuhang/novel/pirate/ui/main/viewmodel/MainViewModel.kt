package com.yuhang.novel.pirate.ui.main.viewmodel

import android.annotation.SuppressLint
import android.text.TextUtils
import com.orhanobut.logger.Logger
import com.vondear.rxtool.RxDeviceTool
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceBookChapterKSEntity
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.database.entity.PushMessageEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ChapterListResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.VersionResult
import com.yuhang.novel.pirate.ui.main.adapter.MainAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.concurrent.thread

class MainViewModel : BaseViewModel() {

    val adapter: MainAdapter by lazy { MainAdapter() }

    /**
     * 获取本地所有书本详情
     */
    fun getBookInfoListLocal(): Flowable<List<BookInfoKSEntity?>> {
        return Flowable.just("")
            .flatMap { queryCollectionAll() }
            .map { list ->
                return@map list.filterNotNull().map {
                    return@map it.apply { this.isShowLabel = isShowNewLabel(it.bookid) }
                }.toList()
            }.compose(io_main())
    }


    /**
     * 获取所有书本详情
     */
    @SuppressLint("CheckResult")
    fun getBookDetailsList(): Flowable<BookInfoKSEntity?> {
        return queryCollectionAll()
            .flatMap { Flowable.fromArray(* it.toTypedArray()) }
            .flatMap {
                mConvertRepository.updateBook(it, it.resouce)
            }
            .flatMap {
                val bookInfo = queryBookInfo(it.bookid)

                if (bookInfo == null) {
                    //书籍信息插入本地
                    insertBookInfo(it)
                    return@flatMap Flowable.just(queryBookInfo(it.bookid))
                } else {
                    //更新本地数据
                    //有置顶的时候,才更新数据.最新数据有更新也会刷新
                    if (bookInfo.stickTime > 0 || bookInfo.lastChapterName != it.lastChapterName) {
                        it.id = bookInfo.id
                        it.stickTime = bookInfo.stickTime
                        it.resouce = bookInfo.resouce
                        updateBookInfo(it)
                    }
                    return@flatMap Flowable.just(it)
                }
            }.compose(io_main())
    }

    /**
     * 从本地查询书籍信息
     */
    private fun queryBookInfo(bookid: String): BookInfoKSEntity? {
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
    private fun getChapterList(bookid: String): Flowable<ChapterListResult> {
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
    private fun deleteChapterList(bookid: String) {
        mDataRepository.deleteChapterList(bookid)
    }

    /**
     * 从服务器更新书籍章节到本地
     */
    fun updateChapterToDB(): Flowable<List<BookChapterKSEntity>> {
        Logger.i("updateChapterToDB ====> ")
        return queryCollectionAll()
            .flatMap { Flowable.fromArray(*it.toTypedArray()) }
            .flatMap {
                getChapterList(it.bookid)
            }
            .filter { it.status == 1 }
            .map {
                val list = it.data.niceBookChapterKSEntity()
                deleteChapterList(it.data.id)

                insertChapterList(list)
                return@map list
            }
    }

    /**
     * 查询所有收藏
     */
    private fun queryCollectionAll(): Flowable<List<BookInfoKSEntity?>> {
        return Flowable.just("")
            .map { mDataRepository.queryBookInfoCollectionAll() }
            .subscribeOn(Schedulers.io())
    }

    /**
     * 查找收藏
     */
    fun queryCollection(bookid: String): Flowable<BooksResult?> {
        return Flowable.just(bookid)
            .map { mDataRepository.queryCollection(it) }
            .map {
                val info = mDataRepository.queryBookInfo(it.bookid)
                it.niceBooksResult().apply {
                    this.author = info?.author!!
                    this.bookName = info.bookName
                    this.cover = info.cover
                }
            }
            .compose(io_main())
    }

    /**
     * 更新置顶时间戳
     */
    fun updateStickTime(bookid: String) {
        thread { mDataRepository.updateBookInfoStickTime(bookid) }
    }

    /**
     * 取消置顶
     */
    fun updateBookInfoClearStickTime(bookid: String) {
        thread { mDataRepository.updateBookInfoClearStickTime(bookid) }
    }

    /**
     * 删除收藏书箱
     */
    @SuppressLint("CheckResult")
    fun deleteCollection(bookid: String) {
        thread {
            //删除线上收藏
            if (!TextUtils.isEmpty(PirateApp.getInstance().getToken())) {

                mDataRepository.deleteNetCollect(
                    bookid,
                    mDataRepository.queryCollection(bookid)?.resouce?:""
                )
                    .compose(io_main())
                    .compose(mFragment?.bindToLifecycle())
                    .subscribe({}, {})
            }
            //删除本地收藏
            mDataRepository.deleteCollection(bookid)
        }
    }

    /**
     * 是否显示最新更新标签
     * true:更新
     * false:不更新
     */
    private fun isShowNewLabel(bookid: String): Boolean {
        return mDataRepository.isShowUpdateLable(bookid = bookid)

    }


    /**
     * 检测版本
     */
    fun checkVersion(): Flowable<VersionResult> {
        return mDataRepository.checkVersion(RxDeviceTool.getAppVersionName(mActivity))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getMessage(result: VersionResult): String {
        return StringBuilder()
            .append("\n")
            .append("建议在WLAN环境下进行升级")
            .append("\n\n")
            .append("版本: ${result.newVersion}")
            .append("\n\n")
            .append("大小: ${result.targetSize}")
            .append("\n\n")
            .append("更新说明:")
            .append("\n")
            .append(result.updateLog)
            .append("\n")
            .toString()
    }

    fun getPushMessageEntity(): Flowable<PushMessageEntity?> {
        return Flowable.just("")
            .map { mDataRepository.queryNoteEntity() }
            .compose(io_main())
    }

    /**
     * 删除公告信息
     */
    fun deletePushMessage(obj: PushMessageEntity) {
        thread { mDataRepository.delete(obj) }
    }


}