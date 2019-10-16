package com.yuhang.novel.pirate.viewholder

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemBookResouceBinding
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BookResouceListResult
import java.text.SimpleDateFormat
import java.util.*


class ItemBookResouceVH(parent: ViewGroup) : BaseViewHolder<BookResouceListResult, ItemBookResouceBinding>(parent, R.layout.item_book_resouce) {

    override fun onBindViewHolder(obj: BookResouceListResult, position: Int) {
        super.onBindViewHolder(obj, position)
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        mBinding.titleTv.text = obj.title
        mBinding.heatTv.text = "热度: ${obj.heat}"
        mBinding.urlTv.text = "地址: ${obj.websiteUrl}"
        mBinding.statusTv.text = "${obj.status}"
        mBinding.timeTv.text = "${sdf.format(Date(obj.updateTime!!))}"
        mBinding.checkbox.isChecked = obj.isCheck == 1

        if (obj.status == "运行正常") {
            mBinding.statusTv.setTextColor(ContextCompat.getColor(mContext, R.color.secondary_text))
            mBinding.statusTv.visibility = View.VISIBLE
        } else {
            mBinding.statusTv.setTextColor(ContextCompat.getColor(mContext, R.color.md_red_500))
            mBinding.statusTv.visibility = View.VISIBLE
        }
    }
}