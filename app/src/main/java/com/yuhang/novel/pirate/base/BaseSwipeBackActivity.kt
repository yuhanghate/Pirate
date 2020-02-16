package com.yuhang.novel.pirate.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import me.yokeyword.fragmentation.SwipeBackLayout
import me.yokeyword.fragmentation_swipeback.core.ISwipeBackActivity
import me.yokeyword.fragmentation_swipeback.core.SwipeBackActivityDelegate


abstract class BaseSwipeBackActivity<D : ViewDataBinding, VM : BaseViewModel> : BaseActivity<D, VM>(),
    ISwipeBackActivity {

    //    lateinit var mSwipeBackHelper: BGASwipeBackHelper
    var mDelegate: SwipeBackActivityDelegate = SwipeBackActivityDelegate(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 在 super.onCreate(savedInstanceState) 之前调用该方法
        super.onCreate(savedInstanceState)
        mDelegate.onCreate(savedInstanceState)
        swipeBackLayout.setEdgeOrientation(SwipeBackLayout.EDGE_LEFT)
        setEdgeLevel(SwipeBackLayout.EdgeLevel.MED)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDelegate.onPostCreate(savedInstanceState)
    }

    override fun getSwipeBackLayout(): SwipeBackLayout {
        return mDelegate.swipeBackLayout
    }

    /**
     * 是否可滑动
     * @param enable
     */
    override fun setSwipeBackEnable(enable: Boolean) {
        mDelegate.setSwipeBackEnable(enable)
    }

    override fun setEdgeLevel(edgeLevel: SwipeBackLayout.EdgeLevel) {
        mDelegate.setEdgeLevel(edgeLevel)
    }

    override fun setEdgeLevel(widthPixel: Int) {
        mDelegate.setEdgeLevel(widthPixel)
    }

    /**
     * 限制SwipeBack的条件,默认栈内Fragment数 <= 1时 , 优先滑动退出Activity , 而不是Fragment
     *
     * @return true: Activity优先滑动退出;  false: Fragment优先滑动退出
     */
    override fun swipeBackPriority(): Boolean {
        return mDelegate.swipeBackPriority()
    }



}