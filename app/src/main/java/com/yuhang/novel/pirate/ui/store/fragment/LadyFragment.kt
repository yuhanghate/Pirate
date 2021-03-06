package com.yuhang.novel.pirate.ui.store.fragment

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentLadyBinding
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.listener.OnClickBookListener
import com.yuhang.novel.pirate.listener.OnClickBooksListListener
import com.yuhang.novel.pirate.listener.OnClickItemStoreTitleMoreListener
import com.yuhang.novel.pirate.listener.OnClickMoreRankingListener
import com.yuhang.novel.pirate.repository.database.entity.StoreEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.common.adapter.VlayoutColumn3Adapter
import com.yuhang.novel.pirate.ui.common.model.LineModel
import com.yuhang.novel.pirate.ui.common.model.RankingModel
import com.yuhang.novel.pirate.ui.common.model.TitleNomoreModel
import com.yuhang.novel.pirate.ui.store.activity.BooksListActivity
import com.yuhang.novel.pirate.ui.store.activity.KanShuRankingActivity
import com.yuhang.novel.pirate.ui.store.activity.MoreRankingListActivity
import com.yuhang.novel.pirate.ui.store.adapter.*
import com.yuhang.novel.pirate.ui.store.viewmodel.LadyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 书城 - 女生
 */
