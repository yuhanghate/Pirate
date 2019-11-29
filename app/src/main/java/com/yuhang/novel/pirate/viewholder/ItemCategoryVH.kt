package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemCategoryBinding
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

        mBinding.itemLl.setOnClickListener { getListener<OnClickItemListener>()?.onClickItemListener(it, position) }

        //获取outline
//        val vop: ViewOutlineProvider = object : ViewOutlineProvider() {
//            override fun getOutline(view: View, outline: Outline) { //修改outline
//                outline.setOval(0, 0, view.width, view.height)
//            }
//        }
        //重新设置
//        mBinding.avatarIv.outlineProvider = vop
//        getGlide().load(obj.toCover()[0].niceCoverPic())
//            .transition(niceCrossFade())
//            .apply(niceDefaultAvatar())
//            .into(mBinding.avatarIv)

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