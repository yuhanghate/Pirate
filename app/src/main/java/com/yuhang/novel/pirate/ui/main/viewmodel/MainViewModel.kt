package com.yuhang.novel.pirate.ui.main.viewmodel

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.tamsiree.rxkit.RxDeviceTool
import com.tamsiree.rxkit.RxNetTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.extension.niceCategoryKDEntity
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.database.entity.ConfigEntity
import com.yuhang.novel.pirate.repository.database.entity.PushMessageEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.AppConfigResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.VersionResult
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.main.adapter.MainAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.concurrent.thread

class MainViewModel : BaseViewModel() {

    /**
     * 更新检测时间3天
     */
    val MAX_TIME_VERSION = 1000 * 60 * 60 * 24 * 3

    /**
     * 更新检测时间8天
     */
    val MAX_TIME_LOGIN = 1000 * 60 * 60 * 24 * 8

    val adapter: MainAdapter by lazy { MainAdapter() }

    /**
     * 主页标签
     */
    val tabIcons by lazy { listOf(R.drawable.ic_tab_main_normal, R.drawable.ic_tab_store_normal, R.drawable.ic_tab_me_normal) }

    /**
     * 主页名称
     */
    val tabNames by lazy { listOf("主页", "书城", "我的") }

    /**
     * 当前Fragment角标
     */
    var currentIndex = 0

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
     * 收藏列表 快读源
     */
    fun getBookDetailsListKD(): Flowable<BookInfoKSEntity> {
        return Flowable.just("")
            .flatMap {
                val list = mDataRepository.queryCollectionKD()

                if (!RxNetTool.isAvailable(PirateApp.getInstance()) || list.isEmpty()) {
                    return@flatMap Flowable.fromIterable(list)
                }
                val sb = StringBuilder()
                list.forEach { sb.append(it.bookid).append(",") }
                sb.deleteCharAt(sb.length - 1)
                return@flatMap mConvertRepository.updateBookKD(sb.toString())
            }.flatMap {return@flatMap Flowable.just(updateBookInfo(it))}
    }

    /**
     * 收藏列表 看书源
     */
    fun getBookDetailsListKS(): Flowable<BookInfoKSEntity> {
        return Flowable.just("")
            .flatMap {
                val list = mDataRepository.queryCollectionKS()
                return@flatMap Flowable.fromIterable(list)
            }
            .flatMap {
                if (it.bookStatus == "完结") {
                    //不刷新完结小说
                    return@flatMap Flowable.just(it)
                }
                if (RxNetTool.isAvailable(PirateApp.getInstance())) {
                    return@flatMap mConvertRepository.updateBookKS(it.bookid)
                }
                return@flatMap Flowable.just(it)
            }.flatMap {return@flatMap Flowable.just(updateBookInfo(it))}
    }

    /**
     * 更新书籍信息
     */
    private fun updateBookInfo(obj: BookInfoKSEntity): BookInfoKSEntity {
        val bookInfo = queryBookInfo(obj.bookid)
        return if (bookInfo == null) {
            //书籍信息插入本地
            insertBookInfo(obj)
            queryBookInfo(obj.bookid)!!
        } else {
            //更新本地数据
            //有置顶的时候,才更新数据.最新数据有更新也会刷新
            if (bookInfo.stickTime > 0 || bookInfo.lastChapterName != obj.lastChapterName) {
                bookInfo.lastChapterName = obj.lastChapterName
                mDataRepository.updateBookInfo(bookInfo)
            }
            obj
        }
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
     * 获取章节列表
     */
    private fun getChapterList(obj: BooksResult): Flowable<List<BookChapterKSEntity>> {
        return mConvertRepository.getChapterList(obj)
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
        return Flowable.just("")
            .map { mDataRepository.queryCollectionAllSerial() }
            .flatMap { Flowable.fromIterable(it) }
            .flatMap {
                getChapterList(it.niceBooksResult())
            }
            .map {
                deleteChapterList(it[0].bookId)
                insertChapterList(it)
                return@map it
            }.compose(io_main())
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
                    mDataRepository.queryCollection(bookid)?.resouce ?: ""
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


    /**
     * 是否有安装应用权限
     * true:有
     * false:没有
     */
    fun installProcess(): Boolean {
        var haveInstallPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            haveInstallPermission = mActivity?.packageManager?.canRequestPackageInstalls()!!
            if (!haveInstallPermission) {//没有权限
                startInstallPermissionSettingActivity()
                return haveInstallPermission
            }
            return haveInstallPermission
        }
        return haveInstallPermission
    }

    /**
     * 打开允许安装界面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        val intent = Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:" + mActivity?.packageName)
        )
        mActivity?.startActivityForResult(intent, 10086)
    }

    /**
     * 是否检测版本
     */
    fun isShowVersionDialog(): Boolean {
        val millis = System.currentTimeMillis()
        val lastTime = PreferenceUtil.getLong("version_update", millis)
        val b = Math.abs(lastTime - millis) > MAX_TIME_VERSION
        //第一次或者超过最大时间弹出版本检测
        if (lastTime == millis || b) {
            PreferenceUtil.commitLong("version_update", millis)
            return true
        }
        return false
    }

    /**
     * 是否显示登陆
     */
    fun isShowLoginDialog(): Boolean {

        val millis = System.currentTimeMillis()
        val lastTime = PreferenceUtil.getLong("show_login", millis)
        val b = lastTime - millis > MAX_TIME_LOGIN
        //第一次或者超过最大时间弹出版本检测
        if (lastTime == millis || b) {
            PreferenceUtil.commitLong("show_login", millis)
            return true
        }
        return false
    }

    /**
     * 快读分类预加载
     */
    fun preloadCategory(): Flowable<Unit> {
        return mDataRepository.getCategoryList()
            .map { it.map { it.niceCategoryKDEntity() }.toList() }
            .map { mDataRepository.insertCategoryList(it) }
            .compose(io_main())
    }

    /**
     * 加载配置
     */
    fun preloadConfig(): Flowable<AppConfigResult> {
        return mDataRepository.getAppConfig()
            .map {
                mDataRepository.insertConfig(ConfigEntity().apply {
                    this.showGameRecommended = it.data.isShowGameRecommended
                    this.showSexBook = it.data.isShowSexBook
                    this.isOpenVip = it.data.isOpenVip
                })
                it
            }
            .compose(io_main())
    }
}