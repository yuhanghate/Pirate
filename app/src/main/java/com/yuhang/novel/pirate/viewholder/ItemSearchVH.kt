package com.yuhang.novel.pirate.viewholder

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemSearchBinding
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceGlideInto
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookSearchDataResult

class ItemSearchVH(parent:ViewGroup):BaseViewHolder<BookSearchDataResult, ItemSearchBinding>(parent, R.layout.item_search) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(obj: BookSearchDataResult, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.descTv.text = obj.Desc
        mBinding.titleTv2.text = "${obj.CName} | ${obj.Author}"
        mBinding.titleTv.text = obj.Name

        /**
         * 加载头像
         */
        val drawable = mContext.getDrawable(R.drawable.ic_default_cover)
        val placeholder =
                RequestOptions().transforms(CenterCrop(), RoundedCorners(mContext.niceDp2px(3f)))
                        .placeholder(drawable)
                        .error(drawable)
        getGlide().load(niceCoverPic(obj.Img))
                .apply(placeholder)
                .into(niceGlideInto(mBinding.converIv))
    }
}