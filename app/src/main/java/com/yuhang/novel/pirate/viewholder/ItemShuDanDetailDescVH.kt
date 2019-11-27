package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemShudanDetailDescBinding
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.ShuDanDetailResult

class ItemShuDanDetailDescVH(parent:ViewGroup):BaseViewHolder<String, ItemShudanDetailDescBinding>(parent, R.layout.item_shudan_detail_desc) {

    override fun onBindViewHolder(obj: String, position: Int) {
        super.onBindViewHolder(obj, position)
        mBinding.descTv.text = obj
    }
}