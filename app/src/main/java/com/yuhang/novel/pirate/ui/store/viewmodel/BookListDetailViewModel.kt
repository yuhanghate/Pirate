package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ShuDanDetailResult
import kotlin.properties.Delegates

class BookListDetailViewModel:BaseViewModel() {

    var adapter : DelegateAdapter by Delegates.notNull()

    /**
     * 获取书单
     */
    suspend fun getBookListDetail(id: String): ShuDanDetailResult {
        return mDataRepository.getBookListDetail(id)
    }
}