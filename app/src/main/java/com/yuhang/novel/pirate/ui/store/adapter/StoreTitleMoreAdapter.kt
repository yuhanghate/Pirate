package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.viewholder.ItemStoreTitleMoreVH

/**
 * 书城分隔线
 */
class StoreTitleMoreAdapter : BaseAdapterV2<String>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<String, *> {
        return ItemStoreTitleMoreVH(parent)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateLayoutHelper(): LinearLayoutHelper {
        return LinearLayoutHelper()
    }

    override fun getItemViewType(position: Int): Int {
        return 5
    }
}