package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.ChapterSexDataResult
import com.yuhang.novel.pirate.viewholder.ItemSexVH

class BookSexAdapter:BaseAdapter<ChapterSexDataResult>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ChapterSexDataResult, ViewDataBinding> {
        return ItemSexVH(parent)
    }
}