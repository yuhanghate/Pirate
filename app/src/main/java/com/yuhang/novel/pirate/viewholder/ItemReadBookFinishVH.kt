package com.yuhang.novel.pirate.viewholder

import android.view.View
import android.view.ViewGroup
import com.yuhang.novel.pirate.R
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
        BaseViewHolder<BookContentKSEntity, ItemReadBookFinishBinding>(parent, R.layout.item_read_book_finish) {

    override fun onBindViewHolder(obj: BookContentKSEntity, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.loading.visibility = View.VISIBLE
        if (!obj.hasNext) {
            mBinding.loading.showContent()
        } else {
            mBinding.loading.setLoading(R.layout._loading_layout_loading2)
            mBinding.loading.showLoading()
        }

    }
}