package com.yuhang.novel.pirate.ui.store.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityBooksListBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.ShuDanEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksListResult
import com.yuhang.novel.pirate.ui.store.adapter.BooksListAdapter
import com.yuhang.novel.pirate.ui.store.viewmodel.BooksListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 书单
 *
 * 最新发布/本周最热/最多收藏/小编推荐
 */
class BooksListActivity : BaseSwipeBackActivity<ActivityBooksListBinding, BooksListViewModel>(),
    OnRefreshLoadMoreListener, OnClickItemListener {

    var PAGE_NUM = 1

    companion object {
        const val TYPE_NEW = "new" //最新发布
        const val TYPE_HOT = "hot" //本周最热
        const val TYPE_COLLECT = "collect" //最多收藏
        const val TYPE_RECOMMEND = "commend" //小编推荐

        const val TYPE_MAN = "man" //男生
        const val TYPE_LADY = "lady" //女生

        const val TYPE = "type"//类型
        const val GENDER = "gender" //性别
        fun start(context: Activity, type: String, gender: String) {
            val intent = Intent(context, BooksListActivity::class.java)
            intent.putExtra(TYPE, type)
            intent.putExtra(GENDER, gender)
            startIntent(context, intent)
        }
    }

    /**
     * 类型
     */
    private fun getType() = intent.getStringExtra(TYPE)!!

    /**
     * 性别
     */
    private fun getGender() = intent.getStringExtra(GENDER)!!

    override fun onLayoutId(): Int {
        return R.layout.activity_books_list
    }

    override fun initView() {
        super.initView()
        initRecyclerView()
        initRefreshLayout()
        onClick()
    }

    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            val entity = mViewModel.queryBooksKSEntity(
                mBinding.layoutToolbar.titleTv.text.toString(),
                getGender(), getType()
            )
            if (entity.isEmpty()) {
                mBinding.refreshLayout.autoRefresh()
                return@launch
            }
            buildRecylerView(entity)
        }
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()

        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this)
//        mBinding.refreshLayout.autoRefresh()
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        val layoutManager = VirtualLayoutManager(this)
        mBinding.recyclerview.layoutManager = layoutManager
        mViewModel.adapter = DelegateAdapter(layoutManager, true)
        mBinding.recyclerview.adapter = mViewModel.adapter
    }

    private fun onClick() {
        mBinding.layoutToolbar.btnBack.clickWithTrigger { onBackPressedSupport() }
        //置顶
        mBinding.layoutToolbar.toolbar.clickWithTrigger {
            onTopRecyclerView(
                mBinding.refreshLayout,
                mBinding.recyclerview,
                25
            )
        }
        when (getType()) {
            TYPE_NEW -> mBinding.layoutToolbar.titleTv.text = "最新发布"
            TYPE_HOT -> mBinding.layoutToolbar.titleTv.text = "本周最热"
            TYPE_COLLECT -> mBinding.layoutToolbar.titleTv.text = "最多收藏"
            TYPE_RECOMMEND -> mBinding.layoutToolbar.titleTv.text = "小编推荐"
        }
    }

    /**
     * 刷新
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        PAGE_NUM = 1
        val title = mBinding.layoutToolbar.titleTv.text.toString()
        lifecycleScope.launch {
            flow {
                emit(mViewModel.getBooksList(title, getGender(), getType(), PAGE_NUM))
            }
                .onCompletion { mBinding.refreshLayout.finishRefresh() }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    withContext(Dispatchers.Main){
                        buildRecylerView(it.data)
                    }
                }
        }
    }

    /**
     * 加载更多
     */
    override fun onLoadMore(refreshLayout: RefreshLayout) {
        PAGE_NUM++
        val title = mBinding.layoutToolbar.titleTv.text.toString()
        lifecycleScope.launch {
            flow {
                emit(
                    mViewModel.getBooksList(
                        title,
                        getGender(),
                        getType(),
                        PAGE_NUM
                    )
                )
            }
                .onCompletion { mBinding.refreshLayout.finishLoadMore() }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    mViewModel.list.addAll(it.data)
                    val adapter = mViewModel.adapter.findAdapterByIndex(0) as? BooksListAdapter
                    adapter?.getList()?.addAll(it.data)
                    adapter?.notifyDataSetChanged()
                }
        }
    }

    private fun buildRecylerView(list: List<ShuDanEntity>) {
        mViewModel.adapter.clear()
        mViewModel.list.clear()
        val adapters = arrayListOf<DelegateAdapter.Adapter<RecyclerView.ViewHolder>>()

        val adapter = BooksListAdapter()
            .setListener(this)
            .initData(list)

        mViewModel.list.addAll(list)

        adapters.add(adapter.toAdapter())

        mViewModel.adapter.addAdapters(adapters)
        mBinding.recyclerview.requestLayout()
    }

    /**
     * 点击事件
     */
    override fun onClickItemListener(view: View, position: Int) {
        BookListDetailActivity.start(this, mViewModel.list[position].ListId.toString())
    }
}