package com.yuhang.novel.pirate.ui.book.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.viewholder.ItemReadHistoryVH

class ReadHistoryAdapter:BaseAdapter<BookInfoKSEntity>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BookInfoKSEntity, ViewDataBinding> {

        return ItemReadHistoryVH(parent)
    }
}