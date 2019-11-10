package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemSearchSuggestBinding
import com.yuhang.novel.pirate.listener.OnClickSearchSuggestListener
import com.yuhang.novel.pirate.repository.network.data.pirate.result.SearchSuggestResult

class ItemSearchSuggestVH(parent:ViewGroup):BaseViewHolder<SearchSuggestResult, ItemSearchSuggestBinding>(parent, R.layout.item_search_suggest) {

    override fun onBindViewHolder(obj: SearchSuggestResult, position: Int) {
        super.onBindViewHolder(obj, position)
        mBinding.contentTv.text = obj.text

        mBinding.root.setOnClickListener { getListener<OnClickSearchSuggestListener>()?.onClickSearchSuggestListener(position) }
    }
}