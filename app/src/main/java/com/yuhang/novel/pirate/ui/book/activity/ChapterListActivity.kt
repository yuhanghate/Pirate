package com.yuhang.novel.pirate.ui.book.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.databinding.ActivityChapterListBinding
import com.yuhang.novel.pirate.extension.niceBookInfoKSEntity
import com.yuhang.novel.pirate.listener.OnClickChapterItemListener
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.ui.book.fragment.DrawerLayoutLeftFragment
import com.yuhang.novel.pirate.ui.book.viewmodel.ChapterListViewModel

/**
 * 章节目录
 */
class ChapterListActivity : BaseSwipeBackActivity<ActivityChapterListBinding, ChapterListViewModel>(), OnClickChapterItemListener,
        OnClickItemListener {



    companion object {
        private const val BOOKID = "bookid"
        private const val CHAPTERID = "chapterid"
        fun start(context: Activity, bookid: Long, chapterid: Int) {
            val intent = Intent(context, ChapterListActivity::class.java)
            intent.putExtra(BOOKID, bookid)
            intent.putExtra(CHAPTERID, chapterid)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_chapter_list
    }

    /**
     * 小说id
     */
    private fun getBookid() = intent.getLongExtra(BOOKID, -1)

    /**
     * 章节id
     */
    private fun getChapterid() = intent.getIntExtra(CHAPTERID, -1)


    override fun initView() {
        super.initView()
        onClick()
        initBookInfo()
        initRecyclerView()
        initBackgroud()
    }

    private fun initBackgroud() {
        mBinding.root.setBackgroundColor(BookConstant.getPageBackground())
        mBinding.toolbar.setBackgroundColor(BookConstant.getPageBackground())
        if (BookConstant.getPageColorIndex() == 3) {
            mBinding.backCloseIv.setImageResource(R.drawable.btn_back_white)
        } else {
            mBinding.backCloseIv.setImageResource(R.drawable.btn_back_black)
        }
    }

    @SuppressLint("CheckResult")
    private fun initBookInfo() {
        mViewModel.queryBookInfo(getBookid())
                .compose(bindToLifecycle())
                .subscribe({
                    mBinding.titleCloseTv.text = it?.bookName
                },{})
    }

    @SuppressLint("CheckResult")
    override fun initRecyclerView() {
        super.initRecyclerView()

        mViewModel.adapter.setListener(this)
        mViewModel.adapter
            .setlayoutManager(LinearLayoutManager(this))
                .setRecyclerView(mBinding.recyclerView, false)
        mBinding.fastscroll.setRecyclerView(mBinding.recyclerView)

        mViewModel.queryChapterList(getBookid())
                .compose(bindToLifecycle())
                .subscribe({ list ->
                    mViewModel.adapter.setRefersh(list)
                }, {})
    }

    private fun onClick() {
        mBinding.backCloseIv.setOnClickListener { onBackPressed() }
    }

    /**
     * 章节目录点击
     */
    @SuppressLint("CheckResult")
    override fun onClickChapterItemListener(view: View, chapterid: Int) {
        ReadBookActivity.start(this, getBookid(), chapterid)
    }

    /**
     * Item点击事件
     */
    override fun onClickItemListener(view: View, position: Int) {
        val chapterKSEntity = mViewModel.adapter.getObj(position)
        ReadBookActivity.start(this, getBookid(), chapterKSEntity.chapterId)
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