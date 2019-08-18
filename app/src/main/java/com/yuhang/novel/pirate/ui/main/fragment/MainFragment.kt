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
import com.yuhang.novel.pirate.databinding.FragmentMainBinding
import com.yuhang.novel.pirate.eventbus.LoginEvent
import com.yuhang.novel.pirate.eventbus.LogoutEvent
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
        mBinding.btnSearch.setOnClickListener { SearchActivity.start(mActivity!!) }
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
                    "书籍详情" -> BookDetailsActivity.start(mActivity!!, bookid = bookInfoKSEntity.bookid)
                    "目录书摘" -> {
                        ChapterListActivity.start(mActivity!!, bookInfoKSEntity.bookid, bookInfoKSEntity.lastChapterId)
                    }
                    "删除" -> {
                        mViewModel.deleteCollection(bookid = bookInfoKSEntity.bookid)
                        mViewModel.adapter.getList().remove(bookInfoKSEntity)
                        mViewModel.adapter.notifyDataSetChanged()
                    }
                    "置顶" -> {
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
            message(text = "链接复制成功,请分享给您的好友.发送给好友的复制内容是:\n\r \n\r我正在用随便看看APP看免费百万本小说。下载地址 https://fir.im/a9u7")
            negativeButton(text = "取消")
            positiveButton(text = "分享", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    //获取剪贴板管理器：
                    val cm = mActivity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                    // 创建普通字符型ClipData
                    val mClipData = ClipData.newPlainText("Label", "我正在用随便看看APP看免费百万本小说。下载地址 https://fir.im/a9u7")
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
        ReadBookActivity.start(mActivity!!, obj.bookid)
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
                .subscribe({
                    mBinding.loading.showContent()
                    mBinding.refreshLayout.finishRefresh()
                    mBinding.refreshLayout.setEnableLoadMore(false)

                    val list = arrayListOf<BookInfoKSEntity>()
                    it.filterNotNull().forEach { list.add(it) }
                    mViewModel.adapter.setRefersh(list)

//                showNewUpdateLabel()
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
                    it?.let { list.add(it) }
                }, {
                    mBinding.loading.showContent()
                    mViewModel.adapter.setRefersh(list)
                    mBinding.refreshLayout.finishRefresh(false)
                }, {

                    netLocalData()
                    PreferenceUtil.commitBoolean(BookConstant.IS_FIRST_INSTALL, false)
                })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: LogoutEvent) {
//        netLocalData()
    }

    /**
     * 登陆成功回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: LoginEvent) {
        isLogin = true
    }

}