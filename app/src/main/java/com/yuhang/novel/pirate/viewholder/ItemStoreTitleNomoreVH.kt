package com.yuhang.novel.pirate.viewholder

import android.view.View
import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemStoreTitleNomoreBinding
import com.yuhang.novel.pirate.ui.common.model.TitleNomoreModel

class ItemStoreTitleNomoreVH(parent: ViewGroup) :
    BaseViewHolder<TitleNomoreModel, ItemStoreTitleNomoreBinding>(
        parent,
        R.layout.item_store_title_nomore
    ) {

    override fun onBindViewHolder(obj: TitleNomoreModel, position: Int) {
        super.onBindViewHolder(obj, position)
        mBinding.titleTv.text = obj.name

        if (obj.isShowIcon) {
            mBinding.iconIv.visibility = View.VISIBLE
            mBinding.iconIv.setImageResource(obj.icon)
        } else {
            mBinding.iconIv.visibility = View.GONE
        }

    }
}