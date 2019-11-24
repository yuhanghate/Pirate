package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemStoreLineBinding
import com.yuhang.novel.pirate.ui.common.model.LineModel

class ItemStoreLineVH(parent:ViewGroup):BaseViewHolder<LineModel, ItemStoreLineBinding>(parent, R.layout.item_store_line) {

    override fun onBindViewHolder(obj: LineModel, position: Int) {
        super.onBindViewHolder(obj, position)

        val top = mBinding.topV.layoutParams
        top.height = obj.top
        mBinding.topV.layoutParams = top
        mBinding.topV.invalidate()


        val button = mBinding.buttonV.layoutParams
        button.height = obj.bottom
        mBinding.buttonV.layoutParams = button
        mBinding.buttonV.invalidate()

        val start = mBinding.startV.layoutParams
        start.width = obj.start
        mBinding.startV.layoutParams = start
        mBinding.startV.invalidate()

        val end = mBinding.endV.layoutParams
        end.width = obj.end
        mBinding.endV.layoutParams = end
        mBinding.endV.invalidate()

        val line = mBinding.lineV.layoutParams
        line.height = obj.size
        mBinding.lineV.layoutParams = line
        mBinding.lineV.invalidate()


    }


}