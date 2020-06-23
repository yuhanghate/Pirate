package com.yuhang.novel.pirate.ui.ad.adapter

import android.view.ViewGroup
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameDataResult
import com.yuhang.novel.pirate.viewholder.ItemGameVH

class GameAdapter : BaseAdapter<GameDataResult>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<GameDataResult, *> {
        return ItemGameVH(parent)
    }

}