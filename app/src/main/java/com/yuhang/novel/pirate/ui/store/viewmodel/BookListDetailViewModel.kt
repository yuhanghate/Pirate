package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ShuDanDetailResult
import io.reactivex.Flowable
import kotlin.properties.Delegates

class BookListDetailViewModel:BaseViewModel() {

    var adapter : DelegateAdapter by Delegates.notNull()

    /**
     * 获取书单
     */
    fun getBookListDetail(id: String): Flowable<ShuDanDetailResult> {
        return mDataRepository.getBookListDetail(id).compose(io_main())
    }
}