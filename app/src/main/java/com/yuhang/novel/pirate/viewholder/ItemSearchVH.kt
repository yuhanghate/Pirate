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
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult

class ItemSearchVH(parent:ViewGroup):BaseViewHolder<BooksResult, ItemSearchBinding>(parent, R.layout.item_search) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(obj: BooksResult, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.descTv.text = obj.description
        mBinding.titleTv2.text = "${obj.kind} | ${obj.author}"
        mBinding.titleTv.text = obj.bookName

        /**
         * 加载头像
         */
        val drawable = mContext.getDrawable(R.drawable.ic_default_cover)
        val placeholder =
                RequestOptions().transforms(CenterCrop(), RoundedCorners(mContext.niceDp2px(3f)))
                        .placeholder(drawable)
                        .error(drawable)
        getGlide().load(obj.cover)
                .apply(placeholder)
                .into(niceGlideInto(mBinding.converIv))
    }
}