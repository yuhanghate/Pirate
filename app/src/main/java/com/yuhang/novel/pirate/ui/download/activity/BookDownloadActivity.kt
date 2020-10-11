package com.yuhang.novel.pirate.ui.download.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityBookDownloadBinding
import com.yuhang.novel.pirate.eventbus.DownloadEvent
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceBookResult
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.listener.OnBookDownloadListener
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.listener.OnClickItemLongListener
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.download.dialog.DownloadDeleteDialog
import com.yuhang.novel.pirate.ui.download.viewmodel.BookDownloadViewModel
import com.yuhang.novel.pirate.viewholder.ItemBookDownloadVH
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 缓存管理
 */
class BookDownloadActivity :
    BaseSwipeBackActivity<ActivityBookDownloadBinding, BookDownloadViewModel>(),
    OnClickItemListener, OnBookDownloadListener,
    OnClickItemLongListener {

    val workMap = hashMapOf<String, WorkInfo>()

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
        mBinding.btnBack.clickWithTrigger { onBackPressedSupport() }
    }

    override fun initRecyclerView() {
        super.initRecyclerView()

        mViewModel.adapter
            .setListener(this)
            .setlayoutManager(LinearLayoutManager(this))
            .setRecyclerView(mBinding.recyclerview)
            .initData(arrayListOf())
    }

    override fun initData() {
        super.initData()

        lifecycleScope.launch {
            val  list = mViewModel.queryBookDownloadAll()
            list.forEach { entity ->
                val liveData =
                    WorkManager.getInstance().getWorkInfoByIdLiveData(entity.toUUId())
                liveData.observe(this@BookDownloadActivity, { info ->
                    workMap[info.id.toString()] = info
                    //下载失败时重新下载
                    if (info.state == WorkInfo.State.FAILED) {
                        mViewModel.downloadBook(entity.niceBookResult())
                    }
                })

            }
            mViewModel.adapter.setRefersh(list)
        }

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
        DownloadDeleteDialog(
            this,
            mViewModel,
            obj
        ).show()
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
                //如果进度小了,也返回.防止进度乱跳
                if (obj.progress < bookDownloadEntity.progress) return@forEachIndexed

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
    override fun onBookDownloadListener(
        obj: BookDownloadEntity,
        position: Int,
        isDownload: Boolean
    ) {
        ReadBookActivity.start(this, obj.niceBookResult(), false)
    }

}

