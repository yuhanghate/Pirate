package com.yuhang.novel.pirate.ui.store.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.listener.OnTabSelectListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.base.ViewPagerAdapter
import com.yuhang.novel.pirate.databinding.ActivityBookCategoryBinding
import com.yuhang.novel.pirate.ui.store.viewmodel.BookCategoryViewModel

/**
 * 小说分类
 * 男生/女生/出版
 */
class BookCategoryActivity : BaseSwipeBackActivity<ActivityBookCategoryBinding, BookCategoryViewModel>(),
    OnTabSelectListener, ViewPager.OnPageChangeListener {

    companion object{
        fun start(context: Activity) {
            val intent = Intent(context, BookCategoryActivity::class.java)
            startIntent(context, intent)
        }
    }
    override fun onLayoutId(): Int {
        return R.layout.activity_book_category
    }

    override fun initView() {
        super.initView()
        onClick()
        initTabLayoutView()
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
    }

    /**
     * 初始化滑动栏
     */
    private fun initTabLayoutView() {
        val pagerAdapter =
            ViewPagerAdapter(supportFragmentManager, mViewModel.mTitles, mViewModel.getFragments())
        mBinding.tablayout.setOnTabSelectListener(this)
        mBinding.viewPager.addOnPageChangeListener(this)
        mBinding.viewPager.adapter = pagerAdapter
        mBinding.tablayout.setViewPager(mBinding.viewPager, mViewModel.mTitles.toTypedArray())
        onTabSelect(mViewModel.lastTabEntity)
        mBinding.tablayout.currentTab = 0
    }

    override fun onTabReselect(position: Int) {
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
                    .ofFloat(titleView, "", 1.5f, 1f)
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
                    .ofFloat(titleView, "", 1f, 1.5f)
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