package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.ui.common.model.LineModel
import com.yuhang.novel.pirate.viewholder.ItemStoreLineVH

/**
 * 书城分隔线
 */
class StoreLineAdapter : BaseAdapterV2<LineModel>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<LineModel, ViewDataBinding> {
        return ItemStoreLineVH(parent)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateLayoutHelper(): LinearLayoutHelper {
        return LinearLayoutHelper()
    }

    override fun getItemViewType(position: Int): Int {
        return 3
    }
}