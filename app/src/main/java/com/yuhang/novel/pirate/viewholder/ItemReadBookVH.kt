package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.databinding.ItemReadBookBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceSp2px
import com.yuhang.novel.pirate.listener.OnPageIndexListener
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import com.yuhang.novel.pirate.ui.book.adapter.ReadBookAdapter
import com.yuhang.novel.pirate.utils.StatusBarUtil


class ItemReadBookVH(parent: ViewGroup) :
        BaseViewHolder<BookContentKSEntity, ItemReadBookBinding>(parent, com.yuhang.novel.pirate.R.layout.item_read_book) {


    override fun onBindViewHolder(obj: BookContentKSEntity, position: Int) {
        super.onBindViewHolder(obj, position)
        mBinding.contentTv.position = position
        mBinding.contentTv.onClickNextListener = getListener()
        mBinding.contentTv.onClickPreviousListener = getListener()
        mBinding.contentTv.onClickCenterListener = getListener()


        val margin = mContext.niceDp2px(20f)
        mBinding.contentTv.textSize = BookConstant.getPageTextSize()
        mBinding.contentTv.setTitle(obj.textPageBean?.chapterName)
                .setPageTextColor(BookConstant.getPageTextColor())
                .setBattery(ReadBookAdapter.mBatteryLevel)
                .setMargin(margin, margin, 0, 0)
                .setTextPageBean(obj.textPageBean)
                .init()

    }
}