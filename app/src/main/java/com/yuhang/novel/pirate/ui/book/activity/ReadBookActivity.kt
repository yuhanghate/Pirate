package com.yuhang.novel.pirate.ui.book.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.orhanobut.logger.Logger
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xw.repo.BubbleSeekBar
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.constant.ConfigConstant
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivityReadBookBinding
import com.yuhang.novel.pirate.eventbus.UpdateChapterEvent
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.listener.OnClickChapterItemListener
import com.yuhang.novel.pirate.listener.OnPageIndexListener
import com.yuhang.novel.pirate.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.book.adapter.ReadBookAdapter
import com.yuhang.novel.pirate.ui.book.fragment.DrawerLayoutLeftFragment
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadBookViewModel
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

    companion object {
        val TAG = ReadBookActivity::class.java.simpleName
        const val BOOK_ID = "book_id"
        const val CHAPTERID = "chapter_id"

        const val DURATION: Long = 190

        fun start(context: Activity, bookid: Long) {
            val intent = Intent(context, ReadBookActivity::class.java)
            intent.putExtra(BOOK_ID, bookid)
            startIntent(context, intent)
        }

        /**
         * 转转指定章节
         */
        fun start(context: Activity, bookid: Long, chapterid: Int) {
            val intent = Intent(context, ReadBookActivity::class.java)
            intent.putExtra(BOOK_ID, bookid)
            intent.putExtra(CHAPTERID, chapterid)
            startIntent(context, intent)
        }
    }


    override fun onLayoutId(): Int {
        return R.layout.activity_read_book
    }

    override fun initStatusTool() {
        StatusBarUtil.setColor(this, onStatusColor(), 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override fun onStatusColor(): Int {
        return BookConstant.getPageBackground()
    }

    private fun getBookid() = intent.getLongExtra(BOOK_ID, -1)

    private fun getChapterid() = intent.getIntExtra(CHAPTERID, -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //去除状态栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onCreate(savedInstanceState)
        window.navigationBarColor = BookConstant.getPageBackground()
    }


    override fun onPause() {

//        keepScreenOnWithPermissionCheck(false)
        super.onPause()
//        mViewModel.onPageEnd("阅读内容页")
        mViewModel.onPause(this)
        mBinding.root.keepScreenOn = false
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }


    override fun initView() {
        super.initView()
        initViewModel()
        initContentViewHeight()
        initRefreshLayout()
        initRecyclerView()

        initDrawerView()
        initFontSeekBar()
        initBackground()
        resetBackground(BookConstant.getPageColorIndex())
        initChapterProgressSeekBar()
        onClick()


    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //如果获取焦点,并且RecyclerView是第一次加载
        if (hasFocus && mViewModel.adapter.getList().isEmpty()) {
            //当Activity尺寸计算好以后,进行加载.因为需要动态根据尺寸分页
            if (getChapterid() > 0) {
                //打开指定章节
                mViewModel.chapterid = getChapterid()
                netDataChapterContentFromId(getChapterid())
            } else {
                //打开最近章节
                netDataChatpterContent()
            }
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

        mBinding.layoutButton.seekBar
        mViewModel.getChapterList()
            .compose(bindToLifecycle())
            .subscribe({
                mViewModel.chapterList = it
//                    val entity = it.filter { it.chapterId == mViewModel.chapterid }.map { it }.toList()
                mBinding.layoutButton.chapterProgressSb.configBuilder
                    .min(1f)
                    .max((it.size).toFloat())
                    .progress(1f)
                    .sectionCount(it.size - 1)
                    .trackColor(ContextCompat.getColor(this, R.color.md_grey_500))
                    .secondTrackColor(ContextCompat.getColor(this, R.color.icons))
                    .showSectionText()
                    .bubbleColor(ContextCompat.getColor(this, R.color.secondary_text))
                    .bubbleTextSize(18)
                    .build()

//                    mBinding.layoutButton.chapterProgressSb.setPercentage()

                mBinding.layoutButton.chapterProgressSb.onProgressChangedListener =
                    object : BubbleSeekBar.OnProgressChangedListenerAdapter() {
                        override fun onProgressChanged(
                            bubbleSeekBar: BubbleSeekBar?,
                            progress: Int,
                            progressFloat: Float,
                            fromUser: Boolean
                        ) {
                            super.onProgressChanged(bubbleSeekBar, progress, progressFloat, fromUser)
                            if (mViewModel.chapterList.isNotEmpty()) {
                                mBinding.layoutButton.chapterNameTv.text = mViewModel.chapterList[progress - 1].name
                            }
//                            bubbleSeekBar?.setBubbleProgressText("$progress/${bubbleSeekBar.max}")
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

            }, {})
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
                override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
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

    private fun initViewModel() {
        mViewModel.bookid = getBookid()
        mViewModel.clearLable()
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
                    "bookid" to mViewModel.bookid.toString(),
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

        fragment = supportFragmentManager.findFragmentById(R.id.fg_left_menu) as? DrawerLayoutLeftFragment

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
                mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.START)
            }
        })

        fragment?.bookid = mViewModel.bookid
        fragment?.chapterid = mViewModel.chapterid
        fragment?.mOnClickChapterItemListener = this
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

    }

    /**
     * 获取小说最近阅读章节内容
     */

    @SuppressLint("CheckResult")
    private fun netDataChatpterContent() {
        mBinding.loading.showLoading()
        mViewModel.isCollectionBook()
        mViewModel.getLastOpenContent()
            .compose(bindToLifecycle())
            .subscribe({
                mBinding.loading.showContent()
                val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                mViewModel.updateReadHistory(it.chapterId, it.chapterName).compose(bindToLifecycle()).subscribe({}, {})

                mViewModel.adapter.setRefersh(list)
                mBinding.recyclerView.scrollToPosition(it.lastContentPosition)
                onPageIndexListener(it.lastContentPosition)
                mViewModel.preloadedChapterContent(it.pid)
                mViewModel.preloadedChapterContent(it.nid)

//                Logger.t("空白").i("page content = ${it.content} size = ${list.size} 页  ")
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
    private fun netDataChapterContentFromId(chapterid: Int) {
        mBinding.loading.showLoading()
        mViewModel.getContentFromChapterid(chapterid)
            .compose(bindUntilEvent(ActivityEvent.PAUSE))
            .subscribe({

                val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                mViewModel.updateReadHistory(it.chapterId, it.chapterName).compose(bindToLifecycle()).subscribe({}, {})

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
        //加载最后一页
        if (position >= mViewModel.adapter.getList().size - 2 && !mViewModel.isLoadAdapter(obj.nid)) {
            mViewModel.getContentFromChapterid(obj.nid)
                .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe({
                    mBinding.loading.showContent()

                    val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                    mViewModel.updateReadHistory(it.chapterId, it.chapterName).compose(bindToLifecycle())
                        .subscribe({}, {})

                    mViewModel.adapter.loadMore(list)
                }, {
                    Logger.i(it.message!!)
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
        Logger.i("onClickPreviousListener obj.pid=${obj.pid} listitem=${mViewModel.getLastItemPosition()}")
        //过滤重复加载
        if (position == 1 || position == 0) {
            //加载上一页数据
            mViewModel.getContentFromChapterid(obj.pid)
                .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe({
                    mBinding.loading.showContent()
                    val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                    mViewModel.updateReadHistory(it.chapterId, it.chapterName).compose(bindToLifecycle())
                        .subscribe({}, {})

                    //上一页如果不指定角标,默认会刷新返回第一页
                    mViewModel.adapter.getList().addAll(0, list)
                    mViewModel.adapter.notifyDataSetChanged()
                    moveToPosition(list.size)
                    Logger.i("onClickPreviousListener list=${list.size}  listitem=${mViewModel.getLastItemPosition()}")

                }, {
                })
        }

        /**
         * 手动刷新界面
         * 因为刷新界面有延迟,所以
         */
        Handler().postDelayed({ onPageIndexListener(mViewModel.getFirstVisiblePosition(mBinding.recyclerView)) }, 200)

    }


    /**
     * 移动到指定位置
     */
    private fun moveToPosition(position: Int) {
        val manager = mBinding.recyclerView.layoutManager as LinearLayoutManager
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
    override fun onClickChapterItemListener(view: View, chapterid: Int) {
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
        if (obj.pid == -1) return

        mViewModel.getContentFromChapterid(obj.pid)
            .compose(bindUntilEvent(ActivityEvent.PAUSE))
            .subscribe({
                mBinding.loading.showContent()
                val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                mViewModel.updateReadHistory(it.chapterId, it.chapterName).compose(bindToLifecycle()).subscribe({}, {})

                mViewModel.adapter.getList().addAll(0, list)
                mViewModel.adapter.notifyItemRangeInserted(0, list.size)
            }, {
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

        if (obj.nid == -1) {
            return
        }
        Logger.i("${this.javaClass.simpleName}  position = $lastVisibleItemPosition  itemCount=${mViewModel.adapter.getList().size}")
        mViewModel.getContentFromChapterid(obj.nid)
            .compose(bindUntilEvent(ActivityEvent.PAUSE))
            .subscribe({
                mBinding.loading.showContent()

                val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                mViewModel.updateReadHistory(it.chapterId, it.chapterName).compose(bindToLifecycle()).subscribe({}, {})

                mViewModel.adapter.loadMore(list)

            }, {
                Logger.i(it.message!!)
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
        mViewModel.nid = obj.nid
        mViewModel.pid = obj.pid
        mViewModel.currentPosition = position
        mViewModel.chapterName = obj.chapterName


        fragment?.setCurrentReadItem(obj.chapterId)


        /**
         * 每次滑动页面都更新时间和角标
         */
        mViewModel.updateLastOpenTimeAndPosition(obj.chapterId, obj.textPageBean?.currentPage!!)


        /**
         * 滑动的时候预加载上一章节和下一章节数据
         */
        mViewModel.preloadedChapterContent(obj.pid)
        mViewModel.preloadedChapterContent(obj.nid)


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
                super.onBackPressedSupport()
            }

        }
    }

    private fun showCollectionDialog() {
        MaterialDialog(this).show {
            title(text = "提示")
            message(text = "是否添加到书架?")
            negativeButton(text = "取消", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    mViewModel.onUMEvent(this@ReadBookActivity, UMConstant.TYPE_DETAILS_CLICK_REMOVE_BOOKCASE, "取消添加书架")
                    mViewModel.isCollection = true
                    this@ReadBookActivity.onBackPressedSupport()
                }
            })
            positiveButton(text = "确定", click = object : DialogCallback {
                @SuppressLint("CheckResult")
                override fun invoke(p1: MaterialDialog) {
                    mViewModel.postCollection()
                    mViewModel.insertCollection().compose(bindToLifecycle())
                        .subscribe({
                            mViewModel.onUMEvent(
                                this@ReadBookActivity,
                                UMConstant.TYPE_DETAILS_CLICK_REMOVE_BOOKCASE,
                                "确定添加书架"
                            )
                            niceToast("加入成功")
                            mViewModel.isCollection = true
                            EventBus.getDefault().post(UpdateChapterEvent())
                            this@ReadBookActivity.onBackPressed()

                        },
                            { niceToast("加入失败") })
                }

            })
        }
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