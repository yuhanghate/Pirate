package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.ui.common.model.TitleNomoreModel
import com.yuhang.novel.pirate.viewholder.ItemStoreLineVH
import com.yuhang.novel.pirate.viewholder.ItemStoreTitleNomoreVH

/**
 * 书城分隔线
 */
class StoreTitleNomoreAdapter : BaseAdapterV2<TitleNomoreModel>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<TitleNomoreModel, ViewDataBinding> {
        return ItemStoreTitleNomoreVH(parent)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        val helper = LinearLayoutHelper()
        helper.marginBottom = PirateApp.getInstance().niceDp2px(10f)
        helper.marginTop = PirateApp.getInstance().niceDp2px(20f)
        return helper
    }

    override fun getItemViewType(position: Int): Int {
        return 6
    }
}