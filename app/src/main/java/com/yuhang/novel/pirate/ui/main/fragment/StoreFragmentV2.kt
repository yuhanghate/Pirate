package com.yuhang.novel.pirate.ui.main.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.listener.OnTabSelectListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.base.ViewPagerAdapter
import com.yuhang.novel.pirate.databinding.FragmentStoreV2Binding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.ui.main.viewmodel.StoreViewModelV2
import com.yuhang.novel.pirate.ui.search.activity.SearchActivity
import com.yuhang.novel.pirate.ui.settings.activity.ProblemActivity
import com.yuhang.novel.pirate.ui.store.activity.BookCategoryActivity
import com.yuhang.novel.pirate.ui.store.fragment.LadyFragment
import com.yuhang.novel.pirate.ui.store.fragment.ManFragment
import com.yuhang.novel.pirate.ui.store.fragment.SexFragment

/**
 * 书城 v2
 */
class StoreFragmentV2 : BaseFragment<FragmentStoreV2Binding, StoreViewModelV2>(),
    OnTabSelectListener, ViewPager.OnPageChangeListener {

    companion object {
        fun newInstance(): StoreFragmentV2 {
            return StoreFragmentV2()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_store_v2
    }

    override fun initView() {
        super.initView()
        initTabLayoutView()
        onClick()
    }

    private fun onClick() {
        //搜索
        mBinding.searchLl.clickWithTrigger { SearchActivity.start(mActivity!!) }
        //帮助与问题
        mBinding.helpIv.clickWithTrigger { ProblemActivity.start(mActivity!!) }
        //分类
        mBinding.categoryTv.clickWithTrigger { BookCategoryActivity.start(mActivity!!) }
    }

    /**
     * 初始化滑动栏
     */
    private fun initTabLayoutView() {
        val pagerAdapter =
            ViewPagerAdapter(childFragmentManager, mViewModel.getTitles(), mViewModel.getFragments())
        mBinding.tablayout.setOnTabSelectListener(this)
        mBinding.viewPager.addOnPageChangeListener(this)
        mBinding.viewPager.adapter = pagerAdapter
        mBinding.tablayout.setViewPager(mBinding.viewPager, mViewModel.getTitles().toTypedArray())
        onTabSelect(mViewModel.lastTabEntity)
        mBinding.tablayout.currentTab = 0
    }


    override fun onTabReselect(position: Int) {
        when (val fragment = mViewModel.getFragments()[position]) {
            is ManFragment -> onTopRecyclerView(
                fragment.mBinding.refreshLayout,
                fragment.mBinding.recyclerview,
                -1
            )
            is LadyFragment -> onTopRecyclerView(
                fragment.mBinding.refreshLayout,
                fragment.mBinding.recyclerview,
                -1
            )
            is SexFragment -> onTopRecyclerView(fragment.mBinding.refreshLayout,
                fragment.mBinding.recyclerview,
                -1)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        mBinding.tablayout.currentTab = position
        onTabSelect(position)
    }

    @SuppressLint("ObjectAnimatorBinding")
    override fun onTabSelect(position: Int) {
        (0 until mBinding.tablayout.tabCount).forEach {
            val titleView = mBinding.tablayout.getTitleView(it)
            if (it == mViewModel.lastTabEntity) {
                val animator = ObjectAnimator
                    .ofFloat(titleView, "", 1.2f, 1f)
                    .setDuration(240)
                animator.start()
                animator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    titleView.scaleX = value
                    titleView.scaleY = value
                }
            }
        }

        (0 until mBinding.tablayout.tabCount).forEach {
            val titleView = mBinding.tablayout.getTitleView(it)
            if (it == position) {
                mViewModel.lastTabEntity = position
                val animator = ObjectAnimator
                    .ofFloat(titleView, "", 1f, 1.2f)
                    .setDuration(240)
                animator.start()
                animator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    titleView.scaleX = value
                    titleView.scaleY = value
                }
            }
        }

        if (mBinding.viewPager.currentItem != position) {
            mBinding.viewPager.currentItem = position
        }
    }

}