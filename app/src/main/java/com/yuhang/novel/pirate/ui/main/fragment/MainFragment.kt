package com.yuhang.novel.pirate.ui.main.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.FragmentMainBinding
import com.yuhang.novel.pirate.eventbus.LoginEvent
import com.yuhang.novel.pirate.eventbus.LogoutEvent
import com.yuhang.novel.pirate.eventbus.RemoveCollectionEvent
import com.yuhang.novel.pirate.eventbus.UpdateChapterEvent
import com.yuhang.novel.pirate.extension.findNavController
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.listener.OnClickItemLongListener
import com.yuhang.novel.pirate.listener.OnClickItemMoreListener
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.book.activity.ChapterListActivity
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.main.viewmodel.MainViewModel
import com.yuhang.novel.pirate.ui.search.activity.SearchActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 主页
 */
@SuppressLint("CheckResult")
class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(), OnRefreshLoadMoreListener,
        OnClickItemMoreListener, OnClickItemLongListener, OnClickItemListener, PopupMenu.OnMenuItemClickListener {

    var isLogin = false


    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun onStart() {
        super.onStart()
        //加入书架/标签都要用到.每次返回刷新
        netLocalData()
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        mViewModel.onPageStart("主页页面")
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        mViewModel.onPageEnd("主页页面")
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }


    override fun onDestroyView() {
        onDestryEventbus(this)
        super.onDestroyView()
    }

    override fun initView() {
        super.initView()
        onCreateEventbus(this)
        initRecyclerView()
        initRefreshLayout()
        onClick()
        netData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {

            if (isLogin) {
                isLogin = false
                netLocalData()
            }

        }
    }


    /**
     * 刷新
     */
    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.loading.showContent()
        Log.i("MainFragment", "${this.hashCode()}")
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this)

    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        mViewModel.adapter
                .setDecorationMargin(20f)
                .setListener(this)
                .setlayoutManager(null)
                .setDecorationColor(ContextCompat.getColor(mActivity!!, R.color.list_divider_color))
                .setRecyclerView(mBinding.recyclerView)
    }


    /**
     * 点击事件
     */
    private fun onClick() {
        mBinding.mainBtn.setOnClickListener {
            findNavController().navigate(
                    R.id.storeFragment, null, getNavOptions()
            )
        }
        mBinding.btnMore.setOnClickListener { showPopupMenu(mBinding.btnMore, R.menu.menu_main, itemListener = this) }
        mBinding.btnSearch.setOnClickListener {
            mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_MAIN_CLICK_SEARCH, "主页 -> 点击搜索")
            SearchActivity.start(mActivity!!)
        }

        //空白页面
        mBinding.btnEmpty.setOnClickListener {
            mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_MAIN_CLICK_SEARCH, "主页 -> 点击搜索")
            SearchActivity.start(mActivity!!)
        }
    }

    /**
     * Item点击更多
     */
    override fun onClickItemMoreListener(view: View, position: Int) {
        showPopupMenu(view, R.menu.menu_main)
    }

    /**
     * Item长按事件
     */
    override fun onClickItemLongListener(
            view: View,
            position: Int
    ) {
        val bookInfoKSEntity = mViewModel.adapter.getObj(position)
        val myItems = listOf("书籍详情", "目录书摘", "删除", "置顶")
        MaterialDialog(activity!!).show {
            listItems(items = myItems, selection = { dialog, index, text ->
                when (text) {
                    "书籍详情" -> {
                        mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_MAIN_ITEM_LONG_CLICK_DETAILS, "主页 -> 书箱详情")
                        BookDetailsActivity.start(mActivity!!, bookid = bookInfoKSEntity.bookid)
                    }
                    "目录书摘" -> {
                        mViewModel.onUMEvent(
                                mActivity!!,
                                UMConstant.TYPE_MAIN_ITEM_LONG_CLICK_DIR_CHANPTER,
                                "主页 -> 目录书箱"
                        )
                        ChapterListActivity.start(mActivity!!, bookInfoKSEntity.bookid, bookInfoKSEntity.lastChapterId)
                    }
                    "删除" -> {
                        mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_MAIN_ITEM_LONG_CLICK_DELETE, "主页 -> 从书架删除收藏")
                        mViewModel.deleteCollection(bookid = bookInfoKSEntity.bookid)
                        mViewModel.adapter.getList().remove(bookInfoKSEntity)
                        mViewModel.adapter.notifyDataSetChanged()
                    }
                    "置顶" -> {
                        mViewModel.onUMEvent(mActivity!!, UMConstant.TYPE_MAIN_ITEM_LONG_CLICK_TOP, "主页 -> 书架置顶")
                        mViewModel.updateStickTime(bookInfoKSEntity.bookid)
                        netLocalData()
                    }
                }
            })
        }
    }

    /**
     * 右上角点击更多事件
     */
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.delete -> {
            }
            R.id.share -> {
                showShareDialog()
            }

        }
        return true
    }

    /**
     * 分享Dialog
     */
    private fun showShareDialog() {
        MaterialDialog(mActivity!!).show {
            title(text = "温馨提示")
            message(text = "链接复制成功,请分享给您的好友.发送给好友的复制内容是:\n\r \n\r我正在用随便看书APP看免费百万本小说。下载地址 https://fir.im/a9u7")
            negativeButton(text = "取消")
            positiveButton(text = "分享", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    //获取剪贴板管理器：
                    val cm = mActivity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                    // 创建普通字符型ClipData
                    val mClipData = ClipData.newPlainText("Label", "我正在用随便看书APP看免费百万本小说。下载地址 https://fir.im/a9u7")
                    // 将ClipData内容放到系统剪贴板里。
                    cm!!.setPrimaryClip(mClipData)
                    niceToast("复制成功,可以分享给朋友了")
                }
            })
        }
    }

    /**
     * Item点击事件
     */
    override fun onClickItemListener(view: View, position: Int) {
        val obj = mViewModel.adapter.getObj(position)
        obj.isShowLabel = false
        ReadBookActivity.start(mActivity!!, obj.bookid)

        //延迟1秒刷新.体验更好
        Handler().postDelayed({ mViewModel.adapter.notifyDataSetChanged() }, 1000)

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        Handler().postDelayed({
            mBinding.refreshLayout.finishLoadMore()
        }, 1500)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        netServiceData()
    }

    /**
     * 加载数据
     * 第一次安装app,加载服务器数据.否则加载本地数据
     */
    private fun netData() {
        if (PreferenceUtil.getBoolean(BookConstant.IS_FIRST_INSTALL, true)) {
            netServiceData()
        } else {
            netLocalData()
        }
    }

    /**
     * 从本地数据库加载书架数据
     */
    private fun netLocalData() {
        mViewModel.getBookInfoListLocal()
                .compose(bindToLifecycle())
                .subscribe({
                    mBinding.loading.showContent()
                    mBinding.refreshLayout.finishRefresh()
                    mBinding.refreshLayout.setEnableLoadMore(false)

                    val list = arrayListOf<BookInfoKSEntity>()
                    it.filterNotNull().forEach { list.add(it) }
                    mViewModel.adapter.getList().clear()
                    mViewModel.adapter.setRefersh(list)
                    if (mViewModel.adapter.getList().isEmpty()) {
                        mBinding.btnEmpty.visibility = View.VISIBLE
                    } else {
                        mBinding.btnEmpty.visibility = View.GONE
                    }

                }, {
                    mBinding.loading.showContent()
                    mViewModel.adapter.setRefersh(arrayListOf())
                    mBinding.refreshLayout.finishRefresh(false)
                })
    }

    /**
     * 从服务器刷新数据
     */

    private fun netServiceData() {
        val list = arrayListOf<BookInfoKSEntity>()
        mViewModel.getBookDetailsList()
                .compose(bindToLifecycle())
                .subscribe({
                    it?.let { bookindo ->
                        list.forEach {
                            if (it.bookid == bookindo.bookid) {
                                return@subscribe
                            }
                        }
                        list.add(it)
                    }
                }, {
                    mBinding.loading.showContent()
                    mViewModel.adapter.setRefersh(list)
                    mBinding.refreshLayout.finishRefresh(false)
                }, {

                    netLocalData()
                    PreferenceUtil.commitBoolean(BookConstant.IS_FIRST_INSTALL, false)
                })
    }

    /**
     * 登出回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: LogoutEvent) {
        netLocalData()
//        isLogin = true
    }

    /**
     * 登陆成功回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: LoginEvent) {
//        isLogin = true
        netLocalData()
    }

    /**
     * 书箱加入收藏回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: UpdateChapterEvent) {
        netLocalData()
    }

    /**
     * 书箱移除收藏回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: RemoveCollectionEvent) {
        netLocalData()
    }
}