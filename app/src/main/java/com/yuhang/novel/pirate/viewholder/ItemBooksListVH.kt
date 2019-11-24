package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemBooksListBinding
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceCrossFade
import com.yuhang.novel.pirate.extension.niceDefaultImageVertical
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksListDataResult

class ItemBooksListVH(parent:ViewGroup):BaseViewHolder<BooksListDataResult, ItemBooksListBinding>(parent, R.layout.item_books_list) {

    override fun onBindViewHolder(obj: BooksListDataResult, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.nameTv.text = obj.Title
        mBinding.descTv.text = obj.Description
        mBinding.bookCountTv.text = "收藏${obj.BookCount}本"

        getGlide().load(obj.Cover.niceCoverPic())
            .apply(niceDefaultImageVertical())
            .transition(niceCrossFade())
            .into(mBinding.coverIv)
    }
}