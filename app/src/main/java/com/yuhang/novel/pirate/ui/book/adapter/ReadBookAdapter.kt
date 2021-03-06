package com.yuhang.novel.pirate.ui.book.adapter

import android.view.SoundEffectConstants
import android.view.ViewGroup
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.listener.OnClickItemLongListener
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import com.yuhang.novel.pirate.viewholder.ItemReadBookFinishVH
import com.yuhang.novel.pirate.viewholder.ItemReadBookVH

class ReadBookAdapter : BaseAdapter<BookContentKSEntity>() {



    companion object {
        /**
         * 内容
         */
        const val TYPE_CONTENT = 0

        /**
         * 完结
         */
        const val TYPE_FINISH = 1

        /**
         * 电池电量
         */
        var mBatteryLevel = 100
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<BookContentKSEntity, *> {
        return if (viewType == TYPE_CONTENT) {
            ItemReadBookVH(parent)
        } else {
            ItemReadBookFinishVH(parent)
        }

    }


    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            //最后一页显示,读完全部章节
            TYPE_FINISH
        } else {
            TYPE_CONTENT
        }

    }

    override fun getItemCount(): Int {
        return getList().size + 1
    }


    override fun onBindViewHolder(holder: BaseViewHolder<BookContentKSEntity, *>, position: Int) {
        //设置Item点击事件
        holder.itemView.clickWithTrigger {
            lastClickItemPosition = position

            (mListener as? OnClickItemListener)?.onClickItemListener(it, position)
        }

        //长按事件
        holder.itemView.setOnLongClickListener {
            it.playSoundEffect(SoundEffectConstants.CLICK)
            (mListener as? OnClickItemLongListener)?.onClickItemLongListener(it, position)
            true
        }

        holder.lastClickItemPosition = lastClickItemPosition
        //绑定View
        when (holder) {
            is ItemReadBookVH -> holder.setListener(mListener).onBindViewHolder(
                getObj(position), position
            )
            is ItemReadBookFinishVH -> holder.setListener(mListener).onBindViewHolder(getObj(position - 1), position)
        }


    }
}