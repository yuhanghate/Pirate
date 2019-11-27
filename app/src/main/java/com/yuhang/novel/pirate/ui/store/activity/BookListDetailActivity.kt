package com.yuhang.novel.pirate.ui.store.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.bumptech.glide.Glide
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.google.android.material.appbar.AppBarLayout
import com.gyf.immersionbar.ImmersionBar
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityBookListDetailBinding
import com.yuhang.novel.pirate.extension.niceBooksKSResult
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.listener.OnClickBookListener
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ShuDanDetailResult
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.common.adapter.VlayoutColumn3Adapter
import com.yuhang.novel.pirate.ui.store.adapter.ShuDanDetailDescAdapter
import com.yuhang.novel.pirate.ui.store.viewmodel.BookListDetailViewModel
import kotlin.math.abs

/**
 * 书单详情
 */
class BookListDetailActivity :
    BaseSwipeBackActivity<ActivityBookListDetailBinding, BookListDetailViewModel>(),
    AppBarLayout.OnOffsetChangedListener, OnClickBookListener {

    companion object {
        const val ID = "id"
        fun start(context: Activity, id: String) {
            val intent = Intent(context, BookListDetailActivity::class.java)
            intent.putExtra(ID, id)
            startIntent(context, intent)
        }
    }

    //获取书单id
    private fun getId() = intent.getStringExtra(ID)

    override fun onLayoutId(): Int {
        return R.layout.activity_book_list_detail
    }

    override fun initView() {
        super.initView()
        mBinding.appBar.addOnOffsetChangedListener(this)
        mBinding.includeToolbarClose.titleCloseTv.text = "书单推荐"
        onClick()
        initRecyclerView()
    }

    override fun initData() {
        super.initData()
        netData()
    }

    override fun initStatusTool() {
        ImmersionBar.with(this)
            .statusBarView(mBinding.statusBarV)
            .navigationBarColor(R.color.md_white_1000)
            .flymeOSStatusBarFontColor(R.color.primary_text)
            .statusBarDarkFont(true)
            .autoDarkModeEnable(true)
            .init()
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
//        addOnScrollListener(mBinding.recyclerview)
        val viewPool = RecyclerView.RecycledViewPool()
        mBinding.recyclerview.setRecycledViewPool(viewPool)
        viewPool.setMaxRecycledViews(0, 1)
        viewPool.setMaxRecycledViews(1, 18)

        val layoutManager = VirtualLayoutManager(this)
        mBinding.recyclerview.layoutManager = layoutManager
        mViewModel.adapter = DelegateAdapter(layoutManager, true)
        mBinding.recyclerview.adapter = mViewModel.adapter
    }

    private fun onClick() {
        mBinding.includeToolbarClose.backCloseIv.setOnClickListener { onBackPressedSupport() }
        mBinding.includeToolbarOpen.backOpenIv.setOnClickListener { onBackPressedSupport() }
    }


    /**
     * 头部动画
     */
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout ?: return
        //垂直方向偏移量
        val offset = abs(verticalOffset).toFloat();
        //最大偏移距离
        val scrollRange = appBarLayout.totalScrollRange.toFloat()
        val alpha4 = (scrollRange - offset) / scrollRange

        mBinding.statusBarV.alpha = 1 - alpha4
//        ImmersionBar.with(this).statusBarDarkFont(true, 1 - alpha4).init()
        mBinding.includeToolbarOpen.root.alpha = alpha4
        mBinding.includeToolbarClose.root.alpha = 1 - alpha4
    }

    /**
     * 加载数据
     */
    private fun netData() {
        mBinding.loading.showLoading()
        mViewModel.getBookListDetail(getId())
            .compose(bindToLifecycle())
            .subscribe({
                mBinding.loading.showContent()
                setHeaderView(it.data)
                setRecyclerView(it.data)
            }, { mBinding.loading.showError() })
    }

    /**
     * 设置头部View
     */
    private fun setHeaderView(obj: ShuDanDetailResult.DataBean) {

        Glide.with(this).load(obj.cover.niceCoverPic())
            .listener(
                GlidePalette.with(obj.cover.niceCoverPic())
                    .use(BitmapPalette.Profile.MUTED_DARK)
                    .crossfade(true)
                    .intoBackground(mBinding.includeToobarHeadOpen.bgCoverIv)
            )
            .into(mBinding.includeToobarHeadOpen.coverIv)

        mBinding.includeToobarHeadOpen.titleTv.text = obj.title
        mBinding.includeToobarHeadOpen.timeTv.text = obj.addTime

    }

    /**
     * 加载Recyclerview
     */
    private fun setRecyclerView(obj: ShuDanDetailResult.DataBean) {
        val adapters = arrayListOf<DelegateAdapter.Adapter<RecyclerView.ViewHolder>>()

        val adapter = ShuDanDetailDescAdapter()
            .initData(obj.description)
        val column3Adapter = VlayoutColumn3Adapter()
            .setListener(this)
            .initData(obj.bookList.map { it.niceBooksKSResult() }.toList())

        //简介
        adapters.add(adapter.toAdapter())
        //书本列表
        adapters.add(column3Adapter.toAdapter())

        mViewModel.adapter.addAdapters(adapters)
        mBinding.recyclerview.requestLayout()
    }


    /**
     * 打开书本
     */
    override fun onClickBookListener(view: View, obj: BooksKSResult, position: Int) {
        BookDetailsActivity.start(this, obj.niceBooksResult())
    }
}