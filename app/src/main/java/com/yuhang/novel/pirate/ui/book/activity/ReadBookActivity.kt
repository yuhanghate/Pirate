package com.yuhang.novel.pirate.ui.book.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.yuhang.novel.pirate.extension.niceToast
import com.orhanobut.logger.Logger
import com.trello.rxlifecycle2.android.ActivityEvent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.databinding.ActivityReadBookBinding
import com.yuhang.novel.pirate.listener.OnClickChapterItemListener
import com.yuhang.novel.pirate.listener.OnPageIndexListener
import com.yuhang.novel.pirate.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.ui.book.fragment.DrawerLayoutLeftFragment
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadBookViewModel
import com.yuhang.novel.pirate.ui.main.activity.MainActivity
import com.yuhang.novel.pirate.utils.StatusBarUtil
import com.yuhang.novel.pirate.viewholder.ItemReadBookVH
import com.yuhang.novel.pirate.widget.OnScrollListener
import com.yuhang.novel.pirate.widget.ReadBookTextView
import com.yuhang.novel.pirate.widget.WrapContentLinearLayoutManager
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions


/**
 * 书籍阅读
 */
@RuntimePermissions
class ReadBookActivity : BaseActivity<ActivityReadBookBinding, ReadBookViewModel>(),
        ReadBookTextView.OnClickCenterListener, ReadBookTextView.OnClickNextListener,
        ReadBookTextView.OnClickPreviousListener, OnClickChapterItemListener, OnRefreshLoadMoreListener,
        OnPageIndexListener {


    val TAG = ReadBookActivity::class.java.simpleName
    private var mTopInAnim: Animation? = null
    private var mTopOutAnim: Animation? = null
    private var mBottomInAnim: Animation? = null
    private var mBottomOutAnim: Animation? = null
    private var mBackgroundInTransparent: Animation? = null
    private var mBackgroundOutTransparent: Animation? = null

    private var wakeLock: PowerManager.WakeLock? = null

    private var toggleMenuSwitch = false


    var fragment: DrawerLayoutLeftFragment? = null

    companion object {

        const val BOOK_ID = "book_id"
        const val DURATION: Long = 190

        fun start(context: Context, bookid: Int) {
            val intent = Intent(context, ReadBookActivity::class.java)
            intent.putExtra(BOOK_ID, bookid)
            context.startActivity(intent)
        }

    }

    override fun onLayoutId(): Int {
        return R.layout.activity_read_book
    }


    private fun getBookid() = intent.getIntExtra(BOOK_ID, -1)


    override fun onCreate(savedInstanceState: Bundle?) {
//        val logger = getLogger()
//        logger.addSplit("onCreate")
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //去除状态栏
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        super.onCreate(savedInstanceState)
        window.navigationBarColor = Color.parseColor("#F6EFDD")
//        logger.addSplit("onCreate")

    }


    override fun onStart() {
//        val logger = getLogger()
//        logger.addSplit("onStart")
        keepScreenOnWithPermissionCheck(true)
        initBattery()
        super.onStart()
//        SystemClock.sleep(20 * 1000);
//        logger.addSplit("onStart")
    }

    override fun onPause() {
//        val logger = getLogger()
//        logger.addSplit("onPause")
        unregisterReceiver(mReceiver)
        mReceiver = null
        keepScreenOnWithPermissionCheck(false)
        super.onPause()



//        logger.addSplit("onPause")
    }


    override fun initView() {
        super.initView()
//        val logger = getLogger()
//        logger.addSplit("initView")
        initViewModel()
        initContentViewHeight()
        initRefreshLayout()
        initRecyclerView()
        netDataChatpterContent()
        initDrawerView()
        onClick()
        initBackground()

//        logger.addSplit("initView")
//        logger.dumpToLog()
//        initBattery()
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
    }

    private fun initViewModel() {
        mViewModel.bookid = getBookid()
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
            toggleMenu()
            mBinding.drawerLayout.openDrawer(Gravity.START)
        }

        mBinding.loading.setOnClickListener { toggleMenu() }
        mBinding.layoutTop.refreshTv.setOnClickListener { netDataChatpterContent() }
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private fun toggleMenu() {
//        val logger = getLogger()
//        logger.addSplit("toggleMenu")
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

//        logger.addSplit("toggleMenu")
//        logger.dumpToLog()
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

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(mBinding.recyclerView)
        val linearLayoutManager = WrapContentLinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
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
//        val logger = getLogger()
        mViewModel.isCollectionBook()
        mViewModel.getLastOpenContent()
                .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe({
                    mBinding.loading.showContent()
                    val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                    mViewModel.adapter.setRefersh(list)
                    mBinding.recyclerView.scrollToPosition(it.lastContentPosition)
                    onPageIndexListener(it.lastContentPosition)
                    mViewModel.preloadedChapterContent(it.pid)
                    mViewModel.preloadedChapterContent(it.nid)
//                logger.addSplit("netDataChatpterContent -> success")
//                logger.dumpToLog()
                }, {

                    if (!mBinding.loading.isError) {
                        mBinding.loading.showError()
                    }
//                logger.addSplit("netDataChatpterContent - error")
//                logger.dumpToLog()
                }, {})
    }

    /**
     * 获取指定章节的内容
     */
    @SuppressLint("CheckResult")
    private fun netDataChapterContentFromId(chapterid: Int) {
//        val logger = getLogger()
        mBinding.loading.showLoading()
        mViewModel.getContentFromChapterid(chapterid)
                .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe({
                    //                    onPageIndexListener(0)

                    val list = mViewModel.getTxtPageList(mBinding.textPage, it)

                    mViewModel.adapter.setRefersh(list)
                    moveToPosition(0)
                    Handler().postDelayed({
                        onPageIndexListener(0)
                    }, 200)

                    mBinding.loading.showContent()
//                logger.addSplit("getContentFromChapterid->success")
//                logger.dumpToLog()

                }, {
                    if (!mBinding.loading.isError) {
                        mBinding.loading.showError()
                    }
//                logger.addSplit("getContentFromChapterid->error")
//                logger.dumpToLog()
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
            val logger = getLogger()
            mViewModel.getContentFromChapterid(obj.nid)
                    .compose(bindUntilEvent(ActivityEvent.PAUSE))
                    .subscribe({
                        mBinding.loading.showContent()

                        val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                        mViewModel.adapter.loadMore(list)
//                    logger.addSplit("onClickNextListener->success")
//                    logger.dumpToLog()
//                        onPageIndexListener(position + 1)
                    }, {
                        Logger.i(it.message!!)
//                    logger.addSplit("onClickNextListener->error")
//                    logger.dumpToLog()
                    })
        }

        onPageIndexListener(mViewModel.getLastVisiblePosition(mBinding.recyclerView))
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
//            val logger = getLogger()
//            logger.addSplit("onClickPreviousListener")
            //加载上一页数据
            mViewModel.getContentFromChapterid(obj.pid)
                    .compose(bindUntilEvent(ActivityEvent.PAUSE))
                    .subscribe({
                        mBinding.loading.showContent()
                        val list = mViewModel.getTxtPageList(mBinding.textPage, it)

                        //上一页如果不指定角标,默认会刷新返回第一页
                        mViewModel.adapter.getList().addAll(0, list)
                        mViewModel.adapter.notifyDataSetChanged()
//                    mViewModel.adapter.notifyItemRangeInserted(0, list.size)
                        moveToPosition(list.size)
                        Logger.i("onClickPreviousListener list=${list.size}  listitem=${mViewModel.getLastItemPosition()}")

//                    logger.addSplit("onClickPreviousListener->success")
//                    logger.dumpToLog()
                    }, {
                        //                    logger.addSplit("onClickPreviousListener->error")
//                    logger.dumpToLog()
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
        val logger = getLogger()
//        logger.addSplit("moveToPosition")
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

//        logger.addSplit("moveToPosition")
//        logger.dumpToLog()
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
    }

    /**
     * 上一页(滑动)
     */
    @SuppressLint("CheckResult")
    override fun onRefresh(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int) {
//        val logger = getLogger()
        Logger.i("readbook = onRefresh")
        val obj = mViewModel.adapter.getObj(mViewModel.getIndexValid(lastVisibleItemPosition))
        if (obj.pid == -1) return

        mViewModel.getContentFromChapterid(obj.pid)
                .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe({
                    mBinding.loading.showContent()
                    val list = mViewModel.getTxtPageList(mBinding.textPage, it)
                    mViewModel.adapter.getList().addAll(0, list)
                    mViewModel.adapter.notifyItemRangeInserted(0, list.size)
//                logger.addSplit("onRefresh -> success")
//                logger.dumpToLog()
                }, {
                    //                logger.addSplit("onRefresh -> error")
//                logger.dumpToLog()
                })
    }

    /**
     * 下一页(滑动)
     */
    @SuppressLint("CheckResult")
    override fun onLoadMore(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int) {
        Logger.i("onLoadMore firstVisibleItemPosition=$firstVisibleItemPosition  lastVisibleItemPosition=$lastVisibleItemPosition")


//        val logger = getLogger()
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
                    mViewModel.adapter.loadMore(list)

//                logger.addSplit("onLoadMore")
//                logger.dumpToLog()
                }, {
                    Logger.i(it.message!!)
//                logger.addSplit("onLoadMore")
//                logger.dumpToLog()
                })
    }

    /**
     * 当前滑动的界面角标
     */
    override fun onPageIndexListener(position: Int) {
//        val logger = getLogger()
        //如果是最后一页.返回.因为是假数据
        if (position == mViewModel.adapter.itemCount - 1 || position < 0) {
            return
        }
        val obj = mViewModel.adapter.getObj(position)
        mViewModel.chapterid = obj.chapterId
        mViewModel.nid = obj.nid
        mViewModel.pid = obj.pid
        mViewModel.currentPosition = position


        fragment?.setCurrentReadItem(obj.chapterId)


        /**
         * 每次滑动页面都更新时间和角标
         */
        mViewModel.updateLastOpenTimeAndPosition(obj.chapterId, obj.txtPage?.position!!)


        /**
         * 滑动的时候预加载上一章节和下一章节数据
         */
        mViewModel.preloadedChapterContent(obj.pid)
        mViewModel.preloadedChapterContent(obj.nid)

//        logger.addSplit("onPageIndexListener")
//        logger.dumpToLog()
    }

    override fun onBackPressed() {
        if (toggleMenuSwitch) {
            //如果上下切换栏显示就隐藏起来
            toggleMenu()
        } else {
            if (!mViewModel.isCollection) {
                //没收藏弹框
                showCollectionDialog()
            } else {
                super.onBackPressed()
            }

        }
    }

    private fun showCollectionDialog() {
        MaterialDialog(this).show {
            title(text = "提示")
            message(text = "是否添加到书架?")
            negativeButton(text = "取消")
            positiveButton(text = "确定", click = object : DialogCallback {
                @SuppressLint("CheckResult")
                override fun invoke(p1: MaterialDialog) {
                    mViewModel.insertCollection().compose(bindToLifecycle())
                            .subscribe({
                                niceToast("加入成功")
                                mViewModel.isCollection = true
                                this@ReadBookActivity.onBackPressed()

                            },
                                    { niceToast("加入失败") })
                }

            })
        }
    }

    /**
     * 锁定屏幕不灰屏
     */
    @NeedsPermission(Manifest.permission.WAKE_LOCK)
    fun keepScreenOn(on: Boolean) {
        if (on) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                    MainActivity::class.java.simpleName
            )
            wakeLock?.acquire(10 * 60 * 1000L /*10 minutes*/)
        } else {
            if (wakeLock != null) {
                wakeLock?.release()
                wakeLock = null
            }
        }
    }

    /**
     * 权限申请
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    // 接收电池信息和时间更新的广播
    private var mReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            val logger = getLogger()
            val level = intent.getIntExtra("level", 0)
            val bookVH =
                    mBinding.recyclerView.findViewHolderForAdapterPosition(mViewModel.currentPosition) as? ItemReadBookVH
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                mViewModel.adapter.mBatteryLevel = level
                bookVH?.mBinding?.contentTv?.updateBattery(level)
            } else if (intent.action == Intent.ACTION_TIME_TICK) {
                mViewModel.adapter.notifyDataSetChanged()
            }// 监听分钟的变化

//            logger.addSplit("接收电池信息和时间更新的广播")
//            logger.dumpToLog()
        }
    }
}