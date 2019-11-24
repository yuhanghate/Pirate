package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemMoreRankingListBinding
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceCrossFade
import com.yuhang.novel.pirate.extension.niceDefaultImageVertical
import com.yuhang.novel.pirate.listener.OnClickMoreRankingListListener
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult

class ItemMoreRankingListVH(parent:ViewGroup):BaseViewHolder<BooksKSResult, ItemMoreRankingListBinding>(parent, R.layout.item_more_ranking_list) {

    override fun onBindViewHolder(obj: BooksKSResult, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.authorTv.text = obj.Author
        mBinding.nameTv.text = obj.Name
        mBinding.subTitleTv.text = "${obj.CName} | ${obj.BookStatus}"
        mBinding.scoreTv.text = "${obj.Score}分"
        mBinding.chapterTv.text = obj.LastChapter
        mBinding.descTv.text = "内容简介: ${obj.Desc}"

        getGlide().load(obj.Img.niceCoverPic())
            .apply(niceDefaultImageVertical())
            .transition(niceCrossFade())
            .into(mBinding.coverIv)

        mBinding.root.setOnClickListener { getListener<OnClickMoreRankingListListener>()?.onClickMoreRankingListListener(obj, position) }
    }
}