package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemStoreRankingBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.ui.common.model.StoreRankingModel
import com.yuhang.novel.pirate.ui.store.adapter.StoreRankingChildAdapter
import kotlin.math.abs


class ItemStoreRankingVH(parent: ViewGroup) :
    BaseViewHolder<List<StoreRankingModel>, ItemStoreRankingBinding>(
        parent,
        R.layout.item_store_ranking
    ) {

    override fun onBindViewHolder(obj: List<StoreRankingModel>, position: Int) {
        super.onBindViewHolder(obj, position)

        val adapter = StoreRankingChildAdapter()
            .setListener(getListener())
        adapter.initData(obj)


        with(mBinding.viewPager) {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 1
        }
        val pageMarginPx = mContext.niceDp2px(20f)
        val offsetPx = mContext.niceDp2px(38f)
        mBinding.viewPager.setPageTransformer { page, position ->
            val viewPager = page.parent.parent as ViewPager2
            val offset = position * -(2 * offsetPx + pageMarginPx)
            if (viewPager.orientation == ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -offset
                } else {
                    page.translationX = offset
                }
            } else {
                page.translationY = offset
            }
        }
        mBinding.viewPager.adapter = adapter

    }
}