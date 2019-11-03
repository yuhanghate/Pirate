package com.yuhang.novel.pirate.ui.resouce.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.repository.network.data.kuaidu.result.ResouceListKdResult
import com.yuhang.novel.pirate.viewholder.ItemResouceListKdVH

class ResouceListKdAdapter:BaseAdapter<ResouceListKdResult>() {
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): BaseViewHolder<ResouceListKdResult, ViewDataBinding> {
        return ItemResouceListKdVH(parent)
    }
}