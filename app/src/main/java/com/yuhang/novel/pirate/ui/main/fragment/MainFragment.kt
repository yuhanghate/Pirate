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
import com.trello.rxlifecycle2.android.FragmentEvent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.FragmentMainBinding
import com.yuhang.novel.pirate.eventbus.*
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.listener.OnClickItemLongListener
import com.yuhang.novel.pirate.listener.OnClickItemMoreListener
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.book.activity.ChapterListActivity
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.download.activity.BookDownloadActivity
import com.yuhang.novel.pirate.ui.main.dialog.MainDialog
import com.yuhang.novel.pirate.ui.main.viewmodel.MainViewModel
import com.yuhang.novel.pirate.ui.search.activity.SearchActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList


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

        //重要提示
        mBinding.btnPrompt.setOnClickListener { showTipdialog() }
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
    override fun onClickItemLongListener(view: View,position: Int) {
        val bookInfoKSEntity = mViewModel.adapter.getObj(position)

        MainDialog(this, mViewModel, bookInfoKSEntity).show()
    }


    /**
     * 右上角点击更多事件
     */
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            //下载管理
            R.id.download -> {
                BookDownloadActivity.start(mActivity!!)
            }

            //公告
            R.id.tip -> {
                showTipdialog()
            }

        }
        return true
    }

    /**
     * 重要提示
     */
    private fun showTipdialog() {
        MaterialDialog(activity!!).show {
            title(text = "提示")
            message(text = "1.搜索不到小说可以查看 我的-帮助与问题\n\n" +
                    "2.我们建议你注册帐号保证不丢失数据\n\n" +
                    "3.强烈建议为了更好的体验更新最新版本\n\n" +
                    "4.应用内不会出现广告请放心使用")
            positiveButton(text = "确定", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    p1.dismiss()
                }
            })
        }
    }


    /**
     * Item点击事件
     */
    override fun onClickItemListener(view: View, position: Int) {
        val obj = mViewModel.adapter.getObj(position)
        val isShowLabel = obj.isShowLabel
//        val isShowLabel  = true
        obj.isShowLabel = false

        mViewModel.queryCollection(obj.bookid)
            .compose(bindToLifecycle())
            .subscribe({ReadBookActivity.start(mActivity!!, it!!, isShowLabel)},{})



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
    fun netLocalData() {
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

                    initEmptyView()
                }, {
                    mBinding.loading.showContent()
                    mViewModel.adapter.setRefersh(arrayListOf())
                    mBinding.refreshLayout.finishRefresh(false)
                })
    }

    /**
     * 去搜索书
     */
    fun initEmptyView() {
        if (mViewModel.adapter.getList().isEmpty()) {
            mBinding.btnEmpty.visibility = View.VISIBLE
        } else {
            mBinding.btnEmpty.visibility = View.GONE
        }
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

                    //新标题的章节进行刷新
                    mViewModel.updateChapterToDB()
                        .compose(io_main())
                        .compose(bindUntilEvent(FragmentEvent.DESTROY))
                        .subscribe({},{})

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

    /**
     * 更新历史记录,根据阅读时间排序
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: UpdateReadHistoryEvent) {
        netLocalData()
    }
}