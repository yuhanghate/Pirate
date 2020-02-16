package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemStoreHeaderBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.listener.OnClickBooksListListener
import com.yuhang.novel.pirate.ui.store.activity.BooksListActivity

class ItemStoreHeaderVH(parent:ViewGroup):BaseViewHolder<String, ItemStoreHeaderBinding>(parent, R.layout.item_store_header) {

    override fun onBindViewHolder(obj: String, position: Int) {
        super.onBindViewHolder(obj, position)

        mBinding.newTv.clickWithTrigger { getListener<OnClickBooksListListener>()?.onClickBooksListListener(BooksListActivity.TYPE_NEW) }
        mBinding.hotTv.clickWithTrigger { getListener<OnClickBooksListListener>()?.onClickBooksListListener(BooksListActivity.TYPE_HOT) }
        mBinding.collectTv.clickWithTrigger { getListener<OnClickBooksListListener>()?.onClickBooksListListener(BooksListActivity.TYPE_COLLECT) }
        mBinding.recommendTv.clickWithTrigger { getListener<OnClickBooksListListener>()?.onClickBooksListListener(BooksListActivity.TYPE_RECOMMEND) }
    }
}