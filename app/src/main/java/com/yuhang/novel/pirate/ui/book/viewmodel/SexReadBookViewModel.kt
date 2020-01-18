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


class SexReadBookViewModel : ReadBookViewModel() {

}