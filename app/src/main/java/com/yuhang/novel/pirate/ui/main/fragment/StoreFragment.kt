package com.yuhang.novel.pirate.ui.main.fragment

import android.annotation.SuppressLint
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.constant.BookKSConstant
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.FragmentSotreBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.main.viewmodel.StoreViewModel
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

    val genderList by lazy { arrayListOf<ConstraintLayout>(mBinding.genderManCl, mBinding.genderLadyCl) }
    val typeList by lazy {
        arrayListOf<ConstraintLayout>(
            mBinding.typeHotCl,
            mBinding.typeCommendCl,
            mBinding.typeOverCl,
            mBinding.typeCollectCl,
            mBinding.typeNewCl,
            mBinding.typeVoteCl
        )
    }
    val dateList by lazy {
        arrayListOf<ConstraintLayout>(
            mBinding.dateWeekCl,
            mBinding.dateMonthCl,
            mBinding.dateTotalCl
        )
    }

    val DURATION: Long = 190

    /**
     * 页面
     */
    private var PAGE_NUM = 1

    companion object {
        fun newInstance(): StoreFragment {
            return StoreFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_sotre
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        mViewModel.onPageStart("商城页面")
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        mViewModel.onPageEnd("商城页面")

    }
    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {

        super.onPause()

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

        initRefreshLayout()
        initRecyclerView()
        initAnimation()
        initFilterView()
        onClick()
        loadLocalRankingList()
    }

    /**
     * 初始化删选
     */
    private fun initFilterView() {
        setClickItem(mBinding.genderManCl, genderList)
        setClickItem(mBinding.typeHotCl, typeList)
        setClickItem(mBinding.dateWeekCl, dateList)
    }

    private fun onClick() {
        mBinding.filterIv.setOnClickListener { setFileterView() }
        mBinding.filterV.setOnClickListener { setFileterView() }
        mBinding.filterLl.visibility = View.GONE


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
                initFilterView()
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
//        mBinding.refreshLayout.autoRefresh()

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
            textview.setTextColor(ContextCompat.getColor(mActivity!!, R.color.text_white_color))
            textview.setBackgroundColor(ContextCompat.getColor(mActivity!!, R.color.item_select_color))
            imageView.visibility = View.GONE
            val tag = textview.tag as String

            //设置标签
            when {
                BookKSConstant.FILTER_GENDER.contains(tag) -> mViewModel.gender = tag
                BookKSConstant.FILTER_TYPE.contains(tag) -> mViewModel.type = tag
                BookKSConstant.FILTER_DATE.contains(tag) -> mViewModel.date = tag
            }
        } else {
            textview.setTextColor(ContextCompat.getColor(mActivity!!, R.color.primary_text))
            textview.setBackgroundColor(ContextCompat.getColor(mActivity!!, R.color.item_unselect_color))
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
                mBinding.refreshLayout.finishRefresh()
            }, {
                //                    mBinding.loading.showError()
                mBinding.refreshLayout.finishRefresh()
            })
    }

    /**
     * 第一次加载本地数据
     */
    @SuppressLint("CheckResult")
    private fun loadLocalRankingList() {
        mViewModel.getRankingListLocal()
            .compose(bindToLifecycle())
            .subscribe({
                if (it.isEmpty()) {
                    //如果本地没有数据,就从服务器加载
                    mBinding.refreshLayout.autoRefresh()
                } else {
                    mViewModel.adapter.setRefersh(it.map { result -> result!! }.toList())
                    mBinding.refreshLayout.finishRefresh()
                }

            }, {
                mBinding.refreshLayout.finishRefresh()
            })
    }

    override fun onClickItemListener(view: View, position: Int) {
        val obj = mViewModel.adapter.getObj(position)
        mViewModel.onUMEvent(
            mActivity!!,
            UMConstant.TYPE_STORE_CLICK_ITEM,
            hashMapOf(
                "action" to "书城 -> 点击书城列表",
                "bookName" to obj.Name,
                "bookId" to obj.Id.toString(),
                "author" to obj.Author
            )
        )
        BookDetailsActivity.start(mActivity!!, obj.Id)
    }
}