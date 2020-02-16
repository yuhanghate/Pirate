package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemStoreMoreRankingBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.listener.OnClickMoreRankingListener
import com.yuhang.novel.pirate.ui.common.model.RankingModel

class ItemStoreMoreRankingVH(parent:ViewGroup):BaseViewHolder<RankingModel, ItemStoreMoreRankingBinding>(parent, R.layout.item_store_more_ranking) {

    override fun onBindViewHolder(obj: RankingModel, position: Int) {
        super.onBindViewHolder(obj, position)
        mBinding.nameTv.text = obj.name

        mBinding.root.clickWithTrigger { getListener<OnClickMoreRankingListener>()?.onClickMoreRankingListener(obj, position) }
    }
}