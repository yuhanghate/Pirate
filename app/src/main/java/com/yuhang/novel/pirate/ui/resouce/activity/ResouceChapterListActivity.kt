package com.yuhang.novel.pirate.ui.resouce.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.databinding.ActivityResouceChapterListBinding
import com.yuhang.novel.pirate.extension.niceBookChapterKSEntity
import com.yuhang.novel.pirate.ui.resouce.viewmodel.ResouceChapterListViewModel

/**
 * 第三方源章节列表
 */
class ResouceChapterListActivity :
    BaseSwipeBackActivity<ActivityResouceChapterListBinding, ResouceChapterListViewModel>() {

    companion object {
        const val TOCID = "tocId"
        fun start(context: Activity, tocId: String) {
            val intent = Intent(context, ResouceChapterListActivity::class.java)
            intent.putExtra(TOCID,tocId)
            startIntent(context, intent)
        }
    }

    private fun getTocid() = intent.getStringExtra(TOCID)

    override fun onLayoutId(): Int {
        return R.layout.activity_resouce_chapter_list
    }

    override fun initView() {
        super.initView()
        initRecyclerView()
        initBackgroud()
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        mViewModel.adapter
            .setListener(this)
            .setDecorationColor(BookConstant.getPageBackground())
            .setDecorationSize(0)
            .setlayoutManager(LinearLayoutManager(this))
            .setRecyclerView(mBinding.recyclerView)
    }

    private fun initBackgroud() {
        //navigateion状态栏颜色
        window.navigationBarColor = BookConstant.getPageBackground()
        mBinding.root.setBackgroundColor(BookConstant.getPageBackground())
    }

    @SuppressLint("CheckResult")
    override fun initData() {
        super.initData()
        mBinding.progressView.show()
        mViewModel.getChapterList(getTocid())
            .compose(bindToLifecycle())
            .subscribe({
                mBinding.titleTv.text = "章节(${it.name})"
                mViewModel.adapter.setRefersh(it.niceBookChapterKSEntity())
                mBinding.progressView.hide()
            },{mBinding.progressView.hide()})
    }
}