package com.yuhang.novel.pirate.ui.book.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivityBookDetailsBinding
import com.yuhang.novel.pirate.databinding.LayoutBookDetailsAuthorAllBookItemBinding
import com.yuhang.novel.pirate.databinding.LayoutBookDetailsAuthorAllBookLineBinding
import com.yuhang.novel.pirate.eventbus.RemoveCollectionEvent
import com.yuhang.novel.pirate.eventbus.UpdateChapterEvent
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.book.viewmodel.BookDetailsViewModel
import com.yuhang.novel.pirate.widget.BookDetailsHeaderView
import com.yuhang.novel.pirate.widget.glidepalette.BitmapPalette
import com.yuhang.novel.pirate.widget.glidepalette.GlidePalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import kotlin.concurrent.thread


/**
 * 小说详情页
 * 简介/目录/加入书架/立即阅读
 */
class BookDetailsActivity :
    BaseSwipeBackActivity<ActivityBookDetailsBinding, BookDetailsViewModel>(),
    AppBarLayout.OnOffsetChangedListener {


    /**
     * 立即阅读
     */
    private var clickOpenRead = false

    /**
     * 全本缓存
     */
    private var clickDownloadBook = false

    companion object {
        const val BOOK_RESULT = "book_result"

        fun start(context: Activity, result: BooksResult) {
            val intent = Intent(context, BookDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(BOOK_RESULT, Gson().toJson(result))
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_book_details
    }


    private fun getBookResult() =
        Gson().fromJson<BooksResult>(intent.getStringExtra(BOOK_RESULT), BooksResult::class.java)

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        netServiceData()
        onClick()

        //每次打开详情页加载一下章节目录 .防止进入阅读时本地获取不到章节
        lifecycleScope.launch {
            flow<Unit> {
                mViewModel.updateChapterToDB(getBookResult())
                if (clickOpenRead) {
                    clickOpenRead = false
                    mBinding.openReadBookTv.callOnClick()
                }
                if (clickDownloadBook) {
                    clickDownloadBook = false
                    mBinding.downloadTv.callOnClick()
                }
                //点击过立即阅读,加载完成就直接打开阅读界面
                if (hasProgressbar()) {
                    closeProgressbar()
                }
            }
                .catch { Logger.e(it.message?:"") }
                .collect {}
        }

    }

    override fun initStatusTool() {
        ImmersionBar.with(this)
            .statusBarView(mBinding.statusBarV)
            .transparentStatusBar()
            .navigationBarColor(R.color.md_white_1000)
            .flymeOSStatusBarFontColor(R.color.primary_text)
            .statusBarDarkFont(true)
            .autoDarkModeEnable(true)
            .init()
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
//            val layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            if (SystemUtil.getDeviceBrand() == "Meizu") {
//                layoutParams.height = StatusBarUtil.getNavigationBarSize(this).y
//            } else {
//                layoutParams.height =
//                    StatusBarUtil.getNavigationBarSize(this).y - StatusBarUtil.getStatusBarHeight(
//                        this
//                    )
//            }

//            mBinding.tabNavigationBar.layoutParams = layoutParams

        }
    }

    override fun onPause() {
        super.onPause()
        mViewModel.onPause(this)

    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            initCollectionStatus()
        }

        mViewModel.onResume(this)

    }

    /**
     * 书箱是否在书架中
     */
    private suspend fun initCollectionStatus() {
        val bookid = mViewModel.entity?.bookid ?: return
        val collection = mViewModel.queryCollection(bookid)
        if (collection != null) {
            mViewModel.isCollection = true
            mBinding.addBookrackTv.text = "移出书架"
        } else {
            mViewModel.isCollection = false
            mBinding.addBookrackTv.text = "加入书架"
        }
    }

    /**
     * 点击事件
     */
    private fun onClick() {
        mBinding.appBar.addOnOffsetChangedListener(this)
        mBinding.includeToolbarClose.backCloseIv.clickWithTrigger { onBackPressed() }
        mBinding.includeToolbarOpen.backOpenIv.clickWithTrigger { onBackPressed() }
        mBinding.openReadBookTv.clickWithTrigger {

            clickOpenRead = true
            //阅读界面需要等章节列表全部加载完成
            if (mViewModel.chapterList.isEmpty()) {
                showProgressbar("努力获取章节列表...", true)
                return@clickWithTrigger
            }
            mViewModel.onUMEvent(this, UMConstant.TYPE_DETAILS_CLICK_READ, "立即阅读")
            ReadBookActivity.start(this, getBookResult(), false)
        }

        mBinding.addBookrackTv.clickWithTrigger {

            //            showDialogCollection(mViewModel.isCollection)
            if (mViewModel.isCollection) {
                mViewModel.onUMEvent(this, UMConstant.TYPE_DETAILS_CLICK_REMOVE_BOOKCASE, "移出书架")
                removeCollection()
            } else {

                //是否会员
                lifecycleScope.launch {
                    if (mViewModel.queryCollectionAll().size > 20 && !mViewModel.isVip()) {
                        niceToast("超过20本小说请开通会员哦~")
                        return@launch
                    }
                }
                mViewModel.onUMEvent(this, UMConstant.TYPE_DETAILS_CLICK_REMOVE_BOOKCASE, "加入书架")
                addCollection()


            }
        }

        //全本缓存
        mBinding.downloadTv.clickWithTrigger {
            lifecycleScope.launch {
                clickDownloadBook = true
                //阅读界面需要等章节列表全部加载完成
                if (mViewModel.chapterList.isEmpty()) {
                    showProgressbar("努力获取章节列表...", true)
                    return@launch
                }
                //是否会员
                if (mViewModel.queryDownloadAll().size > 20 && !mViewModel.isVip()) {
                    niceToast("超过20本小说请开通会员哦~")
                    return@launch
                }
                mViewModel.downloadBook(getBookResult())
                niceToast("已加入缓存队列")
            }

        }
    }

    /**
     * 重新加载数据
     */
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun resetView(obj: BookInfoKSEntity) {
        Glide.with(this).load(obj.cover.niceCoverPic())
            .listener(
                GlidePalette.with(obj.cover.niceCoverPic())
                    .use(BitmapPalette.Profile.MUTED_DARK)
                    .crossfade(true)
                    .intoBackground(mBinding.includeToobarHeadOpen.bgCoverIv)
            )
            .into(mBinding.includeToobarHeadOpen.coverIv)

        val details = mBinding.layoutBookDetails
        details.statusTv.setText("状态   ${obj.bookStatus}", null)
        details.authorTv.text = obj.author
        details.titleTv.text = obj.bookName
        details.chapterNameTv.text = obj.lastChapterName
        details.bookTypeTv.text = obj.classifyName
        details.descTv.text = obj.description
        details.authorAllBookTv.text = "${obj.author} 的全部作品"
        details.timeTv.text = ""

        mBinding.includeToolbarClose.titleCloseTv.text = obj.bookName

        //目录点击
        mBinding.layoutBookDetails.chapterListLl.setOnClickListener {
            val bookName = mViewModel.entity?.bookName ?: ""
            mViewModel.onUMEvent(
                this,
                UMConstant.TYPE_DETAILS_CLICK_DIR_CHANPTER,
                hashMapOf(
                    "action" to "书箱详情 -> 点击单独章节目录",
                    "bookName" to bookName,
                    "bookId" to getBookResult().getBookid()
                )
            )
            ChapterListActivity.start(
                this,
                getBookResult()

            )
        }

        //动态加载作者全部作品
        details.authorAllBookLl.removeAllViews()
    }


    /**
     * 头部滑动事件
     */
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout ?: return
        BookDetailsHeaderView(this, mViewModel).initHederView(appBarLayout, verticalOffset)
    }

    /**
     * 获取详情数据
     */
    @SuppressLint("CheckResult")
    private fun netServiceData() {
        lifecycleScope.launch {
            flow {
                emit(mViewModel.getBookDetails(getBookResult()))
            }
                .catch {
                    Logger.e(it.message ?: "")
                }
                .onStart { mBinding.loading.showLoading() }
                .onCompletion {
                    if (it?.cause != null) {
                        mBinding.loading.showError()
                    } else {
                        mBinding.loading.showContent()
                    }
                }
                .collect {
                    mViewModel.entity = it
                    //点击立即阅读,返回就刷新一次
                    initCollectionStatus()
                    //插入或更新书箱信息
                    mViewModel.insertBookInfoEntity()

                    withContext(Dispatchers.Main){
                        resetView(it)
                        netAuthorBooksData()
                    }

                }
        }
    }


    /**
     * 作者所有作品
     */
    @SuppressLint("CheckResult")
    private suspend fun netAuthorBooksData() {
        val list = mViewModel.getAuthorBooksList(getBookResult())
        resetAuthorBooksView(list)
    }


    /**
     * 作者全部作品
     */
    private fun resetAuthorBooksView(obj: List<BooksResult>) {
        val details = mBinding.layoutBookDetails
        obj.forEachIndexed { index, recommendBooksResult ->
            recommendBooksResult.let { book ->

                //作品Item
                val itemBinding = LayoutBookDetailsAuthorAllBookItemBinding.inflate(layoutInflater)
                itemBinding.titleTv.text = book.bookName
                itemBinding.chapterNameTv.text = book.lastChapterName
                itemBinding.chapterNameTv.visibility = View.GONE

                val drawable = getDrawable(R.drawable.ic_default_cover)
                val placeholder =
                    RequestOptions().transforms(CenterCrop(), RoundedCorners(niceDp2px(3f)))
                        .placeholder(drawable)
                        .error(drawable)
                Glide.with(this).load(book.cover.niceCoverPic()).apply(placeholder)
                    .into(itemBinding.coverIv)

                //分隔线
                val lineBinding = LayoutBookDetailsAuthorAllBookLineBinding.inflate(layoutInflater)

                //点击跳转作者相关详情
                itemBinding.itemLl.clickWithTrigger {
                    mViewModel.onUMEvent(
                        this,
                        UMConstant.TYPE_DETAILS_CLICK_AUTHOR_OTHER_BOOK,
                        hashMapOf(
                            "action" to "书箱详情 -> 点击作者其他作品",
                            "bookId" to book.bookKsId.toString(),
                            "bookName" to book.bookName!!,
                            "author" to book.author!!
                        )
                    )
                    BookDetailsActivity.start(this, recommendBooksResult)
                }

                details.authorAllBookLl.addView(itemBinding.root)
                details.authorAllBookLl.addView(lineBinding.root)

            }
        }

//        details.authorAllBookLl.removeViewAt(details.authorAllBookLl.childCount - 1)
    }


    /**
     * 增加收藏
     */
    @SuppressLint("CheckResult")
    private fun addCollection() {
        mViewModel.entity?.bookid ?: return

        lifecycleScope.launch {
            flow<Unit> {
                mViewModel.postCollection(getBookResult())
                mViewModel.insertCollection(getBookResult())
                mViewModel.isCollection = true
                mBinding.addBookrackTv.text = "移出书架"
                EventBus.getDefault().post(UpdateChapterEvent())
                niceToast("加入成功")
            }
                .catch { niceToast("加入失败") }
                .collect {  }

        }

    }

    /**
     * 移除收藏
     */
    @SuppressLint("CheckResult")
    private fun removeCollection() {

        val bookid = mViewModel.entity?.bookid ?: return

        lifecycleScope.launch {
            flow<Unit> {
                mViewModel.deleteCollection(bookid)
                mViewModel.isCollection = false
                mBinding.addBookrackTv.text = "加入书架"
                EventBus.getDefault().post(RemoveCollectionEvent())
                niceToast("移除成功")
            }
                .catch { niceToast("加入失败") }
                .collect {
                }
        }




    }


}