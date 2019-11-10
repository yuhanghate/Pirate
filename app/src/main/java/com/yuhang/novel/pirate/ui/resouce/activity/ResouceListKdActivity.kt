package com.yuhang.novel.pirate.ui.resouce.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.databinding.ActivityResouceListKdBinding
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.BookResouceTypeKDEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.resouce.viewmodel.ResouceListKdViewModel

/**
 * 快读源列表
 */
class ResouceListKdActivity :
    BaseSwipeBackActivity<ActivityResouceListKdBinding, ResouceListKdViewModel>(),
    OnClickItemListener {


    companion object {
        const val BOOKS_RESULT = "books_result"
        fun start(context: Activity, obj: BooksResult) {
            val intent = Intent(context, ResouceListKdActivity::class.java)
            intent.putExtra(BOOKS_RESULT, obj.toJson())
            startIntent(context, intent)
        }
    }

    fun getBooksResult() =
        Gson().fromJson<BooksResult>(intent.getStringExtra(BOOKS_RESULT), BooksResult::class.java)

    override fun onLayoutId(): Int {
        return R.layout.activity_resouce_list_kd
    }

    override fun initView() {
        super.initView()
        initBackground()
        initRecyclerView()
        onClick()
    }

    /**
     * 背景
     */
    private fun initBackground() {
        window.navigationBarColor = BookConstant.getPageBackground()
        mBinding.root.setBackgroundColor(BookConstant.getPageBackground())
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        mViewModel.adapter.setListener(this)
            .setDecorationSize(0)
            .setlayoutManager(LinearLayoutManager(this))
            .setRecyclerView(mBinding.recyclerView)
    }

    @SuppressLint("CheckResult")
    override fun initData() {
        super.initData()
        mBinding.progressView.show()

        mViewModel.getResouceList(getBooksResult())
            .compose(bindToLifecycle())
            .subscribe({
                mViewModel.adapter.setRefersh(it)
                mBinding.titleTv.text = "共搜索到${it.size}个网站"
                mBinding.progressView.hide()
            }, {
                mBinding.progressView.hide()
                mBinding.titleTv.text = "共搜索到0个网站"
            })
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
    }

    /**
     * Item点击事件
     */
    override fun onClickItemListener(view: View, position: Int) {
        val obj = mViewModel.adapter.getObj(position)

        ResouceChapterListActivity.start(this, BooksResult().apply {
            this.resouce = "KD"
            this.typeKd = 1
            this.typeKs = 2
            this.bookName = getBooksResult().bookName
            this.bookKdId = getBooksResult().getBookid()
        }, obj)
    }
}