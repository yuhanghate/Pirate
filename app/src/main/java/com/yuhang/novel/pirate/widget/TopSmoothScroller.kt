package com.yuhang.novel.pirate.widget

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * 双击置顶
 */
class TopSmoothScroller(context: Context) : LinearSmoothScroller(context) {
    //标记是否需要二次滑动
    private var shouldMove = false
    //需要滑动到的item位置
    private var mPosition = 0

    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }

    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_START
    }

    fun smoothMoveToPosition(recyclerView: RecyclerView, position: Int) { // 获取RecyclerView的第一个可见位置
        val firstItem: Int = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0))
        // 获取RecyclerView的最后一个可见位置
        val lastItem: Int =
            recyclerView.getChildLayoutPosition(recyclerView.getChildAt(recyclerView.childCount - 1))

        if (lastItem > position) {
            recyclerView.scrollToPosition(position)
            recyclerView.smoothScrollToPosition(0)
        } else {
            recyclerView.smoothScrollToPosition(0)
        }
//        if (position < firstItem) { // 指定item在第一个可见item之前
//            recyclerView.smoothScrollToPosition(position)
//        } else if (position <= lastItem) { // 指定item在可见范围内，即在第一个可见item之后，最后一个可见item之前
//            val position = position - firstItem
//            if (position >= 0 && position < recyclerView.getChildCount()) { // 计算指定item的view到顶部的距离
//                val top: Int = recyclerView.getChildAt(position).getTop()
//                // 手动滑动到顶部
//                recyclerView.smoothScrollBy(0, top)
//            }
//        } else { // 指定item在最后一个可见item之后，用smoothScrollToPosition滑动到可见范围
//            // 再监听RecyclerView的滑动，对其进行二次滑动到顶部
//            recyclerView.smoothScrollToPosition(position)
//            mPosition = position
//            shouldMove = true
//        }
    }
}