package com.yuhang.novel.pirate.viewholder

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemVlayoutColumn3Binding
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceGlideInto
import com.yuhang.novel.pirate.listener.OnClickBookListener
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult

class ItemVlayoutColumn3VH(parent: ViewGroup) :
    BaseViewHolder<BooksKSResult, ItemVlayoutColumn3Binding>(
        parent,
        R.layout.item_vlayout_column3
    ) {

    override fun onBindViewHolder(obj: BooksKSResult, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.nameTv.text = obj.Name
        mBinding.scoreTv.text = "${obj.Score}分"
        mBinding.kindTv.text = obj.CName

        val drawable = mContext.getDrawable(R.drawable.ic_default_img2)
        val placeholder =
            RequestOptions().transforms(CenterCrop(), RoundedCorners(mContext.niceDp2px(3f)))
                .placeholder(drawable)
                .error(drawable)
        getGlide().load(niceCoverPic(obj.Img))
            .apply(placeholder)
            .into(niceGlideInto(mBinding.coverIv))

        mBinding.root.setOnClickListener { clickBook(it, position, obj) }
        mBinding.coverIv.setOnClickListener { clickBook(it, position, obj) }
    }

    private fun clickBook(view: View, position: Int, obj: BooksKSResult) {
        getListener<OnClickBookListener>()?.onClickBookListener(view, obj, position)

    }
}