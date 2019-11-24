package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.viewholder.ItemStoreHeaderVH

/**
 * 最新发布/本周最热/最新收藏/小编推荐
 */
class StoreHeaderAdapter: BaseAdapterV2<String>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String, ViewDataBinding> {
        return ItemStoreHeaderVH(parent)
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        val helper = LinearLayoutHelper()
        helper.marginBottom = PirateApp.getInstance().niceDp2px(10f)
        return helper
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }
}