package com.yuhang.novel.pirate.ui.main.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.navOptions
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.databinding.FragmentSotreBinding
import com.yuhang.novel.pirate.ui.main.viewmodel.StoreViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yuhang.novel.pirate.constant.BookKSConstant
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.widget.DoubleClick
import com.yuhang.novel.pirate.widget.DoubleClickListener


/**
 * 书架
 */
class StoreFragment : BaseFragment<FragmentSotreBinding, StoreViewModel>(), OnRefreshLoadMoreListener,
        OnClickItemListener {



    private var mTopInAnim: Animation? = null
    private var mTopOutAnim: Animation? = null
    private var mBottomInAnim: Animation? = null
    private var mBottomOutAnim: Animation? = null
    private var mBackgroundInTransparent: Animation? = null
    private var mBackgroundOutTransparent: Animation? = null

    val DURATION: Long = 190

    /**
     * 页面
     */
    private var PAGE_NUM = 1

    override fun onLayoutId(): Int {
        return R.layout.fragment_sotre
    }

    override fun initView() {
        super.initView()
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }
        Log.i("StoreFragment", "${this.hashCode()}")

        initRefreshLayout()
        initRecyclerView()

//        netServiceData()
        initAnimation()
        onClick()
    }

    private fun onClick() {
        mBinding.filterIv.setOnClickListener { setFileterView() }
        mBinding.filterV.setOnClickListener { setFileterView() }
        mBinding.filterLl.visibility = View.GONE
        val genderList = arrayListOf<ConstraintLayout>(mBinding.genderManCl, mBinding.genderLadyCl)
        val typeList = arrayListOf<ConstraintLayout>(mBinding.typeHotCl, mBinding.typeCommendCl, mBinding.typeOverCl, mBinding.typeCollectCl, mBinding.typeNewCl, mBinding.typeVoteCl)
        val dateList = arrayListOf<ConstraintLayout>(mBinding.dateWeekCl, mBinding.dateMonthCl, mBinding.dateTotalCl)

        mBinding.genderManCl.setOnClickListener { setClickItem(it as ConstraintLayout, genderList) }
        mBinding.genderLadyCl.setOnClickListener { setClickItem(it as ConstraintLayout, genderList) }

        mBinding.typeHotCl.setOnClickListener { setClickItem(it as ConstraintLayout, typeList) }
        mBinding.typeCommendCl.setOnClickListener { setClickItem(it as ConstraintLayout, typeList) }
        mBinding.typeOverCl.setOnClickListener { setClickItem(it as ConstraintLayout, typeList) }
        mBinding.typeCollectCl.setOnClickListener { setClickItem(it as ConstraintLayout, typeList) }
        mBinding.typeNewCl.setOnClickListener { setClickItem(it as ConstraintLayout, typeList) }
        mBinding.typeVoteCl.setOnClickListener { setClickItem(it as ConstraintLayout, typeList) }

        mBinding.dateWeekCl.setOnClickListener { setClickItem(it as ConstraintLayout, dateList) }
        mBinding.dateMonthCl.setOnClickListener { setClickItem(it as ConstraintLayout, dateList) }
        mBinding.dateTotalCl.setOnClickListener { setClickItem(it as ConstraintLayout, dateList) }

        mBinding.btnReset.setOnClickListener {
            mViewModel.gender = "man"
            mViewModel.type = "hot"
            mViewModel.date = "week"
            setFileterView()
            Handler().postDelayed({
                mBinding.refreshLayout.autoRefresh()
            }, DURATION * 3)
        }
        mBinding.btnCommit.setOnClickListener {
            setFileterView()
            Handler().postDelayed({
                mBinding.refreshLayout.autoRefresh()
            }, DURATION * 3)
        }

        //双击刷新
        mBinding.toolbar.setOnClickListener(DoubleClick(object : DoubleClickListener {
            override fun onSingleClick(view: View?) {
            }

            override fun onDoubleClick(view: View?) {
                scrollTop()
            }
        }))

    }

    /**
     * 双击滑动到顶部并刷新
     */
    private fun scrollTop() {
        mBinding.recyclerview.scrollToPosition(20)
        mBinding.recyclerview.smoothScrollToPosition(0)
        Handler().postDelayed({
            mBinding.refreshLayout.autoRefresh()
        }, 1000)
    }

    private fun setClickItem(item: ConstraintLayout, list: List<ConstraintLayout>) {
        list.forEach {
            if (it == item) {
                setFilterItemClick(it, true)
            } else {
                setFilterItemClick(it, false)
            }
        }
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.loading.showContent()
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this)
        mBinding.refreshLayout.autoRefresh()
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        mViewModel.adapter.setListener(this)
        mViewModel.adapter.initData(arrayListOf())
        val decoration = HorizontalDividerItemDecoration.Builder(mActivity)
                .size(niceDp2px(20f))
                .color(android.R.color.white)
                .build()
        val layoutManager = LinearLayoutManager(mActivity)
        layoutManager.orientation = RecyclerView.VERTICAL
        mBinding.recyclerview.layoutManager = layoutManager
