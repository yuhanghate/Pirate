package com.yuhang.novel.pirate.widget

import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import com.google.android.material.appbar.AppBarLayout
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.book.viewmodel.BookDetailsViewModel
import com.yuhang.novel.pirate.widget.expandablelayout.Utils
import kotlin.math.abs

class BookDetailsHeaderView(val activity:BookDetailsActivity, val vm:BookDetailsViewModel) {

    enum class TitleEvent {

        ENTER_START, //进入动画开始
        ENTER_END,  //进入动画结束
        EXIT_START, //退出动画开始
        EXIT_END, //退出动画结束
        DEFAULT //默认
    }

    /**
     * 是否显示标题动画
     */
    private var animationEvent = TitleEvent.DEFAULT

    fun initHederView(appBarLayout: AppBarLayout, verticalOffset: Int) {
        //垂直方向偏移量
        val offset = abs(verticalOffset).toFloat();
        //最大偏移距离
        val scrollRange = appBarLayout.totalScrollRange.toFloat()

        val alpha4 = (scrollRange - offset) / scrollRange
        Logger.i("offset = $offset scrollRange = $scrollRange  alpha4 = $alpha4")

        if (scrollRange - offset == 0f) {

            //滑入标题动画
            animationEvent = TitleEvent.ENTER_START
            activity.mBinding.includeToolbarOpen.root.visibility = View.INVISIBLE
            activity.mBinding.includeToolbarClose.root.visibility = View.VISIBLE

            activity.mBinding.includeToolbarClose.titleCloseTv.visibility = View.VISIBLE
            activity.mBinding.includeToolbarClose.backCloseIv.visibility = View.VISIBLE

            val animationIn = AnimationUtils.loadAnimation(activity, R.anim.slide_book_details_in)
            animationIn.interpolator =
                Utils.createInterpolator(Utils.LINEAR_OUT_SLOW_IN_INTERPOLATOR) as Interpolator
            activity.mBinding.includeToolbarClose.titleCloseTv.startAnimation(animationIn)
            Handler().postDelayed({
                activity.mBinding.includeToolbarClose.titleCloseTv.visibility = View.VISIBLE
                animationEvent = TitleEvent.ENTER_END
            }, 500)

        } else if (animationEvent == TitleEvent.ENTER_END) {

            //标题滑出动画
            animationEvent = TitleEvent.EXIT_START
            activity.mBinding.includeToolbarClose.root.visibility = View.VISIBLE
            val animationOut = AnimationUtils.loadAnimation(activity, R.anim.slide_book_details_out)
            animationOut.interpolator =
                Utils.createInterpolator(Utils.LINEAR_OUT_SLOW_IN_INTERPOLATOR) as Interpolator
            animationOut.fillAfter = true
            activity.mBinding.includeToolbarClose.titleCloseTv.startAnimation(animationOut)
            Handler().postDelayed({
                //                mBinding.includeToolbarClose.titleCloseTv.visibility = View.INVISIBLE
                animationEvent = TitleEvent.EXIT_END
            }, 600)
        } else if (animationEvent == TitleEvent.DEFAULT || animationEvent == TitleEvent.EXIT_END) {
            animationEvent = TitleEvent.DEFAULT
            activity.mBinding.includeToolbarClose.root.visibility = View.VISIBLE
        }

        Logger.t("offset").i("alpha=$alpha4")

        activity.mBinding.includeToolbarClose.backCloseIv.alpha = 1 - alpha4
        activity.mBinding.includeToolbarOpen.root.alpha = alpha4
        activity.mBinding.statusBarV.alpha = 1 - alpha4
        activity.mBinding.includeToolbarClose.root.alpha = 1 - alpha4

        if (offset == 0f) {
            //打开
            activity.mBinding.includeToolbarClose.root.visibility = View.INVISIBLE
            activity.mBinding.includeToolbarOpen.root.visibility = View.VISIBLE
            activity.mBinding.includeToolbarOpen.root.alpha = 1f
            activity.mBinding.statusBarV.visibility = View.VISIBLE
        }
    }
}