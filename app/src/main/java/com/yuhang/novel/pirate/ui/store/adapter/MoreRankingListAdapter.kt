package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.viewholder.ItemMoreRankingListVH

/**
 * 书城分隔线
 */
class MoreRankingListAdapter : BaseAdapterV2<BooksKSResult>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<BooksKSResult, *> {
        return ItemMoreRankingListVH(parent)
    }

    override fun getItemCount(): Int {
        return getList().size
    }

    override fun onCreateLayoutHelper(): LinearLayoutHelper {
        return LinearLayoutHelper()
    }

    override fun getItemViewType(position: Int): Int {
        return 10
    }
}