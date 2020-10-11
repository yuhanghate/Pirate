package com.yuhang.novel.pirate.widget

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.constant.ConfigConstant
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.book.adapter.ReadBookAdapter
import com.yuhang.novel.pirate.ui.book.fragment.DrawerLayoutLeftFragment
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadBookViewModel
import com.yuhang.novel.pirate.widget.bubble.BubbleSeekBar
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * 阅读的View相关处理
 */
class ReadBookView(val activity:ReadBookActivity, val vm:ReadBookViewModel) {

    private var mTopInAnim: Animation? = null
    private var mTopOutAnim: Animation? = null
    private var mBottomInAnim: Animation? = null
    private var mBottomOutAnim: Animation? = null
    private var mBackgroundInTransparent: Animation? = null
    private var mBackgroundOutTransparent: Animation? = null

    var toggleMenuSwitch = false


    /**
     * 接收电池信息和时间更新的广播
     */
    var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra("level", 0)
            Logger.t("level").i(level.toString())
            if (intent.action == Intent.ACTION_BATTERY_CHANGED && level > 0 && abs(level - ReadBookAdapter.mBatteryLevel) >= 1) {
                //显示电池百分比
                ReadBookAdapter.mBatteryLevel = level
                vm.adapter.notifyDataSetChanged()
                Logger.t("level").i("notifyDataSetChanged")
            } else if (Intent.ACTION_TIME_TICK == intent.action
                && PreferenceUtil.getInt(
                    ConfigConstant.PAGE_TIME,
                    ConfigConstant.PAGE_TIME_SHOW
                ) == ConfigConstant.PAGE_TIME_SHOW
            ) {
                //每一分钟更新时间 && 显示时间
                Logger.t("level").i("一分钟更新")
                vm.adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * 初始化DrawerView
     * 侧滑栏
     */
    @SuppressLint("WrongConstant")
    fun initDrawerView() {
        activity.fragment =
            activity.supportFragmentManager.findFragmentById(R.id.fg_left_menu) as? DrawerLayoutLeftFragment

        activity.mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.START)
        activity.mBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                activity.mBinding.drawerLayout.setDrawerLockMode(
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.START
                )
            }

