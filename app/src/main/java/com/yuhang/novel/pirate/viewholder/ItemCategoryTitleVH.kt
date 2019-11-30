package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemCategoryBinding
import com.yuhang.novel.pirate.databinding.ItemCategoryTitleBinding

class ItemCategoryTitleVH(parent:ViewGroup): BaseViewHolder<String, ItemCategoryTitleBinding>(parent, R.layout.item_category_title) {

    override fun onBindViewHolder(obj: String, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.nameTv.text = obj
    }
}