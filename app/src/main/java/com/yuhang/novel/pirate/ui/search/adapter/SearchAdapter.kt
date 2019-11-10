package com.yuhang.novel.pirate.ui.search.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookSearchDataResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.viewholder.ItemSearchVH

/**
 * 搜索小说
 */
class SearchAdapter:BaseAdapter<BooksResult>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BooksResult, ViewDataBinding> {
        return ItemSearchVH(parent)
    }

}