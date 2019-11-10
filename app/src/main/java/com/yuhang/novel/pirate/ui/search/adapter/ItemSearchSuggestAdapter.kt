package com.yuhang.novel.pirate.ui.search.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.network.data.pirate.result.SearchSuggestResult
import com.yuhang.novel.pirate.viewholder.ItemSearchSuggestVH

class ItemSearchSuggestAdapter:BaseAdapter<SearchSuggestResult>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<SearchSuggestResult, ViewDataBinding> {
        return ItemSearchSuggestVH(parent)
    }
}