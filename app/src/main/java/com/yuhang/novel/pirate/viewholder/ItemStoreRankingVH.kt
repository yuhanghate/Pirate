package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemStoreRankingBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.ui.common.model.StoreRankingModel
import com.yuhang.novel.pirate.ui.store.adapter.StoreRankingChildAdapter


class ItemStoreRankingVH(parent: ViewGroup) :
    BaseViewHolder<List<StoreRankingModel>, ItemStoreRankingBinding>(
        parent,
        R.layout.item_store_ranking
    ) {


//    val pageMarginPx = PirateApp.getInstance().niceDp2px(20f)
//    val offsetPx = PirateApp.getInstance().niceDp2px(20f)
    override fun onBindViewHolder(obj: List<StoreRankingModel>, position: Int) {
        super.onBindViewHolder(obj, position)

        val adapter = StoreRankingChildAdapter()
            .setListener(getListener())
        adapter.initData(obj)


//        with(mBinding.viewPager) {
//            clipToPadding = false
//            clipChildren = false
//            offscreenPageLimit = 1
//        }

//        mBinding.viewPager.setPageTransformer { page, position ->
//            val viewPager = page.parent.parent as ViewPager2
////            val pos = if (position > 3) position - 2 else position
////            val offset = pos * -(2 * offsetPx)
//            if (viewPager.orientation == ORIENTATION_HORIZONTAL) {
//                if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
//                    page.translationX = -offsetPx + (position * 20)
//                } else {
//                    page.translationX = offsetPx + (position * 20)
//                }
//            } else {
//                page.translationY = offsetPx.toFloat()
//            }
//        }
        mBinding.viewPager.adapter = adapter

    }
}