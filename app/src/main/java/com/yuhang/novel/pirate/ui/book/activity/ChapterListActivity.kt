package com.yuhang.novel.pirate.ui.book.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.databinding.ActivityChapterListBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.listener.OnClickChapterItemListener
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.book.viewmodel.ChapterListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 章节目录
 */
class ChapterListActivity :
    BaseSwipeBackActivity<ActivityChapterListBinding, ChapterListViewModel>(),
    OnClickChapterItemListener,
    OnClickItemListener {

    private var booksResult: BooksResult? = null

    companion object {
        private const val BOOKID = "bookid"
        private const val BOOK_RESULT = "book_result"

        fun start(context: Activity, obj: BooksResult) {
            val intent = Intent(context, ChapterListActivity::class.java)
            intent.putExtra(BOOK_RESULT, obj.toJson())
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_chapter_list
    }

    private fun getBooksResult(): BooksResult {
        if (booksResult == null) {
            booksResult = Gson().fromJson<BooksResult>(
                intent.getStringExtra(BOOK_RESULT),
                BooksResult::class.java
            )
        }
        return booksResult!!
    }

    /**
     * 小说id
     */
    private fun getBookid() = intent.getStringExtra(BOOKID)!!


    override fun initStatusTool() {
        ImmersionBar.with(this)
            .statusBarView(mBinding.statusBarV)
//            .statusBarColor(BookConstant.getPageBackgroundString())
            .statusBarColorTransform(BookConstant.getPageBackgroundString())
            .navigationBarColor(BookConstant.getPageBackgroundString())
            .flymeOSStatusBarFontColor(R.color.primary_text)
            .statusBarDarkFont(true)
            .autoDarkModeEnable(true)
            .init()
    }

    override fun initView() {
        super.initView()
        onClick()
        initBookInfo()
        initRecyclerView()
        initBackgroud()
    }

    private fun initBackgroud() {
        //navigateion状态栏颜色
//        window.navigationBarColor = BookConstant.getPageBackground()
//        mBinding.root.setBackgroundColor(BookConstant.getPageBackground())
        mBinding.statusBarV.setBackgroundColor(BookConstant.getPageBackground())
        mBinding.toolbar.setBackgroundColor(BookConstant.getPageBackground())
        if (BookConstant.getPageColorIndex() == 3) {
            mBinding.backCloseIv.setImageResource(R.drawable.btn_back_white)
        } else {
            mBinding.backCloseIv.setImageResource(R.drawable.btn_back_black)
        }
    }

    @SuppressLint("CheckResult")
    private fun initBookInfo() {
        lifecycleScope.launch {
            val info = mViewModel.queryBookInfo(getBooksResult().getBookid())
            mBinding.titleCloseTv.text = info?.bookName
        }
    }

    @SuppressLint("CheckResult")
    override fun initRecyclerView() {
        super.initRecyclerView()

        mViewModel.adapter.setListener(this)
        mViewModel.adapter
            .setlayoutManager(LinearLayoutManager(this))
            .setRecyclerView(mBinding.recyclerView, false)
        mBinding.fastscroll.setRecyclerView(mBinding.recyclerView)

        lifecycleScope.launch {
            flow { emit(mViewModel.queryChapterList(getBooksResult())) }
                .catch {
                    mBinding.progressView.hide()
                    Logger.e(it.message?:"")
                }
                .onCompletion { mBinding.progressView.hide() }
                .onStart { mBinding.progressView.show() }
                .collect {
                    withContext(Dispatchers.Main){
                        mViewModel.adapter.setRefersh(it)
                    }
                }
        }
    }

    private fun onClick() {
        mBinding.backCloseIv.clickWithTrigger { onBackPressed() }
    }

    /**
     * 章节目录点击
     */
    @SuppressLint("CheckResult")
    override fun onClickChapterItemListener(view: View, chapterid: String) {
        lifecycleScope.launch {
            val queryCollection = mViewModel.queryCollection(getBookid())
            ReadBookActivity.start(this@ChapterListActivity, queryCollection?.niceBooksResult()!!, chapterid)
            onBackPressedSupport()
        }
    }

    /**
     * Item点击事件
     */
    @SuppressLint("CheckResult")
    override fun onClickItemListener(view: View, position: Int) {
        lifecycleScope.launch {
            val chapterKSEntity = mViewModel.adapter.getObj(position)
            val queryCollection = mViewModel.queryCollection(chapterKSEntity.bookId)
            ReadBookActivity.start(this@ChapterListActivity, queryCollection?.niceBooksResult()!!, chapterKSEntity.chapterId)
            onBackPressedSupport()
        }
    }

    override fun onPause() {
        super.onPause()
        mViewModel.onPause(this)
    }

    override fun onResume() {
        super.onResume()
        mViewModel.onResume(this)
    }
}