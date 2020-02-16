package com.yuhang.novel.pirate.ui.store.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.listener.OnTabSelectListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.base.ViewPagerAdapter
import com.yuhang.novel.pirate.databinding.ActivityCategoryDetailBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.ui.store.fragment.CategoryDetailFragment
import com.yuhang.novel.pirate.ui.store.viewmodel.CategoryDetailViewModel
import me.yokeyword.fragmentation.SwipeBackLayout

/**
 * 分类详情页
 */
class CategoryDetailActivity :
    BaseSwipeBackActivity<ActivityCategoryDetailBinding, CategoryDetailViewModel>(),
    OnTabSelectListener, ViewPager.OnPageChangeListener {


    companion object {
        const val GENDER = "gender"
        const val MAJOR = "major"
        fun start(context: Activity, gender: String, major: String) {
            val intent = Intent(context, CategoryDetailActivity::class.java)
            intent.putExtra(GENDER, gender)
            intent.putExtra(MAJOR, major)
            startIntent(context, intent)
        }
    }

    private fun getGender() = intent.getStringExtra(GENDER)
    private fun getMajor() = intent.getStringExtra(MAJOR)

    override fun onLayoutId(): Int {
        return R.layout.activity_category_detail
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeLevel(SwipeBackLayout.EdgeLevel.MIN)
    }

    override fun initView() {
        super.initView()
        mBinding.titleTv.text = getMajor()
        onClick()
        initTabLayoutView()
    }

    private fun onClick() {
        mBinding.btnBack.clickWithTrigger { onBackPressedSupport() }
    }

    /**
     * 初始化滑动栏
     */
    private fun initTabLayoutView() {
        val pagerAdapter =
            ViewPagerAdapter(
                supportFragmentManager,
                mViewModel.mTitles,
                mViewModel.getFragments(getGender(), getMajor())
            )
        mBinding.tablayout.setOnTabSelectListener(this)
        mBinding.viewPager.addOnPageChangeListener(this)
        mBinding.viewPager.adapter = pagerAdapter
        mBinding.tablayout.setViewPager(mBinding.viewPager, mViewModel.mTitles.toTypedArray())
        onTabSelect(mViewModel.lastTabEntity)
        mBinding.tablayout.currentTab = 0
    }

    override fun onTabReselect(position: Int) {
        val fragment = mViewModel.getFragments(getGender(), getGender())[position]
        when (fragment) {
            is CategoryDetailFragment -> onTopRecyclerView(
                fragment.mBinding.refreshLayout,
                fragment.mBinding.recyclerview,
                25
            )
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