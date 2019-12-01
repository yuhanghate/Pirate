package com.yuhang.novel.pirate.widget

import android.animation.ObjectAnimator
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.SlidingTabLayout
import com.flyco.tablayout.listener.OnTabSelectListener
import com.yuhang.novel.pirate.ui.main.viewmodel.StoreViewModelV2

class TabLayoutListener(val tabLayout: SlidingTabLayout, val viewPager:ViewPager,val vm:StoreViewModelV2?) : OnTabSelectListener {
    override fun onTabSelect(position: Int) {
        (0 until tabLayout.tabCount).forEach {
            val titleView = tabLayout.getTitleView(it)
            if (it == position) {
                val animator = ObjectAnimator
                    .ofFloat(titleView, "", 1.5f, 1f)
                    .setDuration(220)
                animator.start()
                animator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    titleView.scaleX = value
                    titleView.scaleY = value
                }
            }
        }

        (0 until tabLayout.tabCount).forEach {
            val titleView = tabLayout.getTitleView(it)
            if (it == position) {
                vm?.lastTabEntity = position
                val animator = ObjectAnimator
                    .ofFloat(titleView, "", 1f, 1.5f)
                    .setDuration(220)
                animator.start()
                animator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    titleView.scaleX = value
                    titleView.scaleY = value
                }
            }
        }

        if (viewPager.currentItem != position) {
            viewPager.currentItem = position
        }
    }

    override fun onTabReselect(position: Int) {
    }
}