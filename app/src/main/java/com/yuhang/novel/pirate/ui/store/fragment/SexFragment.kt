package com.yuhang.novel.pirate.ui.store.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentSexBinding
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.book.activity.SexReadBookActivity
import com.yuhang.novel.pirate.ui.store.viewmodel.SexViewModel
import com.yuhang.novel.pirate.ui.user.activity.LoginActivity
import com.yuhang.novel.pirate.ui.user.dialog.LoginNoteDialog

/**
 * 书城 -> 小黄书
 */
class SexFragment : BaseFragment<FragmentSexBinding, SexViewModel>(), OnRefreshLoadMoreListener,
    OnClickItemListener {

    var PAGE_NUM = 20

    companion object {
        fun newInstance(): SexFragment {
            return SexFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_sex
    }

    override fun initView() {
        super.initView()
        initRecyclerView()
        initRefreshLayout()
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this)
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        mViewModel.adapter.setListener(this)
            .setDecorationSize(niceDp2px(5f))
            .setlayoutManager(LinearLayoutManager(mActivity))
            .setRecyclerView(mBinding.recyclerview)
    }

    override fun initData() {
        super.initData()
        mViewModel.queryStoreSex()
            .compose(bindToLifecycle())
            .subscribe {
                if (it.isEmpty()) {
                    mBinding.refreshLayout.autoRefresh()
                    return@subscribe
                }
                mViewModel.adapter.setRefersh(it)
            }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel.getStoreSex(PAGE_NUM, false)
            .subscribe({
                mBinding.refreshLayout.finishLoadMore()
                mViewModel.adapter.loadMore(it)
            }, { mBinding.refreshLayout.finishLoadMore() })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        PAGE_NUM = 20
        mViewModel.getStoreSex(PAGE_NUM, true)
            .subscribe({
                mBinding.refreshLayout.finishRefresh()
                mViewModel.adapter.setRefersh(it)
            }, {
                mBinding.refreshLayout.finishRefresh()
            })
    }

    /**
     * 阅读界面
     */
    override fun onClickItemListener(view: View, position: Int) {

        if (PirateApp.getInstance().getToken().isEmpty()) {
            LoginNoteDialog.start(requireContext())
            return
        }
        val obj = mViewModel.adapter.getObj(position)
        SexReadBookActivity.start(mActivity!!, obj.niceBooksResult())
    }

    /**
     * 去登陆
     */
    private fun showLoginDialog() {
        MaterialDialog(requireContext()).show {
            title(text = "实诚公告")
            message(text = "我们在新手期,增加了一段冗长,毫无意义的操作体验,强迫必须登陆!产品的智商也就这个水平了,请大家谅解...")
            negativeButton(text = "取消")
            positiveButton(text = "马上登陆", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    LoginActivity.start(requireActivity())
                }
            })
        }
    }
}