package com.yuhang.novel.pirate.ui.resouce.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BookResouceListResult
import com.yuhang.novel.pirate.viewholder.ItemBookResouceVH

class ResouceAdapter:BaseAdapter<BookResouceListResult>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BookResouceListResult, ViewDataBinding> {
        return ItemBookResouceVH(parent)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BookResouceListResult, ViewDataBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder) {
            is ItemBookResouceVH -> holder.onBindViewHolder(getObj(position), position)
        }
    }
}