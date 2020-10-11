package com.yuhang.novel.pirate.ui.main.fragment

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentStoreV2Binding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.ui.main.viewmodel.StoreViewModelV2
import com.yuhang.novel.pirate.ui.search.activity.SearchActivity
import com.yuhang.novel.pirate.ui.settings.activity.ProblemActivity
import com.yuhang.novel.pirate.ui.store.activity.BookCategoryActivity
import com.yuhang.novel.pirate.ui.store.fragment.LadyFragment
import com.yuhang.novel.pirate.ui.store.fragment.ManFragment
import com.yuhang.novel.pirate.ui.store.fragment.SexFragment
import com.yuhang.novel.pirate.utils.ScaleTransitionPagerTitleView
import com.yuhang.novel.pirate.utils.bindViewPager
import com.yuhang.novel.pirate.utils.dp
import com.yuhang.novel.pirate.utils.getColorCompat
import kotlinx.coroutines.launch
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 * 书城 v2
 */
class StoreFragmentV2 : BaseFragment<FragmentStoreV2Binding, StoreViewModelV2>(),
    ScaleTransitionPagerTitleView.onReselectListener {

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
        initUser()
        initMagicIndicator()
        initViewPager()
        onClick()

    }

    private fun initUser() {
        lifecycleScope.launch {
            val user = mViewModel.getUser() ?: return@launch
            mViewModel.isVip = user.isVip
        }

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
     * 初始化ViewPager
     */
    private fun initViewPager() {
        mBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return mViewModel.getFragments().size
            }

            override fun createFragment(position: Int): Fragment {
                return mViewModel.getFragments()[position]
            }
        }
    }


    private fun initMagicIndicator() {
        mBinding.magicIndicator.setBackgroundResource(R.color.window_background)
        val commonNavigator = CommonNavigator(requireContext())
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return mViewModel.getTitles().size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = ScaleTransitionPagerTitleView(context)
                lifecycleScope.launch {
                    simplePagerTitleView.text = mViewModel.getTitles()[index]
                    simplePagerTitleView.normalColor =
                        context.getColorCompat(R.color.secondary_text)
                    simplePagerTitleView.selectedColor =
                        context.getColorCompat(R.color.primary_text)
                    simplePagerTitleView.textSize = 19f
                    simplePagerTitleView.setOnClickListener {
                        mBinding.viewPager.setCurrentItem(
                            index,
                            false
                        )
                    }
                    simplePagerTitleView.listener = this@StoreFragmentV2
                }

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
        when (val fragment = mViewModel.getFragments()[mBinding.viewPager.currentItem]) {
            is ManFragment -> {
                if (fragment.isPreBinding()) {
                    onTopRecyclerView(
                        fragment.mBinding.refreshLayout,
                        fragment.mBinding.recyclerview,
                        -1
                    )
                }

            }

            is LadyFragment -> {
                if (fragment.isPreBinding()) {
                    onTopRecyclerView(
                        fragment.mBinding.refreshLayout,
                        fragment.mBinding.recyclerview,
                        -1
                    )

                }
            }
            is SexFragment -> {
                if (fragment.isPreBinding()) {
                    onTopRecyclerView(
                        fragment.mBinding.refreshLayout,
                        fragment.mBinding.recyclerview,
                        -1
                    )
                }
            }
        }

    }

}