//        mBinding.recyclerview.addItemDecoration(decoration)
        mBinding.recyclerview.adapter = mViewModel.adapter
    }

    /**
     * 获取服务器数据
     */
    @SuppressLint("CheckResult")
    private fun netServiceData() {
        mBinding.loading.showLoading()
        mViewModel.getBookCategory()
                .compose(bindToLifecycle())
                .subscribe({
                    mBinding.loading.showContent()
//                    it.data?.let { mViewModel.adapter.setRefersh(it) }
                }, {
                    mBinding.loading.showError()
                })
    }

    /**
     * 动画初始化
     */
    private fun initAnimation() {
        mTopInAnim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_in)
        mTopOutAnim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_out)
        mBottomInAnim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_in)
        mBottomOutAnim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_out)
        mBackgroundInTransparent = AnimationUtils.loadAnimation(mActivity, R.anim.slide_in_transparent)
        mBackgroundOutTransparent = AnimationUtils.loadAnimation(mActivity, R.anim.slide_out_transparent)
        //退出的速度要快
        mTopOutAnim?.duration = DURATION
        mBottomOutAnim?.duration = DURATION
    }

    /**
     * 设置删选
     */
    private fun setFileterView() {
        if (mBinding.filterLl.visibility == View.VISIBLE) {

            //关闭
            mBinding.filterIv.setImageResource(R.drawable.ic_filter_unselect)
            mBinding.filterLl.startAnimation(mTopOutAnim)
            mBinding.filterV.startAnimation(mBackgroundOutTransparent)

            Handler().postDelayed({
                mBinding.filterLl.visibility = View.INVISIBLE
                mBinding.filterV.visibility = View.INVISIBLE
            }, DURATION)

        } else {

            //打开
            mBinding.filterIv.setImageResource(R.drawable.ic_filter_select)
            mBinding.filterLl.visibility = View.VISIBLE
            mBinding.filterV.visibility = View.VISIBLE
            mBinding.filterV.startAnimation(mBackgroundInTransparent)
            mBinding.filterLl.startAnimation(mTopInAnim)
        }
    }

    /**
     * 设置item Filter
     */
    private fun setFilterItemClick(item: ConstraintLayout, isClick: Boolean) {


        val textview = item.getChildAt(0) as TextView
        val imageView = item.getChildAt(1) as ImageView

        if (isClick) {
            textview.setTextColor((Color.parseColor("#286EE2")))
            textview.setBackgroundColor(Color.parseColor("#ECF3FF"))
            imageView.visibility = View.VISIBLE
            val tag = textview.tag as String

            //设置标签
            when {
                BookKSConstant.FILTER_GENDER.contains(tag) -> mViewModel.gender = tag
                BookKSConstant.FILTER_TYPE.contains(tag) -> mViewModel.type = tag
                BookKSConstant.FILTER_DATE.contains(tag) -> mViewModel.date = tag
            }
        } else {
            textview.setTextColor((Color.parseColor("#666666")))
            textview.setBackgroundColor(Color.parseColor("#F8F8F8"))
            imageView.visibility = View.GONE
        }
    }

    /**
     * 加载更多
     */
    @SuppressLint("CheckResult")
    override fun onLoadMore(refreshLayout: RefreshLayout) {
        PAGE_NUM++
        mViewModel.getRankingList(PAGE_NUM)
                .compose(bindToLifecycle())
                .subscribe({

                    mBinding.loading.showContent()


                    if (!it.data.isHasNext) {
                        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                    } else {
                        mBinding.refreshLayout.finishLoadMore()
                        mViewModel.adapter.loadMore(it.data.bookList)
                    }

                }, {
                    //                    mBinding.loading.showError()
                    mBinding.refreshLayout.finishRefresh()
                })
    }

    /**
     * 刷新
     */
    @SuppressLint("CheckResult")
    override fun onRefresh(refreshLayout: RefreshLayout) {

        PAGE_NUM = 1
        mBinding.loading.showContent()
        mBinding.refreshLayout.finishLoadMore()
        mViewModel.getRankingList(PAGE_NUM)
                .compose(bindToLifecycle())
                .subscribe({
                    mViewModel.adapter.setRefersh(it.data.bookList)
                    mBinding.loading.showContent()
                    mBinding.refreshLayout.finishRefresh()
                }, {
                    //                    mBinding.loading.showError()
                    mBinding.refreshLayout.finishRefresh()
                })
    }

    override fun onClickItemListener(view: View, position: Int) {
        BookDetailsActivity.start(mActivity!!, mViewModel.adapter.getObj(position).Id)
    }
}