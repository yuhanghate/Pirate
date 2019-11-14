package com.yuhang.novel.pirate.ui.download

import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity
import com.yuhang.novel.pirate.ui.download.activity.BookDownloadActivity
import com.yuhang.novel.pirate.ui.download.viewmodel.BookDownloadViewModel

/**
 * 是否删除缓存
 */
class DownloadDeleteDialog(val activity:BookDownloadActivity, val viewModel:BookDownloadViewModel, val obj:BookDownloadEntity) {

    fun show() {
        MaterialDialog(activity).show {
            title(text = "提示")
            message(text = "是否删除缓存数据?")
            positiveButton(text = "确定", click = object :DialogCallback{
                override fun invoke(p1: MaterialDialog) {
                    viewModel.deleteDownload(obj.bookId)
                    viewModel.adapter.getList().remove(obj)
                    viewModel.adapter.notifyDataSetChanged()
                    p1.dismiss()
                }
            })
            negativeButton(text = "取消")
        }
    }
}