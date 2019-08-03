package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.constant.BookKSConstant
import com.yuhang.novel.pirate.databinding.ItemReadBookBinding
import com.yuhang.novel.pirate.databinding.ItemReadBookFinishBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceSp2px
import com.yuhang.novel.pirate.listener.OnPageIndexListener
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import com.yuhang.novel.pirate.utils.StatusBarUtil


class ItemReadBookFinishVH(parent: ViewGroup) :
        BaseViewHolder<BookContentKSEntity, ItemReadBookFinishBinding>(parent, com.yuhang.novel.pirate.R.layout.item_read_book_finish) {

    override fun onBindViewHolder(obj: BookContentKSEntity, position: Int) {
        super.onBindViewHolder(obj, position)

        if (obj.nid == BookKSConstant.NOT_CHAPTER) {
            mBinding.loading.showContent()
        } else {
            mBinding.loading.showLoading()
        }

    }
}