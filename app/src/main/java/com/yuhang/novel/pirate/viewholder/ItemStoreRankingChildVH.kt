package com.yuhang.novel.pirate.viewholder

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemStoreRankingChildBinding
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceCrossFade
import com.yuhang.novel.pirate.extension.niceDefaultImageVertical
import com.yuhang.novel.pirate.extension.niceGlideInto
import com.yuhang.novel.pirate.listener.OnClickBookListener
import com.yuhang.novel.pirate.listener.OnClickItemStoreTitleMoreListener
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.ui.common.model.StoreRankingModel


class ItemStoreRankingChildVH(parent:ViewGroup):BaseViewHolder<StoreRankingModel, ItemStoreRankingChildBinding>(parent, R.layout.item_store_ranking_child) {

    override fun onBindViewHolder(obj: StoreRankingModel, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.titleTv.text = obj.name
        mBinding.descTv.text = obj.desc

        mBinding.name1Tv.text = obj.list[0].Name
        mBinding.name2Tv.text = obj.list[1].Name
        mBinding.name3Tv.text = obj.list[2].Name

        loadCover(obj.list[0], mBinding.cover1Iv)
        loadCover(obj.list[1], mBinding.cover2Iv)
        loadCover(obj.list[2], mBinding.cover3Iv)

        mBinding.headerLl.setBackgroundResource(obj.background)

        mBinding.item1Cl.setOnClickListener { clickBook(it, position, obj.list[0]) }
        mBinding.item2Cl.setOnClickListener { clickBook(it, position, obj.list[1]) }
        mBinding.item3Cl.setOnClickListener { clickBook(it, position, obj.list[2]) }

        mBinding.cover1Iv.setOnClickListener { clickBook(it, position, obj.list[0]) }
        mBinding.cover2Iv.setOnClickListener { clickBook(it, position, obj.list[1]) }
        mBinding.cover3Iv.setOnClickListener { clickBook(it, position, obj.list[2]) }

        mBinding.moreLl.setOnClickListener {
            getListener<OnClickItemStoreTitleMoreListener>()?.onClickItemStoreTitleMoreListener(itemView, obj.name, position)
        }

    }

    private fun clickBook(view: View, position: Int,  obj:BooksKSResult) {
        getListener<OnClickBookListener>()?.onClickBookListener(view, obj, position)



    }

    private fun loadCover(obj: BooksKSResult, image:ImageView) {
        getGlide().load(niceCoverPic(obj.Img))
            .apply(niceDefaultImageVertical())
            .transition(niceCrossFade())
            .into(niceGlideInto(image))
    }
}