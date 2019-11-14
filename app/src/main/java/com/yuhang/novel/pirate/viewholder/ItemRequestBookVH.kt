package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemRequestBookBinding
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult

class ItemRequestBookVH(parent:ViewGroup):BaseViewHolder<BooksResult, ItemRequestBookBinding>(parent,R.layout.item_request_book) {
    override fun onBindViewHolder(obj: BooksResult, position: Int) {
        super.onBindViewHolder(obj, position)

    }
}