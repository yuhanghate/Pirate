package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.ui.common.model.StoreRankingModel
import com.yuhang.novel.pirate.viewholder.ItemStoreLineVH
import com.yuhang.novel.pirate.viewholder.ItemStoreRankingVH
import com.yuhang.novel.pirate.viewholder.ItemStoreTitleMoreVH
import com.yuhang.novel.pirate.viewholder.ItemStoreTitleNomoreVH

/**
 * 书城分隔线
 */
class StoreRankingAdapter : BaseAdapterV2<List<StoreRankingModel>>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<List<StoreRankingModel>, ViewDataBinding> {
        return ItemStoreRankingVH(parent)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateLayoutHelper(): LinearLayoutHelper {
        return LinearLayoutHelper()
    }

    override fun getItemViewType(position: Int): Int {
        return 7
    }
}