package com.yuhang.novel.pirate.ui.resouce.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ResouceListKdResult
import com.yuhang.novel.pirate.ui.resouce.adapter.ResouceListKdAdapter
import io.reactivex.Flowable

class ResouceListKdViewModel : BaseViewModel() {

    val adapter by lazy { ResouceListKdAdapter() }

    /**
     * 源列表
     */
    fun getResouceList(bookid: String): Flowable<List<ResouceListKdResult>> {
        return mConvertRepository.getResouceList(bookid)
            .compose(io_main())

    }
}