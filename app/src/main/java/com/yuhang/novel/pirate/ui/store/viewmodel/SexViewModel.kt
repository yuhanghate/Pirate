package com.yuhang.novel.pirate.ui.store.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.network.data.pirate.result.ChapterSexResult
import com.yuhang.novel.pirate.ui.store.adapter.BookSexAdapter
import io.reactivex.Flowable

class SexViewModel:BaseViewModel() {

    val adapter by lazy { BookSexAdapter() }


    /**
     * 随机获取小黄书列表
     */
    fun getBookSexList(pageNum:Int): Flowable<ChapterSexResult> {
        return mDataRepository.getBookSexList(pageNum).compose(io_main()).compose(mFragment?.bindToLifecycle())
    }
}