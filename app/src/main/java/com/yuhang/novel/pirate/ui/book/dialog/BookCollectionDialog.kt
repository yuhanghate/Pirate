package com.yuhang.novel.pirate.ui.book.dialog

import android.annotation.SuppressLint
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.eventbus.UpdateChapterEvent
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadBookViewModel
import org.greenrobot.eventbus.EventBus

class BookCollectionDialog(val activity:ReadBookActivity, val viewModel:ReadBookViewModel) {

    fun show() {
        MaterialDialog(activity).show {
            title(text = "提示")
            message(text = "是否添加到书架?")
            negativeButton(text = "取消", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    viewModel.onUMEvent(
                        context,
                        UMConstant.TYPE_DETAILS_CLICK_REMOVE_BOOKCASE,
                        "取消添加书架"
                    )
                    viewModel.isCollection = true
                    activity.onBackPressedSupport()
                }
            })
            positiveButton(text = "确定", click = object : DialogCallback {
                @SuppressLint("CheckResult")
                override fun invoke(p1: MaterialDialog) {

                    viewModel.postCollection(viewModel.mBooksResult!!)
                    viewModel.insertCollection(viewModel.mBooksResult!!)
                        .compose(activity.bindToLifecycle())
                        .subscribe({
                            viewModel.onUMEvent(
                                context,
                                UMConstant.TYPE_DETAILS_CLICK_REMOVE_BOOKCASE,
                                "确定添加书架"
                            )
                            activity.niceToast("加入成功")
                            viewModel.isCollection = true
                            EventBus.getDefault().post(UpdateChapterEvent())
                            activity.onBackPressed()

                        },
                            { activity.niceToast("加入失败") })
                }

            })
        }
    }

}