package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.database.entity.ShuDanEntity
import com.yuhang.novel.pirate.viewholder.ItemBooksListVH

/**
 * 书城分隔线
 */
class BooksListAdapter : BaseAdapterV2<ShuDanEntity>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ShuDanEntity, *> {
        return ItemBooksListVH(parent)
    }

    override fun getItemCount(): Int {
        return getList().size
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return LinearLayoutHelper()
    }

    override fun getItemViewType(position: Int): Int {
        return 10
    }
}