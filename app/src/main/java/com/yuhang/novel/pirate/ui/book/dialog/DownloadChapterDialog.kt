package com.yuhang.novel.pirate.ui.book.dialog

import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
import com.afollestad.materialdialogs.list.ItemListener
import com.afollestad.materialdialogs.list.listItems
import com.vondear.rxtool.RxNetTool
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadBookViewModel

class DownloadChapterDialog(val activity:ReadBookActivity, val viewModel:ReadBookViewModel, val obj:BooksResult):DialogCallback {
    override fun invoke(p1: MaterialDialog) {
        activity.initStatusTool()
    }

    fun show() {
        MaterialDialog(activity).show {
            listItems(items = arrayListOf("全本缓存", "从当前章节缓存"), selection = object :ItemListener{
                override fun invoke(dialog: MaterialDialog, index: Int, text: String) {
                    //不是wifi提示
                    if (!RxNetTool.isWifi(activity)) {
                        DownloadWifiDialog(activity, viewModel, obj).show()
                        return
                    }
                    viewModel.downloadBook(obj)
                    activity.niceToast("已加入缓存队列")

                }
            })

            onCancel(this@DownloadChapterDialog)
        }
    }
}