package com.yuhang.novel.pirate.ui.store.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.listener.OnTabSelectListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.base.ViewPagerAdapter
import com.yuhang.novel.pirate.databinding.ActivityKanshuRankingBinding
import com.yuhang.novel.pirate.ui.store.fragment.*
import com.yuhang.novel.pirate.ui.store.viewmodel.KanShuRankingViewModel
import me.yokeyword.fragmentation.SwipeBackLayout

/**
 * 看书神器 排行榜
 */
class KanShuRankingActivity :
    BaseSwipeBackActivity<ActivityKanshuRankingBinding, KanShuRankingViewModel>(),
    OnTabSelectListener, ViewPager.OnPageChangeListener {

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
    private fun getGender() = intent.getStringExtra(GENDER)

    //类型
    private fun getType() = intent.getStringExtra(TYPE)

    private fun getName() = intent.getStringExtra(NAME)

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
        initTabLayoutView()
        mBinding.titleTv.text = getName()
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
    }

    /**
     * 初始化滑动栏
     */
    private fun initTabLayoutView() {
        val pagerAdapter =
            ViewPagerAdapter(supportFragmentManager, mViewModel.mTitles, mViewModel.getFragments(getGender(), getType(), getName()))
        mBinding.tablayout.setOnTabSelectListener(this)
        mBinding.viewPager.addOnPageChangeListener(this)
        mBinding.viewPager.adapter = pagerAdapter
        mBinding.tablayout.setViewPager(mBinding.viewPager, mViewModel.mTitles.toTypedArray())
        onTabSelect(mViewModel.lastTabEntity)
        mBinding.tablayout.currentTab = 0
    }


    /**
     * 刷新 + 置顶
     */
    override fun onTabReselect(position: Int) {
        when (val fragment = mViewModel.getFragments(getGender(), getType(), getName())[position]) {
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