            override fun onDrawerOpened(drawerView: View) {
                activity.mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.START)
            }
        })

        activity.fragment?.bookid = vm.mBooksResult?.getBookid()!!
        activity.fragment?.chapterList = vm.chapterList
        activity.fragment?.chapterid = vm.chapterid
        activity.fragment?.mOnClickChapterItemListener = activity
        activity.fragment?.setRefreshView()
    }


    /**
     * 初始化章节进度条
     */
    fun initChapterProgressSeekBar() {
        val size = vm.chapterList.size
        activity.mBinding.layoutButton.chapterProgressSb.configBuilder
            .min(1f)
            .max(size.toFloat())
            .progress(1f)
            .sectionCount(size - 1)
            .trackColor(ContextCompat.getColor(activity, R.color.md_grey_500))
            .secondTrackColor(ContextCompat.getColor(activity, R.color.icons))
//            .showSectionText()
            .bubbleColor(ContextCompat.getColor(activity, R.color.secondary_text))
            .bubbleTextSize(18)
            .build()


        activity.mBinding.layoutButton.chapterProgressSb.onProgressChangedListener =
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
                    if (vm.chapterList.isNotEmpty()) {
                        activity.mBinding.layoutButton.chapterNameTv.text =
                            vm.chapterList[progress - 1].name
                    }
                }

                override fun getProgressOnActionUp(
                    bubbleSeekBar: BubbleSeekBar?,
                    progress: Int,
                    progressFloat: Float
                ) {
                    super.getProgressOnActionUp(bubbleSeekBar, progress, progressFloat)

                    activity.lifecycleScope.launch {
                        activity.netDataChapterContentFromId(vm.chapterList[progress - 1].chapterId)
                    }

                }
            }
    }


    /**
     * 初始化字体进度条
     */
    fun initFontSeekBar() {
        activity.mBinding.layoutButton.seekBar
            .configBuilder
            .min(1f)
            .max(12f)
            .progress(BookConstant.getFontProgress())
            .sectionCount(11)
            .trackColor(ContextCompat.getColor(activity, R.color.md_grey_500))
            .secondTrackColor(ContextCompat.getColor(activity, R.color.icons))
            .thumbColor(ContextCompat.getColor(activity, R.color.secondary_text))
            .showSectionText()
            .sectionTextColor(ContextCompat.getColor(activity, R.color.secondary_text))
            .sectionTextSize(18)
            .showThumbText()
            .touchToSeek()
            .thumbTextColor(ContextCompat.getColor(activity, R.color.secondary_text))
            .thumbTextSize(18)
            .bubbleColor(ContextCompat.getColor(activity, R.color.secondary_text))
            .bubbleTextSize(22)
            .showSectionMark()
            .seekBySection()
            .autoAdjustSectionMark()
            .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
            .build()

        activity.mBinding.layoutButton.seekBar.onProgressChangedListener =
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
                            activity.netDataChatpterContent()
                        }
                        2 -> {
                            BookConstant.setPageTextSize(16f)
                            activity.netDataChatpterContent()
                        }
                        3 -> {
                            BookConstant.setPageTextSize(17f)
                            activity.netDataChatpterContent()
                        }
                        4 -> {
                            BookConstant.setPageTextSize(18f)
                            activity.netDataChatpterContent()
                        }
                        5 -> {
                            BookConstant.setPageTextSize(19f)
                            activity.netDataChatpterContent()
                        }
                        6 -> {
                            BookConstant.setPageTextSize(20f)
                            activity.netDataChatpterContent()
                        }
                        7 -> {
                            BookConstant.setPageTextSize(21f)
                            activity.netDataChatpterContent()
                        }
                        8 -> {
                            BookConstant.setPageTextSize(22f)
                            activity.netDataChatpterContent()
                        }
                        9 -> {
                            BookConstant.setPageTextSize(23f)
                            activity.netDataChatpterContent()
                        }
                        10 -> {
                            BookConstant.setPageTextSize(24f)
                            activity.netDataChatpterContent()
                        }
                        11 -> {
                            BookConstant.setPageTextSize(25f)
                            activity.netDataChatpterContent()
                        }
                        12 -> {
                            BookConstant.setPageTextSize(26f)
                            activity.netDataChatpterContent()
                        }
                    }
                }
            }
    }


    /**
     * 状态栏
     */
    fun initStatusTool(activity: ReadBookActivity) {
        val color = PreferenceUtil.getString("page_color", "#000000")
        ImmersionBar.with(activity)
            .transparentStatusBar()
            .statusBarColor(color)
            .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
            .statusBarColor(color)
            .navigationBarColor(color)
            .statusBarColorTransform(color)
            .navigationBarColorTransform(color)
            .init()
    }


    /**
     * 初始化菜单动画
     */
    private fun initMenuAnim() {
        if (mTopInAnim != null) return

        mTopInAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_top_in)
        mTopOutAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_top_out)
        mBottomInAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_in)
        mBottomOutAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_out)
        mBackgroundInTransparent = AnimationUtils.loadAnimation(activity, R.anim.slide_in_transparent)
        mBackgroundOutTransparent = AnimationUtils.loadAnimation(activity, R.anim.slide_out_transparent)
        //退出的速度要快
        mTopOutAnim?.duration = ReadBookActivity.DURATION
        mBottomOutAnim?.duration = ReadBookActivity.DURATION
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    fun toggleMenu() {
        initMenuAnim()

        if (activity.mBinding.layoutTop.root.visibility == View.VISIBLE) {
            //关闭
            activity.mBinding.layoutTop.root.startAnimation(mTopOutAnim)
            activity.mBinding.layoutButton.root.startAnimation(mBottomOutAnim)
            activity.mBinding.bgShadow.startAnimation(mBackgroundOutTransparent)

            Handler().postDelayed({
                activity.mBinding.layoutTop.root.visibility = View.INVISIBLE
                activity.mBinding.layoutButton.root.visibility = View.INVISIBLE
                activity.mBinding.bgShadow.visibility = View.INVISIBLE

                //关闭时隐藏背景
                activity.mBinding.layoutButton.colorLl.visibility = View.GONE
                //关闭字体
                activity.mBinding.layoutButton.fontLl.visibility = View.GONE
                //关闭进度
                activity.mBinding.layoutButton.chapterProgressLl.visibility = View.GONE
            }, ReadBookActivity.DURATION)

            toggleMenuSwitch = false
        } else {

            //打开
            activity.mBinding.layoutTop.root.visibility = View.VISIBLE
            activity.mBinding.layoutButton.root.visibility = View.VISIBLE
            activity.mBinding.bgShadow.visibility = View.VISIBLE
            activity.mBinding.bgShadow.startAnimation(mBackgroundInTransparent)
            activity.mBinding.layoutTop.root.startAnimation(mTopInAnim)
            activity.mBinding.layoutButton.root.startAnimation(mBottomInAnim)
            toggleMenuSwitch = true
        }
    }

    /**
     * 音量键翻页
     */
    fun onVolume(keyCode: Int):Boolean {
        when (keyCode) {
            // 音量减小
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                // 音量减小时应该执行的功能代码
                if (PreferenceUtil.getBoolean(BookConstant.VOLUME_STATUS, false)) {
                    activity.onClickNextListener(
                        activity.mBinding.recyclerView.findViewHolderForLayoutPosition(vm.currentPosition)?.itemView,
                        vm.currentPosition
                    )
                    return true
                }
            }
            // 音量增大
            KeyEvent.KEYCODE_VOLUME_UP -> {
                // 音量增大时应该执行的功能代码
                if (PreferenceUtil.getBoolean(BookConstant.VOLUME_STATUS, false)) {
                    activity.onClickPreviousListener(
                        activity.mBinding.recyclerView.findViewHolderForLayoutPosition(vm.currentPosition)?.itemView,
                        vm.currentPosition
                    )
                    return true
                }
            }
        }
        return false
    }

    /**
     * 重置背景颜色
     */
    fun resetBackground(index: Int) {
        BookConstant.setPageBackground(index)
        activity.mBinding.root.setBackgroundColor(BookConstant.getPageBackground())
        activity.mBinding.layoutButton.root.setBackgroundColor(BookConstant.getPageBackground())

        //底部栏字体颜色
        activity.mBinding.layoutButton.chapterDirTv.setBackgroundResource(BookConstant.getReadBookButton())
        activity.mBinding.layoutButton.contentBackgroundTv.setBackgroundResource(BookConstant.getReadBookButton())
        activity.mBinding.layoutButton.fontTv.setBackgroundResource(BookConstant.getReadBookButton())
        activity.mBinding.layoutButton.chapterProgressTv.setBackgroundResource(BookConstant.getReadBookButton())

        //底部栏背景颜色
        activity.mBinding.layoutButton.chapterDirTv.setTextColor(BookConstant.getPageTextColor())
        activity.mBinding.layoutButton.contentBackgroundTv.setTextColor(BookConstant.getPageTextColor())
        activity.mBinding.layoutButton.fontTv.setTextColor(BookConstant.getPageTextColor())
        activity.mBinding.layoutButton.chapterProgressTv.setTextColor(BookConstant.getPageTextColor())

        //navigateion状态栏颜色
        activity.window.navigationBarColor = BookConstant.getPageBackground()

        //顶部栏颜色和字体颜色
        activity.mBinding.layoutTop.root.setBackgroundColor(BookConstant.getPageBackground())
        activity.mBinding.layoutTop.resouceTv.setTextColor(BookConstant.getPageTextColor())
//        mBinding.layoutTop.refreshTv.setTextColor(BookConstant.getPageTextColor())

        //返回按钮颜色
        if (BookConstant.getPageColorIndex() == 3) {
            activity.mBinding.layoutTop.backIv.setImageResource(R.drawable.btn_back_white)
        } else {
            activity.mBinding.layoutTop.backIv.setImageResource(R.drawable.btn_back_black)
        }

        activity.mBinding.footerV.setBackgroundColor(BookConstant.getPageBackground())

        //头部状态栏
        val topParams = activity.mBinding.layoutTop.root.layoutParams
        topParams.height = ImmersionBar.getStatusBarHeight(activity) + activity.niceDp2px(61f)
        activity.mBinding.layoutTop.root.layoutParams = topParams
    }
}