package com.yuhang.novel.pirate.ui.resouce.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ResouceListKdResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.resouce.adapter.ResouceListKdAdapter

class ResouceListKdViewModel : BaseViewModel() {

    val adapter by lazy { ResouceListKdAdapter() }

    /**
     * 源列表
     */
    suspend fun getResouceList(obj:BooksResult): List<ResouceListKdResult> {
        return mConvertRepository.getResouceList(obj)
    }



}