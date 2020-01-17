package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemSexBinding
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.ChapterSexDataResult

class ItemSexVH(parent:ViewGroup):BaseViewHolder<ChapterSexDataResult, ItemSexBinding>(parent, R.layout.item_sex) {

    override fun onBindViewHolder(obj: ChapterSexDataResult, position: Int) {
        super.onBindViewHolder(obj, position)
        mBinding.bookNameTv.text = obj.bookName
        mBinding.authorTv.text = " ${obj.author}"
        mBinding.chapterNameTv.text = "${obj.lastChapterName}"
        mBinding.descTv.text = obj.description
    }
}