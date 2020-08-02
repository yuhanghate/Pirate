package com.yuhang.novel.pirate.ui.store.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityKanshuRankingBinding
import com.yuhang.novel.pirate.ui.store.fragment.MonthRankingFragment
import com.yuhang.novel.pirate.ui.store.fragment.TotalRankingFragment
import com.yuhang.novel.pirate.ui.store.fragment.WeekRankingFragment
import com.yuhang.novel.pirate.ui.store.viewmodel.KanShuRankingViewModel
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
 * 看书神器 排行榜
 */
class KanShuRankingActivity :
    BaseSwipeBackActivity<ActivityKanshuRankingBinding, KanShuRankingViewModel>(),
    ScaleTransitionPagerTitleView.onReselectListener {

    companion object {

        const val TYPE_MAN = "man"//男生
        const val TYPE_LADY = "lady"//女生

        const val TYPE_WEEK = "week"//周榜
        const val TYPE_MONTH = "month"//月榜
        const val TYPE_TOTAL = "total"//总榜

        const val TYPE_HOT = "hot"//热门榜
        const val TYPE_OVER = "over"//完结榜
        const val TYPE_COMMEND = "commend"//推荐榜
        const val TYPE_COLLECT = "collect"//收藏榜
        const val TYPE_NEW = "new"//新书榜
        const val TYPE_VOTE = "vote"//评分榜

        const val GENDER = "gender"//性别
        const val TYPE = "type"//类型
        const val DATE = "date"//时间
        const val NAME = "name"//标题名称
        fun start(context: Activity, name: String, gender: String, type: String) {
            val intent = Intent(context, KanShuRankingActivity::class.java)
            intent.putExtra(GENDER, gender)
            intent.putExtra(TYPE, type)
            intent.putExtra(NAME, name)
            startIntent(context, intent)
        }
    }

    /**
     * 性别
     */
    private fun getGender() = intent.getStringExtra(GENDER)?:""

    //类型
    private fun getType() = intent.getStringExtra(TYPE)?:""

    private fun getName() = intent.getStringExtra(NAME)?:""

    override fun onLayoutId(): Int {
        return R.layout.activity_kanshu_ranking
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeLevel(SwipeBackLayout.EdgeLevel.MIN)
    }

    override fun initView() {
        super.initView()
        onClick()
        initMagicIndicator()
        initViewPager()
        mBinding.titleTv.text = getName()
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
    }

    /**
     * 初始化ViewPager
     */
    private fun initViewPager() {
        mBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return mViewModel.getFragments(getGender(), getType(), getName()).size
            }

            override fun createFragment(position: Int): Fragment {
                return mViewModel.getFragments(getGender(), getType(), getName())[position]
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
                simplePagerTitleView.setOnClickListener { mBinding.viewPager.setCurrentItem(index, false) }
                simplePagerTitleView.listener = this@KanShuRankingActivity
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
        when (val fragment = mViewModel.getFragments(getGender(), getType(), getName())[mBinding.viewPager.currentItem]) {
            is WeekRankingFragment -> onTopRecyclerView(
                fragment.mBinding.refreshLayout,
                fragment.mBinding.recyclerview,
                25
            )
            is MonthRankingFragment -> onTopRecyclerView(
                fragment.mBinding.refreshLayout,
                fragment.mBinding.recyclerview,
                25
            )
            is TotalRankingFragment -> onTopRecyclerView(
                fragment.mBinding.refreshLayout,
                fragment.mBinding.recyclerview,
                25
            )
        }
    }
}