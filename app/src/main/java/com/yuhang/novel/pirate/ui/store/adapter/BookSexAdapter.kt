package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.database.entity.SexBooksEntity
import com.yuhang.novel.pirate.viewholder.ItemSexVH

class BookSexAdapter:BaseAdapter<SexBooksEntity>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<SexBooksEntity, *> {
        return ItemSexVH(parent)
    }
}