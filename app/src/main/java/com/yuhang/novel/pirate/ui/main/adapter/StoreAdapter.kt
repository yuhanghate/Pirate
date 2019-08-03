package com.yuhang.novel.pirate.ui.main.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookCategoryDataResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.RankingDataListResult
import com.yuhang.novel.pirate.viewholder.ItemStoreVH

class StoreAdapter: BaseAdapter<RankingDataListResult>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<RankingDataListResult, ViewDataBinding> {
        return ItemStoreVH(parent)
    }
}