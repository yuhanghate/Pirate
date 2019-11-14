package com.yuhang.novel.pirate.viewholder

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemBookDownloadBinding
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceGlideInto
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity

class ItemBookDownloadVH(parent: ViewGroup) :
    BaseViewHolder<BookDownloadEntity, ItemBookDownloadBinding>(
        parent,
        R.layout.item_book_download
    ) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(obj: BookDownloadEntity, position: Int) {
        super.onBindViewHolder(obj, position)
        val progress = (obj.progress.toDouble() / obj.total.toDouble() * 100).toInt() + 1
        mBinding.titleTv.text = obj.bookName
        mBinding.progressHorizontal.progress = progress
        mBinding.progressTv.text = "${obj.progress}/${obj.total}"
        mBinding.authorTv.text = obj.author

        /**
         * 加载头像
         */
        val drawable = mContext.getDrawable(R.drawable.ic_default_img)
        val placeholder =
            RequestOptions().transforms(CenterCrop(), RoundedCorners(mContext.niceDp2px(3f)))
                .placeholder(drawable)
                .error(drawable)

        getGlide().load(niceCoverPic(obj.cover))
            .apply(placeholder)
            .into(niceGlideInto(mBinding.coverIv))
    }
}