package com.yuhang.novel.pirate.ui.book.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.gson.Gson
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xw.repo.BubbleSeekBar
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.constant.ConfigConstant
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivityReadBookBinding
import com.yuhang.novel.pirate.eventbus.UpdateReadHistoryEvent
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.listener.OnClickChapterItemListener
import com.yuhang.novel.pirate.listener.OnPageIndexListener
import com.yuhang.novel.pirate.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.book.adapter.ReadBookAdapter
import com.yuhang.novel.pirate.ui.book.dialog.BookCollectionDialog
import com.yuhang.novel.pirate.ui.book.dialog.DownloadChapterDialog
import com.yuhang.novel.pirate.ui.book.fragment.DrawerLayoutLeftFragment
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadBookViewModel
import com.yuhang.novel.pirate.ui.resouce.activity.ResouceListKdActivity
import com.yuhang.novel.pirate.utils.StatusBarUtil
import com.yuhang.novel.pirate.widget.OnScrollListener
import com.yuhang.novel.pirate.widget.ReadBookTextView
import com.yuhang.novel.pirate.widget.WrapContentLinearLayoutManager
import org.greenrobot.eventbus.EventBus
import kotlin.math.abs


/**
 * 书籍阅读
 */
class ReadBookActivity : BaseActivity<ActivityReadBookBinding, ReadBookViewModel>(),
    ReadBookTextView.OnClickCenterListener, ReadBookTextView.OnClickNextListener,
    ReadBookTextView.OnClickPreviousListener, OnClickChapterItemListener, OnRefreshLoadMoreListener,
    OnPageIndexListener {


    private var mTopInAnim: Animation? = null
    private var mTopOutAnim: Animation? = null
    private var mBottomInAnim: Animation? = null
    private var mBottomOutAnim: Animation? = null
    private var mBackgroundInTransparent: Animation? = null
    private var mBackgroundOutTransparent: Animation? = null


    private var toggleMenuSwitch = false


    var fragment: DrawerLayoutLeftFragment? = null

    /**
     * 是否正在加载上一页,防止重复加载
     */
    private var isPrevious = false

    /**
     * 是否正在加载下一页,防止重复加载
     */
    private var isNext = false

    companion object {
        val TAG = ReadBookActivity::class.java.simpleName
        const val CHAPTERID = "chapter_id"
        const val BOOKS_RESULT = "books_result"
        const val IS_INIT_CHAPTER = "is_init_chapter"

        const val DURATION: Long = 190

        /**
         * 根据章节跳转
         */
        fun start(context: Activity, obj: BooksResult, chapterid: String = "") {
            val intent = Intent(context, ReadBookActivity::class.java)
            intent.putExtra(BOOKS_RESULT, obj.toJson())
            intent.putExtra(CHAPTERID, chapterid)
            startIntent(context, intent)
        }

        fun start(context: Activity, obj: BooksResult, initChapter: Boolean = false) {
            val intent = Intent(context, ReadBookActivity::class.java)
            intent.putExtra(BOOKS_RESULT, obj.toJson())
            intent.putExtra(IS_INIT_CHAPTER, initChapter)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_read_book
    }

    override fun initStatusTool() {
        val color = PreferenceUtil.getString("page_color", "#000000")
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarColor(color)
            .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
            .statusBarColor(color)
            .navigationBarColor(color)
            .statusBarColorTransform(color)
            .navigationBarColorTransform(color)
            .init()

    }

    override fun onStatusColor(): Int {
        return BookConstant.getPageBackground()
    }

    /**
     * 是否重新加载章节列表
     */
    private fun getInitChapter() = intent.getBooleanExtra(IS_INIT_CHAPTER, false)

    private fun getChapterid() = intent.getStringExtra(CHAPTERID)


    /**
     * 获取参数
     */
    private fun getBooksResult(): BooksResult {
        return Gson().fromJson<BooksResult>(
            intent.getStringExtra(BOOKS_RESULT),
            BooksResult::class.java
        )
    }


    override fun onPause() {

        super.onPause()
        mViewModel.onPause(this)
        mBinding.root.keepScreenOn = false
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
        isPrevious = false
        isNext = false
    }


    @SuppressLint("CheckResult")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        this.intent = intent
        intent ?: return
        mViewModel.clearData()
        mViewModel.mBooksResult = getBooksResult()
        mBinding.loading.showLoading()
        mViewModel.initChapterList(mViewModel.mBooksResult!!, getInitChapter())
            .compose(bindToLifecycle())
            .subscribe({
                if (!TextUtils.isEmpty(getChapterid())) {
                    //打开指定章节
                    mViewModel.chapterid = getChapterid()
                    initDrawerView()
                    initChapterProgressSeekBar()
                    netDataChapterContentFromId(getChapterid())
                    return@subscribe
                }
                netDataChatpterContent()
            }, {
                mBinding.loading.showError()
            })
        mViewModel.preloadBookContents(mViewModel.mBooksResult!!)

    }

    override fun initView() {
        super.initView()
        initViewModel()
        initContentViewHeight()
        initRefreshLayout()
        initRecyclerView()


        initBackground()
        resetBackground(BookConstant.getPageColorIndex())
        onClick()
    }

    @SuppressLint("CheckResult")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //如果获取焦点,并且RecyclerView是第一次加载
        if (hasFocus && mViewModel.adapter.getList().isEmpty()) {
            //当Activity尺寸计算好以后,进行加载.因为需要动态根据尺寸分页

            mBinding.loading.showLoading()
            mViewModel.initChapterList(mViewModel.mBooksResult!!, getInitChapter())
                .compose(bindToLifecycle())
                .subscribe({
                    initDrawerView()
                    initFontSeekBar()
                    initChapterProgressSeekBar()
                    mViewModel.setCacheChapter(fragment)
                    mViewModel.preloadBookContents(mViewModel.mBooksResult!!)
                    if (!TextUtils.isEmpty(getChapterid())) {
                        //打开指定章节
                        mViewModel.chapterid = getChapterid()
                        netDataChapterContentFromId(getChapterid())
                        return@subscribe
                    }
                    //打开最近章节

                    netDataChatpterContent()


                }, {
                    mBinding.loading.showError()
                })
        }
    }


    @SuppressLint("NoDelegateOnResumeDetector")
    override fun onResume() {
        super.onResume()
        initBattery()
        mBinding.root.keepScreenOn = true
//        keepScreenOnWithPermissionCheck(true)
        mViewModel.onResume(this)
    }

    /**
     * 初始化章节进度条
     */
    @SuppressLint("CheckResult")
    private fun initChapterProgressSeekBar() {


        val size = mViewModel.chapterList.size
        mBinding.layoutButton.chapterProgressSb.configBuilder
            .min(1f)
            .max(size.toFloat())
            .progress(1f)
            .sectionCount(size - 1)
            .trackColor(ContextCompat.getColor(this, R.color.md_grey_500))
            .secondTrackColor(ContextCompat.getColor(this, R.color.icons))
//            .showSectionText()
            .bubbleColor(ContextCompat.getColor(this, R.color.secondary_text))
            .bubbleTextSize(18)
            .build()


        mBinding.layoutButton.chapterProgressSb.onProgressChangedListener =
            object : BubbleSeekBar.OnProgressChangedListenerAdapter() {
                override fun onProgressChanged(
                    bubbleSeekBar: BubbleSeekBar?,
                    progress: Int,
                    progressFloat: Float,
                    fromUser: Boolean
                ) {
                    super.onProgressChanged(
                        bubbleSeekBar,
                        progress,
                        progressFloat,
                        fromUser
                    )
                    if (mViewModel.chapterList.isNotEmpty()) {
                        mBinding.layoutButton.chapterNameTv.text =
                            mViewModel.chapterList[progress - 1].name
                    }
                }

                override fun getProgressOnActionUp(
                    bubbleSeekBar: BubbleSeekBar?,
                    progress: Int,
                    progressFloat: Float
                ) {
                    super.getProgressOnActionUp(bubbleSeekBar, progress, progressFloat)

                    netDataChapterContentFromId(mViewModel.chapterList[progress - 1].chapterId)
                }
            }
    }

    /**
     * 初始化字体进度条
     */
    private fun initFontSeekBar() {
        mBinding.layoutButton.seekBar
            .configBuilder
            .min(1f)
            .max(12f)
            .progress(BookConstant.getFontProgress())
            .sectionCount(11)
            .trackColor(ContextCompat.getColor(this, R.color.md_grey_500))
            .secondTrackColor(ContextCompat.getColor(this, R.color.icons))
            .thumbColor(ContextCompat.getColor(this, R.color.secondary_text))
            .showSectionText()
            .sectionTextColor(ContextCompat.getColor(this, R.color.secondary_text))
            .sectionTextSize(18)
            .showThumbText()
            .touchToSeek()
            .thumbTextColor(ContextCompat.getColor(this, R.color.secondary_text))
            .thumbTextSize(18)
            .bubbleColor(ContextCompat.getColor(this, R.color.secondary_text))
            .bubbleTextSize(22)
            .showSectionMark()
            .seekBySection()
            .autoAdjustSectionMark()
            .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
            .build()

        mBinding.layoutButton.seekBar.onProgressChangedListener =
            object : BubbleSeekBar.OnProgressChangedListenerAdapter() {
                override fun getProgressOnActionUp(
                    bubbleSeekBar: BubbleSeekBar?,
                    progress: Int,
                    progressFloat: Float
                ) {
                    super.getProgressOnActionUp(bubbleSeekBar, progress, progressFloat)
                    when (progress) {
                        1 -> {
                            BookConstant.setPageTextSize(15f)
                            netDataChatpterContent()
                        }
                        2 -> {
                            BookConstant.setPageTextSize(16f)
                            netDataChatpterContent()
                        }
                        3 -> {
                            BookConstant.setPageTextSize(17f)
                            netDataChatpterContent()
                        }
                        4 -> {
                            BookConstant.setPageTextSize(18f)
                            netDataChatpterContent()
                        }
                        5 -> {
                            BookConstant.setPageTextSize(19f)
                            netDataChatpterContent()
                        }
                        6 -> {
                            BookConstant.setPageTextSize(20f)
                            netDataChatpterContent()
                        }
                        7 -> {
                            BookConstant.setPageTextSize(21f)
                            netDataChatpterContent()
                        }
                        8 -> {
                            BookConstant.setPageTextSize(22f)
                            netDataChatpterContent()
                        }
                        9 -> {
                            BookConstant.setPageTextSize(23f)
                            netDataChatpterContent()
                        }
                        10 -> {
                            BookConstant.setPageTextSize(24f)
                            netDataChatpterContent()
                        }
                        11 -> {
                            BookConstant.setPageTextSize(25f)
                            netDataChatpterContent()
                        }
                        12 -> {
                            BookConstant.setPageTextSize(26f)
                            netDataChatpterContent()
                        }
                    }
                }
            }
    }

    /**
     * 初始化电量
     */
    private fun initBattery() {
        //注册广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(mReceiver, intentFilter)
    }

    /**
     * 初始化背景颜色
     */
    private fun initBackground() {
        mBinding.root.setBackgroundColor(BookConstant.TEXT_PAGE_BACKGROUND)
        mBinding.layoutTop.root.setBackgroundColor(BookConstant.TEXT_PAGE_BACKGROUND)
        mBinding.layoutButton.root.setBackgroundColor(BookConstant.TEXT_PAGE_BACKGROUND)
        mBinding.layoutButton.colorLl.visibility = View.GONE
    }

    @SuppressLint("CheckResult")
    private fun initViewModel() {
        mViewModel.mBooksResult = getBooksResult()
//        mViewModel.bookid = mViewModel.mBooksResult?.getBookid()!!
        mViewModel.clearLable()
        mViewModel.initBookNameData(mViewModel.mBooksResult!!)
        mViewModel.isCollectionBook()
    }


    /**
     * 设置侧滑和内容高度
     */
    private fun initContentViewHeight() {
        mBinding.footerV.layoutParams.height = StatusBarUtil.getStatusBarHeight(this)

    }

    //初始化菜单动画
    private fun initMenuAnim() {
        if (mTopInAnim != null) return

        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in)
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out)
        mBackgroundInTransparent = AnimationUtils.loadAnimation(this, R.anim.slide_in_transparent)
        mBackgroundOutTransparent = AnimationUtils.loadAnimation(this, R.anim.slide_out_transparent)
        //退出的速度要快
        mTopOutAnim?.duration = DURATION
        mBottomOutAnim?.duration = DURATION
    }

    @SuppressLint("WrongConstant")
    private fun onClick() {
        mBinding.bgShadow.setOnClickListener { toggleMenu() }
        mBinding.layoutTop.backIv.setOnClickListener {
            toggleMenu()
            super.onBackPressed()
        }
        mBinding.layoutButton.chapterDirTv.setOnClickListener {
            mViewModel.onUMEvent(this, UMConstant.TYPE_READ_CLICK_DIR_CHANPTER, "阅读页 -> 点击阅读目录")
            fragment?.resetBackground()
            toggleMenu()
            mBinding.drawerLayout.openDrawer(Gravity.START)
            fragment?.setCurrentReadItem(mViewModel.chapterid)
        }

        mBinding.loading.setOnClickListener { toggleMenu() }
        mBinding.layoutTop.refreshTv.setOnClickListener {
            mViewModel.onUMEvent(
                this,
                UMConstant.TYPE_READ_CLICK_REFRESH,
                hashMapOf(
                    "action" to "阅读页 -> 点击刷新",
                    "bookid" to mViewModel.mBooksResult?.getBookid()!!,
                    "bookName" to mViewModel.bookName,
                    "chanpterName" to mViewModel.chapterName
                )
            )
            netDataChatpterContent()
        }

        mBinding.layoutButton.pageBg1.setOnClickListener {
            resetBackground(0)
            mViewModel.adapter.notifyDataSetChanged()
        }
        mBinding.layoutButton.pageBg2.setOnClickListener {
            resetBackground(1)
            mViewModel.adapter.notifyDataSetChanged()
        }
        mBinding.layoutButton.pageBg3.setOnClickListener {
            resetBackground(2)
            mViewModel.adapter.notifyDataSetChanged()
        }
        mBinding.layoutButton.pageBg4.setOnClickListener {
            resetBackground(3)
            mViewModel.adapter.notifyDataSetChanged()
        }

        mBinding.layoutButton.contentBackgroundTv.setOnClickListener {
            mBinding.layoutButton.colorLl.visibility = View.VISIBLE
            mBinding.layoutButton.fontLl.visibility = View.GONE
            mBinding.layoutButton.chapterProgressLl.visibility = View.GONE
        }


        //字体大小
        mBinding.layoutButton.fontTv.setOnClickListener {
            mBinding.layoutButton.fontLl.visibility = View.VISIBLE
            mBinding.layoutButton.colorLl.visibility = View.GONE
            mBinding.layoutButton.chapterProgressLl.visibility = View.GONE
        }

        //进度
        mBinding.layoutButton.chapterProgressTv.setOnClickListener {
            mBinding.layoutButton.chapterProgressLl.visibility = View.VISIBLE
            mBinding.layoutButton.colorLl.visibility = View.GONE
            mBinding.layoutButton.fontLl.visibility = View.GONE
            mBinding.layoutButton.chapterProgressSb.setProgress(mViewModel.getChapterIndex().toFloat() + 1)
        }

        mBinding.loading.setRetryListener { netDataChatpterContent() }

        //换源
        mBinding.layoutTop.resouceTv.setOnClickListener {
            toggleMenu()
            ResouceListKdActivity.start(
                this,
                mViewModel.mBooksResult!!,
                mViewModel.getChapterIndex()
            )
        }

        //上一章
        mBinding.layoutButton.btnPreviousChapter.setOnClickListener {
            if (mViewModel.hasPreviousPage(mViewModel.chapterid)) {
                netDataChapterContentFromId(mViewModel.getPreviousPage(mViewModel.chapterid).chapterId)
                return@setOnClickListener
            }

            niceToast("没有上一页了")

        }

        //下一章
        mBinding.layoutButton.btnNextChapter.setOnClickListener {
            if (mViewModel.hasNextPage(mViewModel.chapterid)) {
                netDataChapterContentFromId(mViewModel.getNextPage(mViewModel.chapterid).chapterId)
                return@setOnClickListener
            }
            niceToast("最后一页了")
        }

        //下载
        mBinding.layoutTop.downloadTv.setOnClickListener { downloadBook() }
    }

    /**
     * 下载章节
     */
    private fun downloadBook() {
        toggleMenu()
        DownloadChapterDialog(this, mViewModel, getBooksResult()).show()
    }


    /**
     * 重置背景颜色
     */
    private fun resetBackground(index: Int) {
        BookConstant.setPageBackground(index)
        mBinding.root.setBackgroundColor(BookConstant.getPageBackground())
        mBinding.layoutButton.root.setBackgroundColor(BookConstant.getPageBackground())

        //底部栏字体颜色
        mBinding.layoutButton.chapterDirTv.setBackgroundResource(BookConstant.getReadBookButton())
        mBinding.layoutButton.contentBackgroundTv.setBackgroundResource(BookConstant.getReadBookButton())
        mBinding.layoutButton.fontTv.setBackgroundResource(BookConstant.getReadBookButton())
        mBinding.layoutButton.chapterProgressTv.setBackgroundResource(BookConstant.getReadBookButton())

        //底部栏背景颜色
        mBinding.layoutButton.chapterDirTv.setTextColor(BookConstant.getPageTextColor())
        mBinding.layoutButton.contentBackgroundTv.setTextColor(BookConstant.getPageTextColor())
        mBinding.layoutButton.fontTv.setTextColor(BookConstant.getPageTextColor())
        mBinding.layoutButton.chapterProgressTv.setTextColor(BookConstant.getPageTextColor())

        //navigateion状态栏颜色
        window.navigationBarColor = BookConstant.getPageBackground()

        //顶部栏颜色和字体颜色
        mBinding.layoutTop.root.setBackgroundColor(BookConstant.getPageBackground())
        mBinding.layoutTop.resouceTv.setTextColor(BookConstant.getPageTextColor())
//        mBinding.layoutTop.refreshTv.setTextColor(BookConstant.getPageTextColor())

        //返回按钮颜色
        if (BookConstant.getPageColorIndex() == 3) {
            mBinding.layoutTop.backIv.setImageResource(R.drawable.btn_back_white)
        } else {
            mBinding.layoutTop.backIv.setImageResource(R.drawable.btn_back_black)
        }

        mBinding.footerV.setBackgroundColor(BookConstant.getPageBackground())


    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private fun toggleMenu() {
        initMenuAnim()

        if (mBinding.layoutTop.root.visibility == View.VISIBLE) {
            //关闭
            mBinding.layoutTop.root.startAnimation(mTopOutAnim)
            mBinding.layoutButton.root.startAnimation(mBottomOutAnim)
            mBinding.bgShadow.startAnimation(mBackgroundOutTransparent)

            Handler().postDelayed({
                mBinding.layoutTop.root.visibility = View.INVISIBLE
                mBinding.layoutButton.root.visibility = View.INVISIBLE
                mBinding.bgShadow.visibility = View.INVISIBLE

                //关闭时隐藏背景
                mBinding.layoutButton.colorLl.visibility = View.GONE
                //关闭字体
                mBinding.layoutButton.fontLl.visibility = View.GONE
                //关闭进度
                mBinding.layoutButton.chapterProgressLl.visibility = View.GONE
            }, DURATION)

            toggleMenuSwitch = false
        } else {

            //打开
            mBinding.layoutTop.root.visibility = View.VISIBLE
            mBinding.layoutButton.root.visibility = View.VISIBLE
            mBinding.bgShadow.visibility = View.VISIBLE
            mBinding.bgShadow.startAnimation(mBackgroundInTransparent)
            mBinding.layoutTop.root.startAnimation(mTopInAnim)
            mBinding.layoutButton.root.startAnimation(mBottomInAnim)
            toggleMenuSwitch = true
        }

    }


    /**
     * 初始化DrawerView
     */
    @SuppressLint("WrongConstant")
    private fun initDrawerView() {

        fragment =
            supportFragmentManager.findFragmentById(R.id.fg_left_menu) as? DrawerLayoutLeftFragment

        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.START)
        mBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                mBinding.drawerLayout.setDrawerLockMode(
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.START
                )
            }

            override fun onDrawerOpened(drawerView: View) {
                mBinding.drawerLayout.setDrawerLockMode(
                    DrawerLayout.LOCK_MODE_UNLOCKED,
                    Gravity.START
                )
            }
        })

        fragment?.bookid = mViewModel.mBooksResult?.getBookid()!!
        fragment?.chapterList = mViewModel.chapterList
        fragment?.chapterid = mViewModel.chapterid
        fragment?.mOnClickChapterItemListener = this
        fragment?.setRefreshView()
    }

    /**
     * 初始化刷新控件
     */
    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.refreshLayout.setEnableRefresh(false)
        mBinding.refreshLayout.setEnableLoadMore(false)
    }

    /**
     * 初始化列表
     */
    @SuppressLint("WrongConstant")
    override fun initRecyclerView() {

        mBinding.loading.setLoading(R.layout._loading_layout_loading2)

        if (PreferenceUtil.getInt(ConfigConstant.PAGE_TYPE, ConfigConstant.PAGE_TYPE_HORIZONTAL)
            == ConfigConstant.PAGE_TYPE_HORIZONTAL
        ) {
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(mBinding.recyclerView)
            val linearLayoutManager = WrapContentLinearLayoutManager(this)
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            mBinding.recyclerView.isNestedScrollingEnabled = false
            mViewModel.adapter.setListener(this)
            mBinding.recyclerView.layoutManager = linearLayoutManager
            mViewModel.adapter.initData(arrayListOf())
            mBinding.recyclerView.adapter = mViewModel.adapter
            mBinding.recyclerView.addOnScrollListener(OnScrollListener(mViewModel.adapter, this))
            mBinding.loading.showLoading()
        } else {
            val linearLayoutManager = WrapContentLinearLayoutManager(this)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            mBinding.recyclerView.isNestedScrollingEnabled = false
            mViewModel.adapter.setListener(this)
            mBinding.recyclerView.layoutManager = linearLayoutManager
            mViewModel.adapter.initData(arrayListOf())
            mBinding.recyclerView.adapter = mViewModel.adapter
            mBinding.recyclerView.addOnScrollListener(OnScrollListener(mViewModel.adapter, this))
            mBinding.loading.showLoading()
        }


    }

    /**
     * 获取小说最近阅读章节内容
     */

    @SuppressLint("CheckResult")
    private fun netDataChatpterContent() {
        mBinding.loading.showLoading()
        mViewModel.getLastBookContent(mViewModel.mBooksResult!!)
            .compose(bindToLifecycle())
            .subscribe({ contentResult ->
                mBinding.loading.showContent()

                val list = mViewModel.getTxtPageList(mBinding.textPage, contentResult)
                mViewModel.updateReadHistory(
                    contentResult.chapterId,
                    contentResult.chapterName,
                    mViewModel.mBooksResult!!
                )
                    .compose(bindToLifecycle()).subscribe({}, {})

                mViewModel.adapter.setRefersh(list)
                mBinding.recyclerView.scrollToPosition(contentResult.lastContentPosition)
                onPageIndexListener(contentResult.lastContentPosition)
            }, {

                if (!mBinding.loading.isError) {
                    mBinding.loading.showError()
                }
                Logger.t("空白").i("网络加载异常")
            }, {})
    }

    /**
     * 获取指定章节的内容
     */
    @SuppressLint("CheckResult")
    private fun netDataChapterContentFromId(chapterid: String) {
        mBinding.loading.showLoading()

        mViewModel.getBookContent(mViewModel.mBooksResult!!, mViewModel.getChapterEntity(chapterid))
            .compose(bindToLifecycle())
            .subscribe({

                val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                mViewModel.updateReadHistory(
                    it.chapterId,
                    it.chapterName,
                    mViewModel.mBooksResult!!
                )
                    .compose(bindToLifecycle()).subscribe({}, {})

                mViewModel.adapter.setRefersh(list)
                moveToPosition(0)
                Handler().postDelayed({
                    onPageIndexListener(0)
                }, 200)

                mBinding.loading.showContent()

            }, {
                if (!mBinding.loading.isError) {
                    mBinding.loading.showError()
                }
            })
    }


    /**
     * 下一页(点击)
     */
    @SuppressLint("CheckResult")
    override fun onClickNextListener(view: View?, position: Int) {
        if (position <= mViewModel.getLastItemPosition()) {

            moveToPosition(position + 1)

        }

        val obj = mViewModel.adapter.getObj(mViewModel.getIndexValid(position))

        //最后一页
        if (!mViewModel.hasNextPage(obj.chapterId)) {
            obj.hasNext = false
            mViewModel.adapter.notifyDataSetChanged()
            return
        }

        //加载最后一页
        if (position >= mViewModel.adapter.getList().size - 2 && !mViewModel.isLoadAdapter(
                mViewModel.getNextPage(obj.chapterId).chapterId
            )
        ) {
            mViewModel.getBookContent(
                mViewModel.mBooksResult!!,
                mViewModel.getNextPage(obj.chapterId)
            )
                .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe({
                    mBinding.loading.showContent()

                    val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                    mViewModel.updateReadHistory(
                        it.chapterId,
                        it.chapterName,
                        mViewModel.mBooksResult!!
                    )
                        .compose(bindToLifecycle())
                        .subscribe({}, {})

                    mViewModel.adapter.loadMore(list)
                    isNext = false
                }, {
                    Logger.i(it.message!!)
                    isNext = false
                })
        }

        //刷新有延迟
        Handler().postDelayed({
            onPageIndexListener(mViewModel.getLastVisiblePosition(mBinding.recyclerView))
        }, 200)

    }

    /**
     * 上一页(点击)
     */
    @SuppressLint("CheckResult")
    override fun onClickPreviousListener(view: View?, position: Int) {

        if (position >= 0) {
            val index = if (position == 0) 0 else position - 1
            //翻页
            mBinding.recyclerView.scrollToPosition(index)

            Logger.i("onClickPreviousListener position=$position index=$index")

        }

        val obj = mViewModel.adapter.getObj(mViewModel.getIndexValid(position))

        if (!mViewModel.hasPreviousPage(obj.chapterId) || isPrevious) {
            return
        }

        isPrevious = true
        //过滤重复加载
        if (position == 1 || position == 0) {
            //加载上一页数据
            mViewModel.getBookContent(
                mViewModel.mBooksResult!!,
                mViewModel.getPreviousPage(obj.chapterId)
            )
                .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe({
                    mBinding.loading.showContent()
                    val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                    mViewModel.updateReadHistory(
                        it.chapterId,
                        it.chapterName,
                        mViewModel.mBooksResult!!
                    )
                        .compose(bindToLifecycle())
                        .subscribe({}, {})

                    //上一页如果不指定角标,默认会刷新返回第一页
                    mViewModel.adapter.getList().addAll(0, list)
                    mViewModel.adapter.notifyDataSetChanged()
                    moveToPosition(list.size)
                    Logger.i("onClickPreviousListener list=${list.size}  listitem=${mViewModel.getLastItemPosition()}")
                    isPrevious = false
                }, {
                    isPrevious = false
                })
        } else {
            isPrevious = false
        }

        /**
         * 手动刷新界面
         * 因为刷新界面有延迟,所以
         */
        Handler().postDelayed(
            { onPageIndexListener(mViewModel.getFirstVisiblePosition(mBinding.recyclerView)) },
            200
        )

    }


    /**
     * 移动到指定位置
     */
    private fun moveToPosition(position: Int) {
        val manager = mBinding.recyclerView.layoutManager as WrapContentLinearLayoutManager
        val recyclerview = mBinding.recyclerView
        val firstItem = manager.findFirstVisibleItemPosition()
        val lastItem = manager.findLastVisibleItemPosition()
        if (position <= firstItem) {
            recyclerview.scrollToPosition(position)
        } else if (position <= lastItem) {
            val top = recyclerview.getChildAt(position - firstItem).getTop();
            recyclerview.scrollBy(0, top)
        } else {
            recyclerview.scrollToPosition(position)
        }
    }


    /**
     * 点击内容中间
     */
    override fun onClickCenterListener(view: View?, position: Int) {
        toggleMenu()
    }

    /**
     * 章节目录点击事件
     */
    @SuppressLint("CheckResult")
    override fun onClickChapterItemListener(view: View, chapterid: String) {
        mBinding.drawerLayout.closeDrawers()
        mViewModel.chapterid = chapterid
        netDataChapterContentFromId(chapterid)
//        fragment?.setCurrentReadItem(chapterid)
    }


    /**
     * 上一页(滑动)
     */
    @SuppressLint("CheckResult")
    override fun onRefresh(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int) {
        Logger.i("readbook = onRefresh")
        if (mViewModel.adapter.getList().isEmpty()) {
            return
        }

        val obj = mViewModel.adapter.getObj(mViewModel.getIndexValid(lastVisibleItemPosition))
        if (!mViewModel.hasPreviousPage(obj.chapterId) || isPrevious) return

        isPrevious = true
        val ksEntity = mViewModel.getPreviousPage(obj.chapterId)
        mViewModel.getBookContent(mViewModel.mBooksResult!!, ksEntity)
            .compose(bindToLifecycle())
            .subscribe({
                mBinding.loading.showContent()
                val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                mViewModel.updateReadHistory(
                    it.chapterId,
                    it.chapterName,
                    mViewModel.mBooksResult!!
                )
                    .compose(bindToLifecycle()).subscribe({}, {})

                mViewModel.adapter.getList().addAll(0, list)
                mViewModel.adapter.notifyItemRangeInserted(0, list.size)
                isPrevious = false
            }, {
                isPrevious = false
            })
    }

    /**
     * 下一页(滑动)
     */
    @SuppressLint("CheckResult")
    override fun onLoadMore(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int) {
        Logger.i("onLoadMore firstVisibleItemPosition=$firstVisibleItemPosition  lastVisibleItemPosition=$lastVisibleItemPosition")


        if (mViewModel.adapter.getList().isEmpty()) return

        val obj = mViewModel.adapter.getObj(mViewModel.getIndexValid(lastVisibleItemPosition))

        if (!mViewModel.hasNextPage(obj.chapterId)) {
            obj.hasNext = false
            mViewModel.adapter.notifyDataSetChanged()
            return
        }


        mBinding.loading.showContent()
        Logger.i("${this.javaClass.simpleName}  position = $lastVisibleItemPosition  itemCount=${mViewModel.adapter.getList().size}")
        mViewModel.getBookContent(mViewModel.mBooksResult!!, mViewModel.getNextPage(obj.chapterId))
            .compose(bindToLifecycle())
            .subscribe({
                val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                mViewModel.updateReadHistory(
                    it.chapterId,
                    it.chapterName,
                    mViewModel.mBooksResult!!
                )
                    .compose(bindToLifecycle()).subscribe({}, {})

                mViewModel.adapter.loadMore(list)
                isNext = false

            }, {
                Logger.i(it.message!!)
                isNext = false
            })
    }

    /**
     * 当前滑动的界面角标
     */
    override fun onPageIndexListener(position: Int) {
        //如果是最后一页.返回.因为是假数据
        if (mViewModel.adapter.getList().isEmpty() || position == mViewModel.adapter.itemCount - 1 || position < 0) {
            return
        }
        val obj = mViewModel.adapter.getObj(position)
        mViewModel.chapterid = obj.chapterId
//        mViewModel.nid = mViewModel.getNextPage(obj.chapterId).chapterId
//        mViewModel.pid = mViewModel.getPreviousPage(obj.chapterId).chapterId
        mViewModel.currentPosition = position
        mViewModel.chapterName = obj.chapterName


        fragment?.setCurrentReadItem(obj.chapterId)


        /**
         * 每次滑动页面都更新时间和角标
         */
        mViewModel.updateLastOpenTimeAndPosition(obj.chapterId, obj.textPageBean?.currentPage!!)

    }

    override fun onBackPressedSupport() {
        if (toggleMenuSwitch) {
            //如果上下切换栏显示就隐藏起来
            toggleMenu()
        } else {
            if (!mViewModel.isCollection) {
                //没收藏弹框
                showCollectionDialog()
            } else {
                EventBus.getDefault().post(UpdateReadHistoryEvent())
                super.onBackPressedSupport()
            }

        }
    }

    /**
     * 是否添加书架
     */
    private fun showCollectionDialog() {
        BookCollectionDialog(this, mViewModel).show()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            // 音量减小
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                // 音量减小时应该执行的功能代码
                if (PreferenceUtil.getBoolean(BookConstant.VOLUME_STATUS, false)) {
                    onClickNextListener(
                        mBinding.recyclerView.findViewHolderForLayoutPosition(mViewModel.currentPosition)?.itemView,
                        mViewModel.currentPosition
                    )
                    return true
                }
            }
            // 音量增大
            KeyEvent.KEYCODE_VOLUME_UP -> {
                // 音量增大时应该执行的功能代码
                if (PreferenceUtil.getBoolean(BookConstant.VOLUME_STATUS, false)) {
                    onClickPreviousListener(
                        mBinding.recyclerView.findViewHolderForLayoutPosition(mViewModel.currentPosition)?.itemView,
                        mViewModel.currentPosition
                    )
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    // 接收电池信息和时间更新的广播
    private var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra("level", 0)
            Logger.t("level").i(level.toString())
            if (intent.action == Intent.ACTION_BATTERY_CHANGED && level > 0 && abs(level - ReadBookAdapter.mBatteryLevel) >= 1) {
                //显示电池百分比
                ReadBookAdapter.mBatteryLevel = level
                mViewModel.adapter.notifyDataSetChanged()
                Logger.t("level").i("notifyDataSetChanged")
            } else if (Intent.ACTION_TIME_TICK == intent.action
                && PreferenceUtil.getInt(
                    ConfigConstant.PAGE_TIME,
                    ConfigConstant.PAGE_TIME_SHOW
                ) == ConfigConstant.PAGE_TIME_SHOW
            ) {
                //每一分钟更新时间 && 显示时间
                Logger.t("level").i("一分钟更新")
                mViewModel.adapter.notifyDataSetChanged()
            }
        }
    }
}