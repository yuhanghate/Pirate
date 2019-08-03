package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemDrawerlayoutLeftBinding
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity

class ItemDrawerLayoutLeftVH(parent:ViewGroup):BaseViewHolder<BookChapterKSEntity, ItemDrawerlayoutLeftBinding>(parent, R.layout.item_drawerlayout_left) {
    var chapterid = -1
    override fun onBindViewHolder(obj: BookChapterKSEntity, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.chapterNameTv.text = obj.name

        /**
         * 如果是最后一次点击的item
         */
        if (chapterid == obj.chapterId) {
            mBinding.chapterNameTv.setTextColor(ContextCompat.getColor(mContext,R.color.primary))
        } else {
            mBinding.chapterNameTv.setTextColor(ContextCompat.getColor(mContext, R.color.primary_text))
        }
    }
}