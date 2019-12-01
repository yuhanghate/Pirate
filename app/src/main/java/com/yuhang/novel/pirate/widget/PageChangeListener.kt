package com.yuhang.novel.pirate.widget

import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.SlidingTabLayout
import com.yuhang.novel.pirate.ui.main.viewmodel.StoreViewModelV2

class PageChangeListener(val tabLayout: SlidingTabLayout,val listener:TabLayoutListener,val vm:StoreViewModelV2?) : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        tabLayout.currentTab = position
        vm?.lastTabEntity = position
        listener.onTabSelect(position)
    }
}