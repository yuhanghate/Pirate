package com.yuhang.novel.pirate.ui.main.adapter

import android.view.ViewGroup
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.viewholder.ItemMainVH

/**
 * 主页Adapter
 */
class MainAdapter:BaseAdapter<BookInfoKSEntity>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BookInfoKSEntity, *> {
        return ItemMainVH(parent)
    }
}