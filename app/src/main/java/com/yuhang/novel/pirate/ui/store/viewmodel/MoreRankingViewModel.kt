package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.MoreRankingResult
import io.reactivex.Flowable
import kotlin.properties.Delegates

class MoreRankingViewModel:BaseViewModel() {

    var adapter : DelegateAdapter by Delegates.notNull()


    /**
     * 正版排行榜
     *
     * 起点/纵横/去起/若初/红薯/潇湘/逐浪
     */
    fun getMoreRankingList(gender:String, type:Int, pageNum:Int): Flowable<MoreRankingResult> {
        return mDataRepository.getMoreRankingList(gender, type, pageNum).compose(io_main())
    }
}