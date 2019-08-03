package com.yuhang.novel.pirate.base

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.FragmentEvent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.R.anim
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<D : ViewDataBinding, VM : BaseViewModel> : RxFragment(), LifecycleProvider<FragmentEvent> {

  var mActivity: AppCompatActivity? = null

  lateinit var mBinding: D

  lateinit var mViewModel: VM

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return initContentView(container)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    if (!::mViewModel.isInitialized) {
      mViewModel = ViewModelProviders.of(this)
          .get(getViewModelClass())
      mViewModel.mFragment = this
      initView()
    }



  }

  private fun getViewModelClass(): Class<out VM> {
    return (this::class.java.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<out VM>
  }

  private fun initContentView(container: ViewGroup?): View {
    if (::mBinding.isInitialized) {
//      mBinding.notifyChange()
      return mBinding.root
    }



    container?.setBackgroundResource(android.R.color.transparent)

    val params = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    mBinding = DataBindingUtil.inflate(
        LayoutInflater.from(mActivity), onLayoutId(), container,
        false
    )

    mBinding.root.layoutParams = params

    return mBinding.root
  }

  /**
   * 赋值Activity
   */
  override fun onAttach(context: Context) {
    super.onAttach(context)
    mActivity = context as AppCompatActivity
  }

  /**
   * 回收Activity
   */
  override fun onDetach() {
    super.onDetach()
    mActivity = null
  }

  /**************************** 子类继承 start **************************/

  /**
   * 布局文件
   */
  @LayoutRes
  abstract fun onLayoutId(): Int

  /**
   * 初始化View
   */
  open fun initView() {

  }

  /**
   * 是否重写状态栏
   */
  open fun initStatusTool(context: BaseActivity<*, *>): Boolean {
//    StatusBarUtil.setColor(context, ContextCompat.getColor(context, onStatusColor()), DEFAULT_STATUS_BAR_ALPHA)
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//      mActivity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//      mActivity?.window?.decorView?.rootView?.setPadding(
//              0, -StatusBarUtil.getStatusBarHeight(mActivity), 0, 0
//      )
//    }
    return false
  }

  open fun isSelect(select : Boolean) {

  }

  /**
   * 沉浸式颜色
   */
  open fun onStatusColor(): Int {
    return R.color.primary_dark
  }

  /**************************** 子类继承 end **************************/

  /**************************** 子类使用 start **************************/

  /**
   * 替换Fragment
   */
  fun replaceFragment(
    id: Int,
    fragment: Fragment
  ) {
    mActivity?.supportFragmentManager?.beginTransaction()
        ?.replace(id, fragment)
        ?.commitNow()
  }


  /**
   * 添加所有fragment
   */
  fun addFragmentList(
    id: Int,
    fragmentList: List<Fragment>, showIndex:Int
  ) {
    val beginTransaction = activity?.supportFragmentManager?.beginTransaction()
    fragmentList.forEach { beginTransaction?.add(id, it) }
    (0 until fragmentList.size).forEachIndexed { index, _ ->
      if (index == showIndex) {
        beginTransaction?.show(fragmentList[index])
      } else {
        beginTransaction?.hide(fragmentList[index])
      }
    }
    beginTransaction?.commit()

  }

  /**
   * 显示Fragment
   */
  fun showFragment(
    fragmentList: List<Fragment>,
    showIndex: Int
  ) {
    val beginTransaction = activity?.supportFragmentManager?.beginTransaction()
    (0 until fragmentList.size).forEachIndexed { index, i ->
      if (index == showIndex) {
        beginTransaction?.show(fragmentList[index])
      } else {
        beginTransaction?.hide(fragmentList[index])
      }
    }
    beginTransaction?.commit()
  }

  /**
   * Fragment动画配置
   */
  fun getNavOptions(): NavOptions {
    return navOptions {
      anim {
        enter = anim.slide_in_right
        exit = anim.slide_out_left
        popEnter = anim.slide_in_left
        popExit = anim.slide_out_right
      }
    }
  }

  /**
   * 打开Popup框
   */
  fun showPopupMenu(view: View, @MenuRes layout:Int, itemListener: PopupMenu.OnMenuItemClickListener? = null, dismissListener: PopupMenu.OnDismissListener? = null): PopupMenu {
    // 这里的view代表popupMenu需要依附的view
    val popupMenu = PopupMenu(activity!!, view)
    // 获取布局文件
    popupMenu.menuInflater.inflate(layout, popupMenu.menu)
    popupMenu.gravity = Gravity.END
    // 通过上面这几行代码，就可以把控件显示出来了
    popupMenu.setOnMenuItemClickListener(itemListener)
    popupMenu.setOnDismissListener(dismissListener)

    popupMenu.show()

    return popupMenu

  }

  /**
   * 注册Eventbus
   */
  fun onCreateEventbus(any: Any) {
    if (!EventBus.getDefault().isRegistered(any)) {
      EventBus.getDefault()
          .register(any)
    }
  }

  /**
   * 回收Eventbus
   */
  fun onDestryEventbus(any: Any) {
    if (EventBus.getDefault().isRegistered(any)) {
      EventBus.getDefault()
          .unregister(any)
    }
  }

  /**
   * 获取状态栏高度
   */
  fun getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
      result = resources.getDimensionPixelSize(resourceId)
    }
    return result
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

  /**************************** 子类使用 end **************************/

}