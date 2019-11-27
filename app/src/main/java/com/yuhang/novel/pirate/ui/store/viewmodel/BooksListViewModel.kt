package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksListDataResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksListResult
import io.reactivex.Flowable
import kotlin.properties.Delegates

class BooksListViewModel:BaseViewModel() {

    var adapter : DelegateAdapter by Delegates.notNull()

    val list = arrayListOf<BooksListDataResult>()
    /**
     * 获取书单
     *
     * 最新发布/本周最热/最多收藏/小编推荐
     */
    fun getBooksList(gender: String, type: String, pageNum: Int): Flowable<BooksListResult> {
        return mDataRepository.getBooksList(gender, type, pageNum).compose(io_main())
    }
}