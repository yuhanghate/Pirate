package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemStoreTitleMoreBinding
import com.yuhang.novel.pirate.databinding.ItemStoreTitleNomoreBinding
import com.yuhang.novel.pirate.listener.OnClickItemStoreTitleMoreListener

class ItemStoreTitleMoreVH(parent:ViewGroup):BaseViewHolder<String, ItemStoreTitleMoreBinding>(parent, R.layout.item_store_title_more) {

    override fun onBindViewHolder(obj: String, position: Int) {
        super.onBindViewHolder(obj, position)
        mBinding.titleTv.text = obj

        mBinding.moreLl.setOnClickListener { getListener<OnClickItemStoreTitleMoreListener>()?.onClickItemStoreTitleMoreListener(it, obj, position) }
    }
}