package com.yuhang.novel.pirate.ui.resouce.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ChapterListKdResult
import com.yuhang.novel.pirate.ui.book.adapter.DrawerlayoutLeftAdapter
import io.reactivex.Flowable

class ResouceChapterListViewModel:BaseViewModel() {

    val adapter by lazy { DrawerlayoutLeftAdapter() }

    /**
     * 第三方源目录列表
     */
    fun getChapterList(tocId:String): Flowable<ChapterListKdResult> {
        return mConvertRepository.getResouceChapterList(tocId)
            .compose(io_main())

    }
}