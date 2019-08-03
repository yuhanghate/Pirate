package com.yuhang.novel.pirate.viewholder

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemMainBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickItemMoreListener
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookDetailsDataResult
import java.text.SimpleDateFormat
import java.util.Date

class ItemMainVH(parent: ViewGroup) : BaseViewHolder<BookInfoKSEntity, ItemMainBinding>(
    parent, com.yuhang.novel.pirate.R.layout.item_main
) {

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(
        obj: BookInfoKSEntity,
        position: Int
    ) {
        super.onBindViewHolder(obj, position)

        /**
         * 加载原始数据
         */
        val time = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(obj.lastTime)
        mBinding.titleTv.text = obj.name
        mBinding.chapterTv.text = obj.lastChapterName
        mBinding.timeTv.text = time
        mBinding.imageIv.isLabelVisual = obj.isShowLabel
        /**
         * 加载头像
         */
        val drawable = mContext.getDrawable(com.yuhang.novel.pirate.R.drawable.ic_default_img)
        val placeholder =
            RequestOptions().transforms(CenterCrop(), RoundedCorners(mContext.niceDp2px(3f)))
                .placeholder(drawable)
                .error(drawable)
        getGlide().load("https://imgapi.jiaston.com/BookFiles/BookImages/${obj.cover}")
            .apply(placeholder)
            .into(mBinding.imageIv)

        mBinding.btnMore.setOnClickListener {
            getListener<OnClickItemMoreListener>()?.onClickItemMoreListener(
                it, position
            )
        }

    }
}