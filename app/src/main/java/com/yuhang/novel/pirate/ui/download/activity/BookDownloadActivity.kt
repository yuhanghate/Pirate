package com.yuhang.novel.pirate.ui.download.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityBookDownloadBinding
import com.yuhang.novel.pirate.eventbus.DownloadEvent
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.listener.OnClickItemLongListener
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.download.DownloadDeleteDialog
import com.yuhang.novel.pirate.ui.download.viewmodel.BookDownloadViewModel
import com.yuhang.novel.pirate.viewholder.ItemBookDownloadVH
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 缓存管理
 */
class BookDownloadActivity :
    BaseSwipeBackActivity<ActivityBookDownloadBinding, BookDownloadViewModel>(),
    OnClickItemListener,
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
            .setRecyclerView(mBinding.recyclerview)
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

        ReadBookActivity.start(this, BooksResult().apply {
            this.bookName = obj.bookName
            this.author = obj.author
            this.cover = obj.cover
            if (obj.resouce == "KS") {
                this.resouce = "KS"
                this.typeKs = 1
                this.typeKd = 2
                this.bookKsId = obj.bookId
            }
            if (obj.resouce == "KD") {
                this.resouce = "KD"
                this.typeKd = 1
                this.typeKs = 2
                this.bookKdId = obj.bookId
            }
        }, false)
    }

    /**
     * 长按删除
     */
    override fun onClickItemLongListener(view: View, position: Int) {
        DownloadDeleteDialog(this, mViewModel, mViewModel.adapter.getObj(position)).show()
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

                val progress =
                    (bookDownloadEntity.progress.toDouble() / bookDownloadEntity.total.toDouble() * 100).toInt() + 1
                vh?.mBinding?.progressHorizontal?.progress = progress
                vh?.mBinding?.progressTv?.text =
                    "${bookDownloadEntity.progress}/${bookDownloadEntity.total}"

                return@forEachIndexed
            }
    }

}