package com.yuhang.novel.pirate.ui.book.adapter

import android.view.ViewGroup
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity
import com.yuhang.novel.pirate.viewholder.ItemDrawerLayoutLeftVH

class DrawerlayoutLeftAdapter:BaseAdapter<BookChapterKSEntity>() {

    /**
     * 当前查看的章节内容
     */
    var chapterid = ""
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<BookChapterKSEntity, *> {
        return ItemDrawerLayoutLeftVH(parent)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BookChapterKSEntity, *>, position: Int) {
        if (holder is ItemDrawerLayoutLeftVH) {
            holder.chapterid = chapterid
        }
        super.onBindViewHolder(holder, position)

    }
}