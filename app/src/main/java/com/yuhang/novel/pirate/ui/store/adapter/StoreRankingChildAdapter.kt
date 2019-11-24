package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.ui.common.model.StoreRankingModel
import com.yuhang.novel.pirate.viewholder.ItemStoreRankingChildVH


class StoreRankingChildAdapter:com.yuhang.novel.pirate.base.BaseAdapter<StoreRankingModel>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<StoreRankingModel, ViewDataBinding> {
        val vh = ItemStoreRankingChildVH(parent)
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        vh.mBinding.root.layoutParams = lp
        return vh
    }

}