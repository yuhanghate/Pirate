package com.yuhang.novel.pirate.ui.common.adapter

import android.view.ViewGroup
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.viewholder.ItemVlayoutColumn3VH

/**
 * Grid 3列
 */
class VlayoutColumn3Adapter:BaseAdapterV2<BooksKSResult>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BooksKSResult, *> {
        return ItemVlayoutColumn3VH(parent)
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        val margin = PirateApp.getInstance().niceDp2px(15f)
        val dp5 = PirateApp.getInstance().niceDp2px(10f)
        val layoutHelper = GridLayoutHelper(3)
        layoutHelper.setAutoExpand(false)
        layoutHelper.setMargin(margin, 0, margin, margin * 2)
        layoutHelper.hGap = dp5
        layoutHelper.vGap = dp5
        return layoutHelper
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }
}