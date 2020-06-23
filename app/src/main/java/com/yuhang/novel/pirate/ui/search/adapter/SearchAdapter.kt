package com.yuhang.novel.pirate.ui.search.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.viewholder.ItemRequestBookVH
import com.yuhang.novel.pirate.viewholder.ItemSearchVH

/**
 * 搜索小说
 */
class SearchAdapter : BaseAdapter<BooksResult>() {
    companion object {
        //求书
        const val TYPE_REQUEST_BOOK = 0

        //结果页
        const val TYPE_RESULT = 1
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<BooksResult, *> {
        if (viewType == TYPE_REQUEST_BOOK) {
            return ItemRequestBookVH(parent)
        }
        return ItemSearchVH(parent)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_REQUEST_BOOK
        }
        return TYPE_RESULT
    }

}