class LadyFragment : BaseFragment<FragmentLadyBinding, LadyViewModel>(), OnRefreshListener,
    OnClickBookListener, OnClickBooksListListener, OnClickMoreRankingListener,
    OnClickItemStoreTitleMoreListener {

    var PAGE_NUM = 1

    companion object {
        fun newInstance(): LadyFragment {
            return LadyFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_lady
    }

    override fun initView() {
        super.initView()
        initRefreshLayout()
        initRecyclerView()
    }

    override fun initData() {
        lifecycleScope.launch {
            flow {
                val queryStoreRankingMan = mViewModel.queryStoreRankingMan()
                mViewModel.buildRanking(queryStoreRankingMan)
                emit(mViewModel.queryStoreMan())
            }
                .onStart { mBinding.progressView.show() }
                .onCompletion { mBinding.progressView.hide() }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    withContext(Dispatchers.Main) {
                        if (it.isEmpty()) {
                            onRefresh(mBinding.refreshLayout)
                            return@withContext
                        }
                        buildRecylerView(it)
                    }

                }
        }
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.refreshLayout.setOnRefreshListener(this)
        mBinding.refreshLayout.setEnableLoadMore(false)
    }

    override fun initRecyclerView() {
        super.initRecyclerView()

        val viewPool = RecyclerView.RecycledViewPool()
        mBinding.recyclerview.setRecycledViewPool(viewPool)
        viewPool.setMaxRecycledViews(0, 1)
        viewPool.setMaxRecycledViews(3, 4)
        viewPool.setMaxRecycledViews(9, 2)
        viewPool.setMaxRecycledViews(5, 3)
        viewPool.setMaxRecycledViews(1, 15)
        viewPool.setMaxRecycledViews(8, 5)
        viewPool.setMaxRecycledViews(6, 1)

        val layoutManager = VirtualLayoutManager(mActivity!!)
        mBinding.recyclerview.layoutManager = layoutManager
        mViewModel.adapter = DelegateAdapter(layoutManager, true)
        mBinding.recyclerview.adapter = mViewModel.adapter
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        lifecycleScope.launch {
            flow {
                val storeRankingLady = mViewModel.getStoreRankingLady()
                mViewModel.buildRanking(storeRankingLady)
                emit(mViewModel.getStoreLady())
            }
                .onStart { PAGE_NUM = 1 }
                .onCompletion {
                    mBinding.refreshLayout.finishRefresh()
                    mBinding.progressView.hide()
                }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    withContext(Dispatchers.Main) {
                        buildRecylerView(it)
                    }
                }
        }


    }

    /**
     * 组装Adapter
     */
    private fun buildRecylerView(list: List<StoreEntity>) {
        mViewModel.adapter.clear()

        mViewModel.buildBook(list)

        //最新发布/本周最热/最多收藏/小编推荐
        val headerAdapter = StoreHeaderAdapter()
            .setListener(this)
            .initData("")


        val adapters = arrayListOf<DelegateAdapter.Adapter<RecyclerView.ViewHolder>>()
        adapters.add(headerAdapter.toAdapter())

        //分隔线
        adapters.add(getLineAdapter(10))

        adapters.add(getTitleAndMoreAdapter("热门连载"))
        adapters.add(getColumn3Adapter(mViewModel.hotList))


        //分隔线 粗
        adapters.add(getBoldLineAdapter())

        //正版网站排行榜 标题
        adapters.add(getTitleNomoreAdapter("排行榜", R.drawable.ic_ranking_title_flag))

        //正版网站排行榜  内容
        adapters.add(getMoreRankingAdapter(mViewModel.getRankingModelList()))

        //分隔线 粗
        adapters.add(getBoldLineAdapter())



        adapters.add(getTitleAndMoreAdapter("重磅推荐"))
        adapters.add(getColumn3Adapter(mViewModel.recommendList))

        //分隔线
//                adapters.add(getLineAdapter(10))

        //榜单
        adapters.add(getRankingAdapter())

        adapters.add(getTitleAndMoreAdapter("火热新书"))
        adapters.add(getColumn3Adapter(mViewModel.newList))

        //分隔线
        adapters.add(getLineAdapter(10))

        adapters.add(getTitleAndMoreAdapter("完本精选"))
        adapters.add(getColumn3Adapter(mViewModel.goodList))


        mViewModel.adapter.addAdapters(adapters)
        mBinding.recyclerview.requestLayout()
    }

    /**
     * 分隔线
     */
    private fun getLineAdapter(top: Int): DelegateAdapter.Adapter<RecyclerView.ViewHolder> {
        //分隔线
        val adapter = StoreLineAdapter()
            .initData(LineModel(top = top))
        return adapter.toAdapter()
    }

    /**
     * 标题 + 更多
     */
    private fun getTitleAndMoreAdapter(title: String): DelegateAdapter.Adapter<RecyclerView.ViewHolder> {
        val adapter = StoreTitleMoreAdapter()
            .setListener(this)
            .initData(mViewModel.titleList[title]!!)
        return adapter.toAdapter()
    }


    /**
     * 3列 内容
     */
    private fun getColumn3Adapter(list: List<BooksKSResult>): DelegateAdapter.Adapter<RecyclerView.ViewHolder> {
        val adapter = VlayoutColumn3Adapter()
            .setListener(this)
            .initData(list)
        return adapter.toAdapter()
    }

    /**
     * 榜单
     */
    private fun getRankingAdapter(): DelegateAdapter.Adapter<RecyclerView.ViewHolder> {
        val adapter = StoreRankingAdapter()
            .setListener(this)
            .initData(mViewModel.rankingList)
        return adapter.toAdapter()
    }

    /**
     * 分隔线 粗
     */
    private fun getBoldLineAdapter(): DelegateAdapter.Adapter<RecyclerView.ViewHolder> {
        val adapter = StoreLineBoldAdapter()
            .initData(LineModel(bottom = 20))
        return adapter.toAdapter()
    }


    /**
     * 正版排行榜 内容
     */
    private fun getMoreRankingAdapter(obj: List<RankingModel>): DelegateAdapter.Adapter<RecyclerView.ViewHolder> {
        val adapter = StoreMoreRankingAdapter()
            .setListener(this)
            .initData(obj)
        return adapter.toAdapter()
    }


    /**
     * 标题
     */
    private fun getTitleNomoreAdapter(
        title: String,
        icon: Int
    ): DelegateAdapter.Adapter<RecyclerView.ViewHolder> {
        val adapter = StoreTitleNomoreAdapter()
            .setListener(this)
            .initData(TitleNomoreModel(icon = icon, name = title, isShowIcon = true))
        return adapter.toAdapter()
    }

    /**
     * 打开书本
     */
    override fun onClickBookListener(view: View, obj: BooksKSResult, position: Int) {
        BookDetailsActivity.start(mActivity!!, obj.niceBooksResult())
    }

    /**
     * 点击书单
     */
    override fun onClickBooksListListener(type: String) {
        BooksListActivity.start(mActivity!!, type, BooksListActivity.TYPE_LADY)
    }

    /**
     * 点击正版排行榜
     */
    override fun onClickMoreRankingListener(obj: RankingModel, position: Int) {
        MoreRankingListActivity.start(
            mActivity!!,
            MoreRankingListActivity.TYPE_LADY,
            obj.type,
            obj.name
        )
    }

    /**
     * 点击更多
     */
    override fun onClickItemStoreTitleMoreListener(view: View, obj: String, position: Int) {
        KanShuRankingActivity.start(
            mActivity!!,
            obj,
            KanShuRankingActivity.TYPE_LADY,
            mViewModel.rankingMap[obj]!!
        )
    }
}