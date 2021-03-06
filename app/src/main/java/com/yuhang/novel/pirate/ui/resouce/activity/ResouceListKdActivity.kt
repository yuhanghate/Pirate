package com.yuhang.novel.pirate.ui.resouce.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.databinding.ActivityResouceListKdBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.BookResouceTypeKDEntity
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ResouceListKdResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.resouce.viewmodel.ResouceListKdViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 快读源列表
 */
class ResouceListKdActivity :
    BaseSwipeBackActivity<ActivityResouceListKdBinding, ResouceListKdViewModel>(),
    OnClickItemListener {


    companion object {
        const val BOOKS_RESULT = "books_result"
        const val BOOKS_CHAPTER_INDEX = "books_chapter_index"
        fun start(context: Activity, obj: BooksResult, chapterIndex: Int) {
            val intent = Intent(context, ResouceListKdActivity::class.java)
            intent.putExtra(BOOKS_CHAPTER_INDEX, chapterIndex)
            intent.putExtra(BOOKS_RESULT, obj.toJson())
            startIntent(context, intent)
        }
    }

    fun getBooksResult() =
        Gson().fromJson<BooksResult>(intent.getStringExtra(BOOKS_RESULT), BooksResult::class.java)

    /**
     * 看书章节列表
     */
    fun getBooksChapterIndex() = intent.getIntExtra(BOOKS_CHAPTER_INDEX, 0)

    override fun onLayoutId(): Int {
        return R.layout.activity_resouce_list_kd
    }

    override fun initView() {
        super.initView()
        initBackground()
        initRecyclerView()
        onClick()
    }

    override fun initStatusTool() {
        val windows = PreferenceUtil.getString("page_color", "#F6EFDD")
        ImmersionBar.with(this)
            .statusBarView(mBinding.statusBarV)
            .flymeOSStatusBarFontColor(R.color.primary_text)
            .statusBarColor(windows)
            .statusBarDarkFont(true)
            .autoDarkModeEnable(true)
            .navigationBarColor(windows)
            .init()
    }

    /**
     * 背景
     */
    private fun initBackground() {
//        window.navigationBarColor = BookConstant.getPageBackground()
        mBinding.toolbarAbl.setBackgroundColor(BookConstant.getPageBackground())
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

        lifecycleScope.launch {
            flow { emit(mViewModel.getResouceList(getBooksResult())) }
                .onStart { mBinding.progressView.show() }
                .onCompletion { mBinding.progressView.hide() }
                .catch { mBinding.titleTv.text = "共搜索到0个网站" }
                .collect {
                    mViewModel.adapter.setRefersh(it)
                    mBinding.titleTv.text = "共搜索到${it.size}个网站"
                }
        }
    }

    private fun onClick() {
        mBinding.btnBack.clickWithTrigger { onBackPressedSupport() }
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
        }, obj, getBooksChapterIndex())
    }
}