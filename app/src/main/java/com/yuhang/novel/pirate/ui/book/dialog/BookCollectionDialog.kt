package com.yuhang.novel.pirate.ui.book.dialog

import android.annotation.SuppressLint
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.eventbus.UpdateChapterEvent
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadBookViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class BookCollectionDialog(val activity: ReadBookActivity, val viewModel: ReadBookViewModel) {

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

                    activity.lifecycleScope.launch {

                        flow {
                            viewModel.postCollection(viewModel.mBooksResult!!)
                            viewModel.insertCollection(viewModel.mBooksResult!!)
                            viewModel.onUMEvent(
                                context,
                                UMConstant.TYPE_DETAILS_CLICK_REMOVE_BOOKCASE,
                                "确定添加书架"
                            )
                            activity.niceToast("加入成功")
                            viewModel.isCollection = true
                            EventBus.getDefault().post(UpdateChapterEvent())
                            activity.onBackPressed()
                            emit(Unit)
                        }
                            .catch { activity.niceToast("加入失败") }
                            .collect { }

                    }


                }

            })
        }
    }

}