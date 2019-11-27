package com.yuhang.novel.pirate.ui.store.activity

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityMoreRankingListBinding
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.listener.OnClickMoreRankingListListener
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.store.adapter.BooksListAdapter
import com.yuhang.novel.pirate.ui.store.adapter.MoreRankingListAdapter
import com.yuhang.novel.pirate.ui.store.viewmodel.MoreRankingViewModel

/**
 * 正版排行榜 (男生/女生)
 *
 * 起点/纵横/潇湘/逐浪/云起/若初/红薯
 */
class MoreRankingListActivity :
    BaseSwipeBackActivity<ActivityMoreRankingListBinding, MoreRankingViewModel>(),
    OnRefreshLoadMoreListener , OnClickMoreRankingListListener {

    var PAGE_NUM = 1

    companion object {

        const val TYPE_MAN = "man"
        const val TYPE_LADY = "lady"

        const val TYPE = "type"//类型
        const val GENDER = "gender" //性别
        const val NAME = "name" //网站名称
        fun start(context: Activity, gender: String, type: Int, name: String) {
            val intent = Intent(context, MoreRankingListActivity::class.java)
            intent.putExtra(GENDER, gender)
            intent.putExtra(TYPE, type)
            intent.putExtra(NAME, name)
            startIntent(context, intent)
        }
    }

    /**
     * 类型
     */
    private fun getType() = intent.getIntExtra(TYPE, 1)

    /**
     * 性别
     */
    private fun getGender() = intent.getStringExtra(GENDER)!!

    /**
     * 网站名称
     */
    private fun getName() = intent.getStringExtra(NAME)!!

    override fun onLayoutId(): Int {
        return R.layout.activity_more_ranking_list
    }

    override fun initView() {
        super.initView()
        mBinding.layoutToolbar.titleTv.text = getName()
        initRefreshLayout()
        initRecyclerView()
        onClick()
    }

    private fun onClick() {
        mBinding.layoutToolbar.btnBack.setOnClickListener { onBackPressedSupport() }
        //置顶
        mBinding.layoutToolbar.toolbar.setOnClickListener {
            onTopRecyclerView(
                mBinding.refreshLayout,
                mBinding.recyclerview,
                25
            )
        }
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this)
        mBinding.refreshLayout.autoRefresh()
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
//        addOnScrollListener(mBinding.recyclerview)
        val layoutManager = VirtualLayoutManager(this)
        mBinding.recyclerview.layoutManager = layoutManager
        mViewModel.adapter = DelegateAdapter(layoutManager, true)
        mBinding.recyclerview.adapter = mViewModel.adapter
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        PAGE_NUM = 1

        mViewModel.getMoreRankingList(getGender(), getType(), PAGE_NUM)
            .compose(bindToLifecycle())
            .subscribe({
                if (!it.data.isHasNext) {
                    mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                }

                mViewModel.adapter.clear()
                val adapters = arrayListOf<DelegateAdapter.Adapter<RecyclerView.ViewHolder>>()

                val adapter = MoreRankingListAdapter()
                    .setListener(this)
                    .initData(it.data.bookList)

                adapters.add(adapter.toAdapter())

                mViewModel.adapter.addAdapters(adapters)
                mBinding.recyclerview.requestLayout()
                mBinding.refreshLayout.finishRefresh()

            },{
                mBinding.refreshLayout.finishRefresh()
            })
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        PAGE_NUM ++

        mViewModel.getMoreRankingList(getGender(), getType(), PAGE_NUM)
            .compose(bindToLifecycle())
            .subscribe({
                if (!it.data.isHasNext) {
                    mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                }


                val adapter = mViewModel.adapter.findAdapterByIndex(0) as? MoreRankingListAdapter
                adapter?.getList()?.addAll(it.data.bookList)
                adapter?.notifyDataSetChanged()

                mBinding.refreshLayout.finishLoadMore()
            },{
                mBinding.refreshLayout.finishLoadMore()
            })
    }

    /**
     * 点击进入书籍详情页
     */
    override fun onClickMoreRankingListListener(obj: BooksKSResult, position: Int) {
        BookDetailsActivity.start(this, obj.niceBooksResult())
    }
}