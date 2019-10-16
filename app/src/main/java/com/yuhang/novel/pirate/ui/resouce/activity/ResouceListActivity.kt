package com.yuhang.novel.pirate.ui.resouce.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityResouceListBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.ui.resouce.viewmodel.ResouceListViewModel

/**
 * 书源列表
 */
class ResouceListActivity : BaseSwipeBackActivity<ActivityResouceListBinding, ResouceListViewModel>(),
    OnRefreshLoadMoreListener, OnClickItemListener {


    private var PAGE_NUM = 1

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ResouceListActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_resouce_list
    }

    override fun initView() {
        super.initView()
        initRecyclerView()
        initRefreshLayout()
        onClick()
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this)
        mBinding.refreshLayout.autoRefresh()
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        mViewModel.adapter
            .setDecorationSize(niceDp2px(2f))
            .setListener(this)
            .setDecorationColor(android.R.color.transparent)
            .setlayoutManager(LinearLayoutManager(this))
            .setRecyclerView(mBinding.recyclerview)
    }

    fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
        mBinding.btnCommit.setOnClickListener {
            mViewModel.updateResouceCheckStatuts(mViewModel.list)
            onBackPressedSupport()
        }
    }

    @SuppressLint("CheckResult")
    override fun initData() {
        super.initData()

    }


    @SuppressLint("CheckResult")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        PAGE_NUM = 1
        mViewModel.getResouceList(PAGE_NUM)
            .flatMap { mViewModel.getDefaultCheckResouce(it.data.list) }
            .compose(bindToLifecycle())
            .subscribe({
                mViewModel.adapter.setRefersh(it)
                mViewModel.insertResouceList(it)
                mBinding.refreshLayout.finishRefresh()
                initBtnCommit()

            }, {
                mBinding.refreshLayout.finishRefresh()
                Logger.i("")
            })
    }

    @SuppressLint("CheckResult")
    override fun onLoadMore(refreshLayout: RefreshLayout) {
        PAGE_NUM++
        mViewModel.getResouceList(PAGE_NUM)
            .flatMap { mViewModel.getDefaultCheckResouce(it.data.list) }
            .compose(bindToLifecycle())
            .subscribe({

                mViewModel.adapter.loadMore(it)
                mViewModel.insertResouceList(it)
                mBinding.refreshLayout.finishLoadMore()
                if (it.isEmpty()) {
                    mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                }
                initBtnCommit()

            }, {
                mBinding.refreshLayout.finishLoadMore()
            })
    }

    /**
     * 源列表点击事件
     */
    @SuppressLint("SetTextI18n")
    override fun onClickItemListener(view: View, position: Int) {
        val checkBox = view.findViewById<CheckBox>(R.id.checkbox)
        val obj = mViewModel.adapter.getObj(position)

        if (!checkBox.isChecked) {
//            if (mViewModel.list.size == 6) {
//                niceToast("最多只能选择6个源")
//                return
//            }
            mViewModel.list.add(obj)
        } else {
            mViewModel.list.remove(obj)
        }

        obj.isCheck = if (!checkBox.isChecked) 1 else 0
        checkBox.isChecked = obj.isCheck == 1


        initBtnCommit()

    }

    /**
     * 初始化View
     */
    private fun initBtnCommit() {
        if (mViewModel.list.size > 0) {
            mBinding.btnCommit.text = "确定(${mViewModel.list.size})"
            mBinding.btnCommit.setBackgroundResource(R.drawable.bg_material_item_red_round_color500)
            mBinding.btnCommit.isClickable = true
        } else {
            mBinding.btnCommit.text = "确定"
            mBinding.btnCommit.setBackgroundResource(R.drawable.bg_material_item_red_round_color300)
            mBinding.btnCommit.isClickable = false
        }
    }


}