package com.yuhang.novel.pirate.ui.book.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.gson.Gson
import com.hunter.library.debug.HunterDebug
import com.orhanobut.logger.Logger
import com.trello.rxlifecycle2.android.ActivityEvent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.constant.ConfigConstant
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivityReadBookBinding
import com.yuhang.novel.pirate.eventbus.UpdateReadHistoryEvent
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.listener.OnClickChapterItemListener
import com.yuhang.novel.pirate.listener.OnPageIndexListener
import com.yuhang.novel.pirate.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.book.dialog.BookCollectionDialog
import com.yuhang.novel.pirate.ui.book.dialog.DownloadChapterDialog
import com.yuhang.novel.pirate.ui.book.fragment.DrawerLayoutLeftFragment
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadBookViewModel
import com.yuhang.novel.pirate.ui.payment.activity.VipNoteDialog
import com.yuhang.novel.pirate.ui.resouce.activity.ResouceListKdActivity
import com.yuhang.novel.pirate.utils.LogUtils
import com.yuhang.novel.pirate.utils.StatusBarUtil
import com.yuhang.novel.pirate.widget.OnScrollListener
import com.yuhang.novel.pirate.widget.ReadBookTextView
import com.yuhang.novel.pirate.widget.ReadBookView
import com.yuhang.novel.pirate.widget.WrapContentLinearLayoutManager
import org.greenrobot.eventbus.EventBus


/**
 * 书籍阅读
 */
