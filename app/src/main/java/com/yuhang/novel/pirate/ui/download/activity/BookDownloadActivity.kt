package com.yuhang.novel.pirate.ui.download.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.work.WorkManager
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityBookDownloadBinding
import com.yuhang.novel.pirate.eventbus.DownloadEvent
import com.yuhang.novel.pirate.extension.niceBookResult
import com.yuhang.novel.pirate.listener.OnBookDownloadListener
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.listener.OnClickItemLongListener
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.download.DownloadDeleteDialog
import com.yuhang.novel.pirate.ui.download.viewmodel.BookDownloadViewModel
import com.yuhang.novel.pirate.viewholder.ItemBookDownloadVH
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


/**
 * 缓存管理
 */
class BookDownloadActivity :
    BaseSwipeBackActivity<ActivityBookDownloadBinding, BookDownloadViewModel>(),
    OnClickItemListener, OnBookDownloadListener,
    OnClickItemLongListener {


    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, BookDownloadActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_book_download
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateEventbus(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestryEventbus(this)
    }

    override fun initView() {
        super.initView()
        initRecyclerView()
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
    }

    override fun initRecyclerView() {
        super.initRecyclerView()

        mViewModel.adapter
            .setListener(this)
            .setRecyclerView(mBinding.recyclerview, false)
            .initData(arrayListOf())
    }

    override fun initData() {
        super.initData()
        mViewModel.queryBookDownloadAll()
            .compose(bindToLifecycle())
            .subscribe({
                mViewModel.adapter.setRefersh(it)
            }, {})
    }

    /**
     * 打开阅读详情
     */
    override fun onClickItemListener(view: View, position: Int) {
        val obj = mViewModel.adapter.getObj(position)
        ReadBookActivity.start(this, obj.niceBookResult(), false)
    }

    /**
     * 长按删除
     */
    override fun onClickItemLongListener(view: View, position: Int) {
        val obj = mViewModel.adapter.getObj(position)
        WorkManager.getInstance().cancelWorkById(UUID.fromString(obj.uuid))

        DownloadDeleteDialog(this, mViewModel, obj).show()
    }

    /**
     * 下载回调
     */
    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: DownloadEvent) {

        //进度回调
        mViewModel.adapter.getList()
            .forEachIndexed { index, bookDownloadEntity ->

                if (bookDownloadEntity.bookId != obj.bookId) return@forEachIndexed
                bookDownloadEntity.total = obj.total
                bookDownloadEntity.progress = obj.progress

                val vh =
                    mBinding.recyclerview.findViewHolderForAdapterPosition(index) as? ItemBookDownloadVH
                vh?.upateProgress(bookDownloadEntity)
                return@forEachIndexed
            }
    }

    /**
     * 阅读
     */
    override fun onBookDownloadListener(obj: BookDownloadEntity, position: Int, isDownload:Boolean) {
        ReadBookActivity.start(this, obj.niceBookResult(), false)
    }

}