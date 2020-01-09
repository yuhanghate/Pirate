package com.yuhang.novel.pirate.ui.main.dialog

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.book.activity.ChapterListActivity
import com.yuhang.novel.pirate.ui.main.fragment.MainFragment
import com.yuhang.novel.pirate.ui.main.viewmodel.MainViewModel
import java.util.*

class MainDialog(val fragment: MainFragment, val vm: MainViewModel, val obj: BookInfoKSEntity) {

    fun show() {


        val myItems: ArrayList<String>
        myItems = if (obj.stickTime > 0) {
            arrayListOf("书籍详情", "目录书摘", "删除", "取消置顶")
        } else {
            arrayListOf("书籍详情", "目录书摘", "删除", "置顶")
        }

        val activity = fragment.mActivity ?: return
        MaterialDialog(activity).show {
            listItems(items = myItems, selection = { dialog, index, text ->
                when (text) {
                    "书籍详情" -> {
                        vm.queryCollection(obj.bookid)
                            .compose(fragment.bindToLifecycle())
                            .subscribe({
                                vm.onUMEvent(
                                    activity!!,
                                    UMConstant.TYPE_MAIN_ITEM_LONG_CLICK_DETAILS,
                                    "主页 -> 书箱详情"
                                )
                                BookDetailsActivity.start(activity, it!!)
                            }, {})

                    }
                    "目录书摘" -> {
                        vm.queryCollection(obj.bookid)
                            .compose(fragment.bindToLifecycle())
                            .subscribe({
                                vm.onUMEvent(
                                    activity,
                                    UMConstant.TYPE_MAIN_ITEM_LONG_CLICK_DIR_CHANPTER, "主页 -> 目录书箱"
                                )
                                ChapterListActivity.start(activity, it!!)
                            }, {})

                    }
                    "删除" -> {
                        vm.onUMEvent(
                            activity,
                            UMConstant.TYPE_MAIN_ITEM_LONG_CLICK_DELETE,
                            "主页 -> 从书架删除收藏"
                        )
                        vm.deleteCollection(bookid = obj.bookid)
                        vm.adapter.getList().remove(obj)
                        vm.adapter.notifyDataSetChanged()
                        fragment.initEmptyView()
                    }
                    "置顶" -> {
                        vm.onUMEvent(
                            activity,
                            UMConstant.TYPE_MAIN_ITEM_LONG_CLICK_TOP,
                            "主页 -> 书架置顶"
                        )
                        vm.updateStickTime(obj.bookid)
                        fragment.netLocalData()
                    }
                    "取消置顶" -> {
                        vm.onUMEvent(
                            activity,
                            UMConstant.TYPE_MAIN_ITEM_LONG_CLICK_TOP,
                            "主页 -> 取消置顶"
                        )
                        vm.updateBookInfoClearStickTime(obj.bookid)
                        fragment.netLocalData()
                    }
                }
            })
        }
    }
}