open class ReadBookActivity : BaseActivity<ActivityReadBookBinding, ReadBookViewModel>(),
    ReadBookTextView.OnClickCenterListener, ReadBookTextView.OnClickNextListener,
    ReadBookTextView.OnClickPreviousListener, OnClickChapterItemListener, OnRefreshLoadMoreListener,
    OnPageIndexListener {


    val log = LogUtils()


    var fragment: DrawerLayoutLeftFragment? = null

    /**
     * 是否正在加载上一页,防止重复加载
     */
    private var isPrevious = false

    /**
     * 是否正在加载下一页,防止重复加载
     */
    private var isNext = false

    /**
     * Activity View扩展使用
     */
    val mReadBookView by lazy { ReadBookView(this, mViewModel) }

    companion object {
        const val CHAPTERID = "chapter_id"
        const val BOOKS_RESULT = "books_result"
        const val IS_INIT_CHAPTER = "is_init_chapter"

        const val DURATION: Long = 190

        /**
         * 根据章节跳转
         */
        fun start(context: Context, obj: BooksResult, chapterid: String = "") {
            val intent = Intent(context, ReadBookActivity::class.java)
            intent.putExtra(BOOKS_RESULT, obj.toJson())
            intent.putExtra(CHAPTERID, chapterid)
            startIntent(context, intent)
        }

        fun start(context: Context, obj: BooksResult, initChapter: Boolean = false) {
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
        mReadBookView.initStatusTool(this)
    }

    override fun onStatusColor(): Int {
        return BookConstant.getPageBackground()
    }

    /**
     * 是否重新加载章节列表
     */
    private fun getInitChapter() = intent.getBooleanExtra(IS_INIT_CHAPTER, false)

    private fun getChapterid() = intent.getStringExtra(CHAPTERID) ?: ""


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
        unregisterReceiver(mReadBookView.mReceiver)
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

    override fun initView() {
        super.initView()
        initViewModel()
        initRefreshLayout()
        initRecyclerView()
        initBackground()
        mReadBookView.resetBackground(BookConstant.getPageColorIndex())
        onClick()
        ReadBookView(this, mViewModel)
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
        mViewModel.onResume(this)
    }

    /**
     * 初始化章节进度条
     */
    @SuppressLint("CheckResult")
    private fun initChapterProgressSeekBar() {
        mReadBookView.initChapterProgressSeekBar()
    }

    /**
     * 初始化字体进度条
     */
    private fun initFontSeekBar() {
        mReadBookView.initFontSeekBar()
    }

    /**
     * 初始化电量
     */
    private fun initBattery() {
        //注册广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(mReadBookView.mReceiver, intentFilter)
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

    @SuppressLint("WrongConstant")
    private fun onClick() {
        mBinding.bgShadow.clickWithTrigger { mReadBookView.toggleMenu() }
        mBinding.layoutTop.backIv.clickWithTrigger {
            mReadBookView.toggleMenu()
            super.onBackPressed()
        }
        mBinding.layoutButton.chapterDirTv.clickWithTrigger {
            mViewModel.onUMEvent(this, UMConstant.TYPE_READ_CLICK_DIR_CHANPTER, "阅读页 -> 点击阅读目录")
            fragment?.resetBackground()
            mReadBookView.toggleMenu()
            mBinding.drawerLayout.openDrawer(Gravity.START)
            fragment?.setCurrentReadItem(mViewModel.chapterid)
        }

        mBinding.loading.clickWithTrigger { mReadBookView.toggleMenu() }

        //刷新页面
        mBinding.layoutTop.refreshTv.clickWithTrigger {
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
            //强制刷新不走缓存

            mReadBookView.toggleMenu()
            Handler().postDelayed({
                netDataChatpterContent(isCache = false)
            }, 500)

        }

        mBinding.layoutButton.pageBg1.clickWithTrigger {
            mReadBookView.resetBackground(0)
            mViewModel.adapter.notifyDataSetChanged()
        }
        mBinding.layoutButton.pageBg2.clickWithTrigger {
            mReadBookView.resetBackground(1)
            mViewModel.adapter.notifyDataSetChanged()
        }
        mBinding.layoutButton.pageBg3.clickWithTrigger {
            mReadBookView.resetBackground(2)
            mViewModel.adapter.notifyDataSetChanged()
        }
        mBinding.layoutButton.pageBg4.clickWithTrigger {
            mReadBookView.resetBackground(3)
            mViewModel.adapter.notifyDataSetChanged()
        }

        mBinding.layoutButton.contentBackgroundTv.clickWithTrigger {
            mBinding.layoutButton.colorLl.visibility = View.VISIBLE
            mBinding.layoutButton.fontLl.visibility = View.GONE
            mBinding.layoutButton.chapterProgressLl.visibility = View.GONE
        }


        //字体大小
        mBinding.layoutButton.fontTv.clickWithTrigger {
            mBinding.layoutButton.fontLl.visibility = View.VISIBLE
            mBinding.layoutButton.colorLl.visibility = View.GONE
            mBinding.layoutButton.chapterProgressLl.visibility = View.GONE
        }

        //进度
        mBinding.layoutButton.chapterProgressTv.clickWithTrigger {
            mBinding.layoutButton.chapterProgressLl.visibility = View.VISIBLE
            mBinding.layoutButton.colorLl.visibility = View.GONE
            mBinding.layoutButton.fontLl.visibility = View.GONE
            mBinding.layoutButton.chapterProgressSb.setProgress(
                mViewModel.getChapterIndex().toFloat() + 1
            )
        }

        //页面重新刷新
        mBinding.loading.setRetryListener { netDataChatpterContent(isCache = false) }

        //换源
        mBinding.layoutTop.resouceTv.clickWithTrigger {
            mReadBookView.toggleMenu()
            ResouceListKdActivity.start(
                this,
                mViewModel.mBooksResult!!,
                mViewModel.getChapterIndex()
            )
        }

        //上一章
        mBinding.layoutButton.btnPreviousChapter.clickWithTrigger {
            if (mViewModel.hasPreviousPage(mViewModel.chapterid)) {
                netDataChapterContentFromId(mViewModel.getPreviousPage(mViewModel.chapterid).chapterId)
                return@clickWithTrigger
            }
            niceToast("没有上一页了")
        }

        //下一章
        mBinding.layoutButton.btnNextChapter.clickWithTrigger {
            if (mViewModel.hasNextPage(mViewModel.chapterid)) {
                netDataChapterContentFromId(mViewModel.getNextPage(mViewModel.chapterid).chapterId)
                return@clickWithTrigger
            }
            niceToast("最后一页了")
        }

        //下载
        mBinding.layoutTop.downloadTv.clickWithTrigger { downloadBook() }
    }

    /**
     * 下载章节
     */
    private fun downloadBook() {
        mReadBookView.toggleMenu()
        DownloadChapterDialog(this, mViewModel, getBooksResult()).show()
    }


    /**
     * 初始化DrawerView
     */
    @SuppressLint("WrongConstant")
    private fun initDrawerView() {
        mReadBookView.initDrawerView()
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
            initContentViewHeight()
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
    fun netDataChatpterContent(isCache: Boolean = true) {
        mBinding.loading.showLoading()
        mViewModel.getLastBookContent(mViewModel.mBooksResult!!, isCache)
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
            }, {})
    }

    /**
     * 获取指定章节的内容
     */
    @SuppressLint("CheckResult")
    fun netDataChapterContentFromId(chapterid: String) {
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
        mReadBookView.toggleMenu()
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
        //判断是否vip,非会员看3页
        if (mViewModel.goVipActivity(this, position)) return

        //如果是最后一页.返回.因为是假数据
        if (mViewModel.adapter.getList()
                .isEmpty() || position == mViewModel.adapter.itemCount - 1 || position < 0
        ) {
            return
        }


        val obj = mViewModel.adapter.getObj(position)
        mViewModel.chapterid = obj.chapterId
        mViewModel.currentPosition = position
        mViewModel.chapterName = obj.chapterName


        fragment?.setCurrentReadItem(obj.chapterId)


        /**
         * 每次滑动页面都更新时间和角标
         */
        mViewModel.updateLastOpenTimeAndPosition(obj.chapterId, obj.textPageBean?.currentPage!!)

        //进度条点击下一章,更新章节名称
        mBinding.layoutButton.chapterNameTv.text = mViewModel.chapterMap[mViewModel.chapterid]?.name

    }

    override fun onBackPressedSupport() {
        if (mReadBookView.toggleMenuSwitch) {
            //如果上下切换栏显示就隐藏起来
            mReadBookView.toggleMenu()
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
        val volume = mReadBookView.onVolume(keyCode)
        if (volume) return volume
        return super.onKeyDown(keyCode, event)
    }

}