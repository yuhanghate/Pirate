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
//        initSwipeBackFinish()
        super.onCreate(savedInstanceState)
        mDelegate.onCreate(savedInstanceState)
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

    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private fun initSwipeBackFinish() {
//        mSwipeBackHelper = BGASwipeBackHelper(this, this)
//
//        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
//        // 下面几项可以不配置，这里只是为了讲述接口用法。
//
//        // 设置滑动返回是否可用。默认值为 true
//        mSwipeBackHelper.setSwipeBackEnable(true)
//        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
//        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(false)
//        // 设置是否是微信滑动返回样式。默认值为 true
//        mSwipeBackHelper.setIsWeChatStyle(true)
//        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
//        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow)
////    mSwipeBackHelper.setShadowResId(R.color.white_transparent)
//        // 设置是否显示滑动返回的阴影效果。默认值为 true
//        mSwipeBackHelper.setIsNeedShowShadow(true)
//        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
//        mSwipeBackHelper.setIsShadowAlphaGradient(true)
//        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
//        mSwipeBackHelper.setSwipeBackThreshold(0.3f)
//        // 设置底部导航条是否悬浮在内容上，默认值为 false
//        mSwipeBackHelper.setIsNavigationBarOverlap(false)
    }

    /**
     * 是否支持滑动返回
     *
     * @return
     */
//    override fun isSupportSwipeBack(): Boolean {
//        return true
//    }
//
//    /**
//     * 正在滑动返回
//     *
//     * @param slideOffset 从 0 到 1
//     */
//    override fun onSwipeBackLayoutSlide(slideOffset: Float) {}
//
//
//    /**
//     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
//     */
//    override fun onSwipeBackLayoutCancel() {
//
//    }
//
//    /**
//     * 滑动返回执行完毕，销毁当前 Activity
//     */
//    override fun onSwipeBackLayoutExecuted() {
////        mSwipeBackHelper.swipeBackward()
//    }

//    override fun onBackPressedSupport() {
//        // 正在滑动返回的时候取消返回按钮事件
////        if (mSwipeBackHelper.isSliding) return
////        mSwipeBackHelper.backward()
//
//        onBackPressed()
//    }


}