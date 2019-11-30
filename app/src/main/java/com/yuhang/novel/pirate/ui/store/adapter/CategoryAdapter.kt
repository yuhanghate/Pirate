package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity
import com.yuhang.novel.pirate.viewholder.ItemCategoryVH

class CategoryAdapter : BaseAdapterV2<CategoryKDEntity>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<CategoryKDEntity, ViewDataBinding> {
        return ItemCategoryVH(parent)
    }

    override fun getItemCount(): Int {
        return getList().size
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        val margin = PirateApp.getInstance().niceDp2px(20f)
        val dp10 = PirateApp.getInstance().niceDp2px(10f)
        val dp15 = PirateApp.getInstance().niceDp2px(15f)
        val layoutHelper = GridLayoutHelper(4)
        layoutHelper.setAutoExpand(false)
        layoutHelper.setMargin(margin, 0, margin, margin * 2)
        layoutHelper.hGap = dp10
        layoutHelper.vGap = dp15
        return layoutHelper
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }
}