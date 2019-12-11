package com.yuhang.novel.pirate.ui.resouce.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.databinding.ActivityResouceChapterListBinding
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.BookResouceTypeKDEntity
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ResouceListKdResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.resouce.viewmodel.ResouceChapterListViewModel
import java.util.logging.Handler

/**
 * 第三方源章节列表
 */
class ResouceChapterListActivity :
    BaseSwipeBackActivity<ActivityResouceChapterListBinding, ResouceChapterListViewModel>(),
    OnClickItemListener {


    companion object {
        const val CHAPTER = "chapter"
        const val BOOKS_RESULT = "books_result"
        const val BOOKS_CHAPTER_INDEX = "books_chapter_index"
        fun start(context: Activity, obj: BooksResult, chapter: ResouceListKdResult, chapterIndex:Int) {
            val intent = Intent(context, ResouceChapterListActivity::class.java)
            intent.putExtra(CHAPTER, Gson().toJson(chapter))
            intent.putExtra(BOOKS_RESULT, obj.toJson())
            intent.putExtra(ResouceListKdActivity.BOOKS_CHAPTER_INDEX, chapterIndex)
            startIntent(context, intent)
        }
    }

    private fun getResouceListKdResult() = Gson().fromJson<ResouceListKdResult>(
        intent.getStringExtra(CHAPTER),
        ResouceListKdResult::class.java
    )

    /**
     * 看书章节列表
     */
    fun getBooksChapterIndex() = intent.getIntExtra(ResouceListKdActivity.BOOKS_CHAPTER_INDEX, 0)

    private fun getBooksResult() =
        Gson().fromJson<BooksResult>(intent.getStringExtra(BOOKS_RESULT), BooksResult::class.java)


    override fun onLayoutId(): Int {
        return R.layout.activity_resouce_chapter_list
    }

    override fun initView() {
        super.initView()
        initRecyclerView()
        initBackgroud()
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

    override fun initRecyclerView() {
        super.initRecyclerView()
        mViewModel.adapter
            .setListener(this)
            .setDecorationColor(BookConstant.getPageBackground())
            .setDecorationSize(0)
            .setlayoutManager(LinearLayoutManager(this))
            .setRecyclerView(mBinding.recyclerView)

        mBinding.fastscroll.setRecyclerView(mBinding.recyclerView)
    }

    private fun initBackgroud() {
        //navigateion状态栏颜色
//        window.navigationBarColor = BookConstant.getPageBackground()
        mBinding.toolbarAbl.setBackgroundColor(BookConstant.getPageBackground())
        mBinding.root.setBackgroundColor(BookConstant.getPageBackground())
    }

    @SuppressLint("CheckResult")
    override fun initData() {
        super.initData()
        mBinding.progressView.show()
        mViewModel.getChapterList(getResouceListKdResult()._id, getResouceListKdResult().book)
            .compose(bindToLifecycle())
            .subscribe({
                mBinding.titleTv.text =
                    "${getResouceListKdResult().name}(${getBooksResult().bookName})"
                mViewModel.chapterList.addAll(it)
                mViewModel.adapter.setRefersh(it)

                //跳转到附近的章节
//                android.os.Handler().postDelayed({mBinding.recyclerView.scrollToPosition(mViewModel.getChapterIndex(getBooksChapterIndex()))}, 200)
                mBinding.recyclerView.scrollToPosition(mViewModel.getChapterIndex(getBooksChapterIndex()))

                mBinding.progressView.hide()
            }, {
                mBinding.progressView.hide()
            })
    }

    /**
     * 点击打开阅读页
     */
    @SuppressLint("CheckResult")
    override fun onClickItemListener(view: View, position: Int) {
        val obj = mViewModel.chapterList[position]
        mViewModel.insertChapterList(BookResouceTypeKDEntity().apply {
            this.bookName = getBooksResult().bookName
            this.bookid = getResouceListKdResult().book
            this.resouce = getBooksResult().resouce
            this.tocId = getResouceListKdResult()._id
            this.typeName = getResouceListKdResult().name
        }, getBooksResult()).compose(bindToLifecycle()).subscribe({
            ReadBookActivity.start(this, BooksResult().apply {
                this.bookKdId = obj.bookId
                this.bookName = getBooksResult().bookName
                this.typeKd = 1
                this.typeKs = 2
                this.resouce = obj.resouce
            }, obj.chapterId)
            finish()
        }, {})
    }
}