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
import com.yuhang.novel.pirate.databinding.ActivityResouceChapterListBinding
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookResouceTypeKDEntity
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ResouceListKdResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.resouce.viewmodel.ResouceChapterListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        fun start(
            context: Activity,
            obj: BooksResult,
            chapter: ResouceListKdResult,
            chapterIndex: Int,
        ) {
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

        lifecycleScope.launch {
            flow {
                emit(
                    mViewModel.getChapterList(
                        getResouceListKdResult()._id,
                        getResouceListKdResult().book
                    )
                )
            }
                .catch { Logger.e(it.message ?: "") }
                .onStart { mBinding.progressView.show() }
                .onCompletion { mBinding.progressView.hide() }
                .collect {
                    withContext(Dispatchers.Main){
                        mBinding.titleTv.text =
                            "${getResouceListKdResult().name}(${getBooksResult().bookName})"
                        mViewModel.chapterList.addAll(it)
                        mViewModel.adapter.setRefersh(it)

                        //跳转到附近的章节
                        mBinding.recyclerView.scrollToPosition(
                            mViewModel.getChapterIndex(
                                getBooksChapterIndex()
                            )
                        )
                    }

                }
        }

    }

    /**
     * 点击打开阅读页
     */
    @SuppressLint("CheckResult")
    override fun onClickItemListener(view: View, position: Int) {
        lifecycleScope.launch {
            flow {
                emit(
                    mViewModel.insertChapterList(BookResouceTypeKDEntity().apply {
                        this.bookName = getBooksResult().bookName
                        this.bookid = getResouceListKdResult().book
                        this.resouce = getBooksResult().resouce
                        this.tocId = getResouceListKdResult()._id
                        this.typeName = getResouceListKdResult().name
                    }, getBooksResult())
                )
            }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    withContext(Dispatchers.Main){
                        val obj = mViewModel.chapterList[position]
                        ReadBookActivity.start(this@ResouceChapterListActivity, BooksResult().apply {
                            this.bookKdId = obj.bookId
                            this.bookName = getBooksResult().bookName
                            this.typeKd = 1
                            this.typeKs = 2
                            this.resouce = obj.resouce
                        }, obj.chapterId)
                        finish()
                    }

                }
        }

    }
}