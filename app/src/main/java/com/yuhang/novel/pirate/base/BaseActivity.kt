package com.yuhang.novel.pirate.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.utils.ThemeHelper
import com.yuhang.novel.pirate.viewbinding.ActivityViewBinder
import com.yuhang.novel.pirate.widget.TopSmoothScroller
import kotlinx.coroutines.CoroutineExceptionHandler
import me.yokeyword.fragmentation.SupportActivity
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType


abstract class BaseActivity<D : ViewBinding, VM : BaseViewModel> : SupportActivity() {
    open lateinit var mBinding: D
    lateinit var mViewModel: VM

    lateinit var mProgressbar: ProgressDialog

    val catch = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }

    companion object {

        /**
         * 打开Activity,自定义动画
         */
        fun startIntent(activity: Context, intent: Intent) {
            activity.startActivity(intent)
            if (activity is Activity) {
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            //26版本部分机型会出现Only fullscreen activities can request orientation 异常
            // 进行兼容性解决处理.google官方bug
            fixOrientation()
        }
        ThemeHelper.applyTheme(PreferenceUtil.getString("themePref", ThemeHelper.LIGHT_MODE))
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.navigationBarColor = ContextCompat.getColor(this, R.color.navigation_bar_color)
        Logger.i(" oncreate ->${this}")
        initSqlScoutServer()
        initLayoutInflater()
        initViewModel()
        initStatusTool()
        initView()
        initData()
    }

    /**
     * 初始化数据库
     */
    private fun initSqlScoutServer() {
//        try {
//            SqlScoutServer.create(this, packageName)
//        } catch (e: IOException) {
//
//        }

    }

    /**
     * 设置横竖屏
     */
    private fun fixOrientation(): Boolean {
        try {
            val field = Activity::class.java.getDeclaredField("mActivityInfo");
            field.isAccessible = true
            val o = field.get(this) as ActivityInfo
            o.screenOrientation = -1
            field.setAccessible(false)
            return true;
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    @SuppressLint("PrivateApi")
    private fun isTranslucentOrFloating(): Boolean {
        var isTranslucentOrFloating = false
        try {
            val styleableRes =
                Class.forName("com.android.internal.R\$styleable").getField("Window").get(null) as IntArray
            val ta = obtainStyledAttributes(styleableRes)
            val m = ActivityInfo::class.java.getMethod(
                "isTranslucentOrFloating",
                TypedArray::class.java
            )
            m.isAccessible = true
            isTranslucentOrFloating = m.invoke(null, ta) as Boolean
            m.isAccessible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isTranslucentOrFloating
    }

    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            return
        }
        super.setRequestedOrientation(requestedOrientation);
    }


    /**
     * 初始化ViewModel
     */
    private fun initViewModel() {
        mViewModel = ViewModelProvider(this).get(getViewModelClass())
        mViewModel.mActivity = this
    }


    /**
     * 异步加载Activity内容
     */
    private fun initLayoutInflater() {
        if (::mBinding.isInitialized) {
            setContentView(mBinding.root)
        }

        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[0] as Class<D>
            val activityViewBinder = ActivityViewBinder(clazz)
            mBinding = activityViewBinder.bind(layoutInflater.inflate(onLayoutId(), null, false))
            setContentView(mBinding.root)
        } else {
            onViewBinding()
            setContentView(mBinding.root)
        }
    }

    /**
     * 手动加载ViewBinding
     */
    open fun  onViewBinding() {

    }

    /**
     * 设置沉浸式
     */
    open fun initStatusTool() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .fitsSystemWindows(true)
            .statusBarColor(onStatusColor())     //状态栏颜色，不写默认透明色
            .navigationBarColor(R.color.navigation_bar_color) //导航栏颜色，不写默认黑色
            .init()

//        StatusBarUtil.setTranslucent(this)
//        StatusBarUtil.setColor(this, ContextCompat.getColor(this, onStatusColor()), 90)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window?.decorView?.rootView?.setPadding(
//                    0, -StatusBarUtil.getStatusBarHeight(this), 0, 0
//            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    /**
     * 获取ViewModel类型
     */
    open fun getViewModelClass(): Class<out VM> {
        return (this::class.java.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<out VM>
    }

    /**
     * 沉浸式颜色
     */
    open fun onStatusColor(): Int {
        return R.color.actionbar_color2
    }


    /**************************** 子类继承 start **************************/


    /**
     * 获取布局文件
     */
    @LayoutRes
    abstract fun onLayoutId(): Int

    /**
     * 初始化View
     */
    open fun initView() {

        Logger.i("baseactivity -> initview")
    }

    open fun initData() {

    }


    /**************************** 子类继承 end **************************/


    /**************************** 子类调用 start **************************/


    /**
     * 打开进度等待条
     */
    fun showProgressbar(message: String = "加载中", cancel: Boolean = false) {
        if (!::mProgressbar.isInitialized) {
            mProgressbar = ProgressDialog(this)
        }
        if (!mProgressbar.isShowing) {
            mProgressbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressbar.setMessage(message)
            mProgressbar.setCancelable(cancel)
            mProgressbar.show()
        }
    }

    /**
     * 关闭等待条
     */
    fun closeProgressbar() {
        if (!::mProgressbar.isInitialized) {
            mProgressbar = ProgressDialog(this)
        }

        if (mProgressbar.isShowing) {
            mProgressbar.dismiss()
        }
    }

    /**
     * 是否显示进度条
     */
    fun hasProgressbar(): Boolean {
        return mProgressbar.isShowing
    }


    /**
     * 替换Fragment
     */
    fun replaceFragment(id: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(id, fragment)
            .commitNow()
    }

    /**
     * 添加所有fragment
     */
    fun addFragmentList(
        id: Int,
        fragmentList: List<Fragment>, showIndex: Int
    ) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        fragmentList.forEach { beginTransaction.add(id, it) }
        (0 until fragmentList.size).forEachIndexed { index, i ->
            if (index == showIndex) {
                beginTransaction.show(fragmentList[index])
            } else {
                beginTransaction.hide(fragmentList[index])
            }
        }
        beginTransaction.commit()

    }

    /**
     * 显示Fragment
     */
    fun showFragment(
        fragmentList: List<Fragment>,
        showIndex: Int
    ) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        (0 until fragmentList.size).forEachIndexed { index, i ->
            if (index == showIndex) {
                beginTransaction.show(fragmentList[index])
            } else {
                beginTransaction.hide(fragmentList[index])
            }
        }
        beginTransaction.commit()
    }


    /**
     * 创建Eventbus
     */
    fun onCreateEventbus(any: Any) {
        if (!EventBus.getDefault().isRegistered(any)) {
            EventBus.getDefault().register(any)
        }
    }

    /**
     * 销毁Eventbus
     */
    fun onDestryEventbus(any: Any) {
        if (EventBus.getDefault().isRegistered(any)) {
            EventBus.getDefault().unregister(any)
        }
    }


    /**
     * 初始化RecyclerView列表数据
     */
    open fun initRecyclerView() {

    }

    /**
     * 初始化下拉刷新
     */
    open fun initRefreshLayout() {
    }

    override fun onBackPressedSupport() {
//        BGAKeyboardUtil.closeKeyboard(this)
        finish()
        this.overridePendingTransition(
            R.anim.activity_in_enter,
            R.anim.activity_in_exit
        )
    }

    /**
     * RecyclerView
     */
    fun addOnScrollListener(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(this@BaseActivity).resumeRequests()
                } else {
                    Glide.with(this@BaseActivity).pauseRequests()
                }
            }
        })

    }

    /**
     * 双击置顶 + 刷新
     */
    fun onTopRecyclerView(
        refreshLayout: SmartRefreshLayout,
        recyclerView: RecyclerView,
        position: Int
    ) {

        val manager = recyclerView.layoutManager as? VirtualLayoutManager
        val firstItem = manager?.findFirstVisibleItemPosition()
        //刷新
        if (firstItem == 0) {
            refreshLayout.autoRefresh()
            return
        }

        //置顶
        onTopRecyclerView(recyclerView, position)
    }

    /**
     * 双击置顶
     */
    fun onTopRecyclerView(recyclerView: RecyclerView, position: Int) {

        //置顶
        val scroller = TopSmoothScroller(this)
        scroller.smoothMoveToPosition(recyclerView, position)
    }

    /**************************** 子类调用 end **************************/


}