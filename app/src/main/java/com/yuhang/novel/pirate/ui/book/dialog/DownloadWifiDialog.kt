package com.yuhang.novel.pirate.ui.book.dialog

import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadBookViewModel

class DownloadWifiDialog(val activity:ReadBookActivity, val viewModel:ReadBookViewModel, val obj: BooksResult) : DialogCallback{
    override fun invoke(p1: MaterialDialog) {
        activity.initStatusTool()
    }

    fun show() {
        MaterialDialog(activity).show {
            title(text = "温馨提示")
            message(text = "正在使用运营商网络,继续下载将消耗移动数据流量")
            positiveButton(text = "确定", click = object :DialogCallback{
                override fun invoke(dialog: MaterialDialog) {
                    dialog.dismiss()
                    activity.initStatusTool()
                    viewModel.downloadBook(obj)
                    activity.niceToast("已加入缓存队列")
                }
            })
            negativeButton(text = "取消", click = this@DownloadWifiDialog )
            onCancel(this@DownloadWifiDialog)
        }
    }
}