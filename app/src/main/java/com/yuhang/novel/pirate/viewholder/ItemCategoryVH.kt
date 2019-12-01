package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemCategoryBinding
import com.yuhang.novel.pirate.listener.OnClickCategoryListener
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity


class ItemCategoryVH(parent: ViewGroup) :
    BaseViewHolder<CategoryKDEntity, ItemCategoryBinding>(parent, R.layout.item_category) {

    override fun onBindViewHolder(obj: CategoryKDEntity, position: Int) {
        super.onBindViewHolder(obj, position)

        when {
            obj.count < 10000 -> {
                obj.count += 9875
            }
            obj.count < 100000 -> {
                obj.count += 67854
            }
            else -> {
                obj.count += 490987
            }
        }

        mBinding.nameTv.text = obj.majorCate
        mBinding.countTv.text = getBookCount(obj)

        mBinding.itemLl.setOnClickListener { getListener<OnClickCategoryListener>()?.onClickCategoryListener(obj, position) }


    }

    private fun getBookCount(obj: CategoryKDEntity): String {
        return when {
            obj.count < 10000 -> {
                "(${obj.count}本)"
            }
            obj.count < 1000000 -> {
                "(${obj.count / 10000}万本)"
            }
            else -> {
                "(${obj.count / 1000000}万本)"
            }
        }
    }
}