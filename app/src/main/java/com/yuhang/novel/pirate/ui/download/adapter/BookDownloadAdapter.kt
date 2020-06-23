package com.yuhang.novel.pirate.ui.download.adapter

import android.view.ViewGroup
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity
import com.yuhang.novel.pirate.viewholder.ItemBookDownloadVH

/**
 * 下载
 */
class BookDownloadAdapter:BaseAdapter<BookDownloadEntity>() {
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): BaseViewHolder<BookDownloadEntity, *> {
        return ItemBookDownloadVH(parent)
    }

}