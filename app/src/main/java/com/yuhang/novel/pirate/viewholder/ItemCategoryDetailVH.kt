package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemCategoryDetailBinding
import com.yuhang.novel.pirate.extension.niceAvatarUrl
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceCrossFade
import com.yuhang.novel.pirate.extension.niceDefaultImageVertical
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.SearchDataKdResult

class ItemCategoryDetailVH(parent: ViewGroup) :
    BaseViewHolder<SearchDataKdResult, ItemCategoryDetailBinding>(
        parent,
        R.layout.item_category_detail
    ) {

    override fun onBindViewHolder(obj: SearchDataKdResult, position: Int) {
        super.onBindViewHolder(obj, position)
        mBinding.nameTv.text = obj.title
        mBinding.authorTv.text = "${obj.author}|${obj.cat}"
        mBinding.descTv.text = obj.longIntro
        mBinding.countTv.text = "好评率: ${obj.retentionRatio}%"

        getGlide().load(obj.cover.niceCoverPic())
            .transition(niceCrossFade())
            .apply(niceDefaultImageVertical())
            .into(mBinding.coverIv)
    }
}