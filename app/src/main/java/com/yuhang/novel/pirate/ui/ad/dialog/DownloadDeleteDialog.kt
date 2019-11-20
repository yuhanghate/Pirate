package com.yuhang.novel.pirate.ui.ad.dialog

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.yuhang.novel.pirate.eventbus.WorkInfoEvent
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameDataResult
import com.yuhang.novel.pirate.ui.ad.activity.GameActivity
import com.yuhang.novel.pirate.ui.ad.viewmodel.GameViewModel
import com.yuhang.novel.pirate.ui.download.activity.BookDownloadActivity
import com.yuhang.novel.pirate.ui.download.viewmodel.BookDownloadViewModel
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * 是否删除缓存
 */
class DownloadDeleteDialog(
    val activity: GameActivity,
    val viewModel: GameViewModel,
    val obj: GameDataResult
) {

    fun show() {
        MaterialDialog(activity).show {
            title(text = "提示")
            message(text = "是否删除下载缓存?")
            positiveButton(text = "确定", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    viewModel.deleteDownload(obj)
                    viewModel.adapter.notifyDataSetChanged()
                    p1.dismiss()
                }
            })
            negativeButton(text = "取消")
        }
    }
}