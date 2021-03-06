package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.ui.common.model.LineModel
import com.yuhang.novel.pirate.viewholder.ItemStoreBoldLineVH
import com.yuhang.novel.pirate.viewholder.ItemStoreLineVH

/**
 * 书城分隔线
 */
class StoreLineBoldAdapter : BaseAdapterV2<LineModel>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<LineModel, *> {
        return ItemStoreBoldLineVH(parent)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return LinearLayoutHelper()
    }

    override fun getItemViewType(position: Int): Int {
        return 9
    }
}