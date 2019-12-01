package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.ui.common.model.RankingModel
import com.yuhang.novel.pirate.viewholder.ItemStoreMoreRankingVH

class StoreMoreRankingAdapter:BaseAdapterV2<RankingModel>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<RankingModel, ViewDataBinding> {
        return ItemStoreMoreRankingVH(parent)
    }

    override fun getItemCount(): Int {
        return getList().size
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return LinearLayoutHelper()
    }

    override fun getItemViewType(position: Int): Int {
        return 8
    }
}