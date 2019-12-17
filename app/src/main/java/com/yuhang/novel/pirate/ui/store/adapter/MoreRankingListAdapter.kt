package com.yuhang.novel.pirate.ui.store.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.yuhang.novel.pirate.base.BaseAdapterV2
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksListDataResult
import com.yuhang.novel.pirate.ui.common.model.LineModel
import com.yuhang.novel.pirate.viewholder.ItemBooksListVH
import com.yuhang.novel.pirate.viewholder.ItemMoreRankingListVH
import com.yuhang.novel.pirate.viewholder.ItemStoreBoldLineVH
import com.yuhang.novel.pirate.viewholder.ItemStoreLineVH

/**
 * 书城分隔线
 */
class MoreRankingListAdapter : BaseAdapterV2<BooksKSResult>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<BooksKSResult, ViewDataBinding> {
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