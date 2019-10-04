package com.yuhang.novel.pirate.ui.book.activity

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityBookResouceBinding
import com.yuhang.novel.pirate.ui.book.viewmodel.BookResouceViewModel

/**
 * 书源列表
 */
class BookResouceActivity : BaseSwipeBackActivity<ActivityBookResouceBinding, BookResouceViewModel>(),
        OnRefreshLoadMoreListener {


    companion object{
        fun start(context: Activity) {
            val intent = Intent(context, BookResouceActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_book_resouce
    }

    override fun initView() {
        super.initView()

        onClick()
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this)
        mBinding.refreshLayout.autoRefresh()
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
//        mViewModel.adapter
//                .setDecorationMargin(20f)
//                .setListener(this)
//                .setlayoutManager(null)
//                .setDecorationColor(ContextCompat.getColor(mActivity!!, R.color.list_divider_color))
//                .setRecyclerView(mBinding.recyclerView)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}