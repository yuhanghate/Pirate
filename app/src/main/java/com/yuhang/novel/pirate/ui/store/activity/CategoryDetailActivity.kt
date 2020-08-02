package com.yuhang.novel.pirate.ui.store.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityCategoryDetailBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.ui.store.fragment.CategoryDetailFragment
import com.yuhang.novel.pirate.ui.store.viewmodel.CategoryDetailViewModel
import com.yuhang.novel.pirate.utils.ScaleTransitionPagerTitleView
import com.yuhang.novel.pirate.utils.bindViewPager
import com.yuhang.novel.pirate.utils.dp
import com.yuhang.novel.pirate.utils.getColorCompat
import me.yokeyword.fragmentation.SwipeBackLayout
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 * 分类详情页
 */
class CategoryDetailActivity :
    BaseSwipeBackActivity<ActivityCategoryDetailBinding, CategoryDetailViewModel>(),
    ScaleTransitionPagerTitleView.onReselectListener {


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

    private fun getGender() = intent.getStringExtra(GENDER) ?: ""
    private fun getMajor() = intent.getStringExtra(MAJOR) ?: ""

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
        initMagicIndicator()
        initViewPager()
        onClick()
    }

    private fun onClick() {
        mBinding.btnBack.clickWithTrigger { onBackPressedSupport() }
    }

    /**
     * 初始化ViewPager
     */
    private fun initViewPager() {
        mBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return mViewModel.getFragments(getGender(), getMajor()).size
            }

            override fun createFragment(position: Int): Fragment {
                return mViewModel.getFragments(getGender(), getMajor())[position]
            }
        }
    }


    private fun initMagicIndicator() {
        mBinding.magicIndicator.setBackgroundResource(R.color.window_background)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return mViewModel.mTitles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.text = mViewModel.mTitles[index]
                simplePagerTitleView.normalColor = context.getColorCompat(R.color.secondary_text)
                simplePagerTitleView.selectedColor = context.getColorCompat(R.color.primary_text)
                simplePagerTitleView.textSize = 20f
                simplePagerTitleView.setOnClickListener {
                    mBinding.viewPager.setCurrentItem(
                        index,
                        false
                    )
                }
                simplePagerTitleView.listener = this@CategoryDetailActivity
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight = 3f.dp
                indicator.lineWidth = 20f.dp
                indicator.roundRadius = 3f.dp
                indicator.setColors(context.getColorCompat(R.color.primary))
                return indicator
            }
        }
        mBinding.magicIndicator.navigator = commonNavigator
        mBinding.magicIndicator.bindViewPager(mBinding.viewPager)
    }

    /**
     * 重复选中一个TAB
     */
    override fun onReselectListener() {
        when (val fragment =
            mViewModel.getFragments(getGender(), getGender())[mBinding.viewPager.currentItem]) {
            is CategoryDetailFragment -> onTopRecyclerView(
                fragment.mBinding.refreshLayout,
                fragment.mBinding.recyclerview,
                25
            )
        }
    }

}