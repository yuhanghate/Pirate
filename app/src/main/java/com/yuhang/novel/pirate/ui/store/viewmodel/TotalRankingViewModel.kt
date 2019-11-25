package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.KanShuRankingResult
import io.reactivex.Flowable
import kotlin.properties.Delegates

class TotalRankingViewModel:BaseViewModel() {

    var adapter: DelegateAdapter by Delegates.notNull()


    /**
     * 看书神器 排行榜
     */
    fun getKanShuRanking(gender:String, type:String, date:String, pageNum:Int): Flowable<KanShuRankingResult> {
        return mDataRepository.getKanShuRankingList(gender, type, date, pageNum).compose(io_main())
    }
}