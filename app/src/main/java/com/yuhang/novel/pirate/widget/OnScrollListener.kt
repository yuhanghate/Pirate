package com.yuhang.novel.pirate.widget

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.listener.OnRefreshLoadMoreListener


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class OnScrollListener(val adapter: BaseAdapter<*>, val listener: OnRefreshLoadMoreListener) :
        RecyclerView.OnScrollListener() {

    /**
     * 最后一个可见角标
     */
    private var mLastVisibleItemPosition = -1

    /**
     * 第一个可见角标
     */
    private var mFirstVisibleItemPosition = -1

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            mLastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            mFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        }
        if (newState === RecyclerView.SCROLL_STATE_IDLE) {
            listener.onPageIndexListener(mLastVisibleItemPosition)
            Logger.i("onScrollStateChanged firstVisibleItemPosition=$mFirstVisibleItemPosition  lastVisibleItemPosition=$mLastVisibleItemPosition")
            if (mLastVisibleItemPosition >= adapter.getList().size - 2) {
                //加载更多
                listener.onLoadMore(mFirstVisibleItemPosition, mLastVisibleItemPosition)
            }
            if (mFirstVisibleItemPosition <= 1) {
                //下拉刷新
                listener.onRefresh(mFirstVisibleItemPosition, mLastVisibleItemPosition)
            }
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
    }
}