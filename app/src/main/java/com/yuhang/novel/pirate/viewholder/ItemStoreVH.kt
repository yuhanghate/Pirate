package com.yuhang.novel.pirate.viewholder

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemSotreBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceGlideInto
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookCategoryDataResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.RankingDataListResult

class ItemStoreVH(parent:ViewGroup):BaseViewHolder<RankingDataListResult, ItemSotreBinding>(parent, R.layout.item_sotre) {

    override fun onBindViewHolder(obj: RankingDataListResult, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.titleTv.text  = obj.Name
        mBinding.descTv.text = obj.Desc
        mBinding.authorTv.text = "${obj.Author} | ${obj.CName}"
        /**
         * 加载头像
         */
        val drawable = mContext.getDrawable(R.drawable.ic_default_img)
        val placeholder =
                RequestOptions().transforms(CenterCrop(), RoundedCorners(mContext.niceDp2px(3f)))
                        .placeholder(drawable)
                        .error(drawable)

        getGlide().load("https://imgapi.jiaston.com/BookFiles/BookImages/${obj.Img}")
                .apply(placeholder)
                .into(niceGlideInto(mBinding.coverIv))
    }
}