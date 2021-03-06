package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemReadHistoryBinding
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceGlideInto
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.database.entity.BookReadHistoryEntity

class ItemReadHistoryVH(parent:ViewGroup):BaseViewHolder<BookInfoKSEntity, ItemReadHistoryBinding>(parent, R.layout.item_read_history) {

    override fun onBindViewHolder(obj: BookInfoKSEntity, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.titleTv.text  = obj.bookName
        mBinding.descTv.text = obj.description
        mBinding.authorTv.text = "${obj.author}"
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