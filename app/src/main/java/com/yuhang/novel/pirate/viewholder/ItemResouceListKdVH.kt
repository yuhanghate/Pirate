package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.databinding.ItemResouceListKdBinding
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ResouceListKdResult
import org.joda.time.DateTime

class ItemResouceListKdVH(parent: ViewGroup) :
    BaseViewHolder<ResouceListKdResult, ItemResouceListKdBinding>(
        parent,
        R.layout.item_resouce_list_kd
    ) {

    override fun onBindViewHolder(obj: ResouceListKdResult, position: Int) {
        super.onBindViewHolder(obj, position)

        val dateTime = DateTime(obj.updated)
        mBinding.bookNameTv.text = obj.name
        mBinding.chapterNameTv.text = obj.lastChapter
        mBinding.timeTv.text = dateTime.toString("yyyy-MM-dd HH:mm:ss")
        mBinding.avatarTv.text = obj.name.substring(0, 1)

        mBinding.root.setBackgroundResource(BookConstant.getReadBookButton())
        mBinding.chapterNameTv.setTextColor(BookConstant.getPageTextColor())
    }
}