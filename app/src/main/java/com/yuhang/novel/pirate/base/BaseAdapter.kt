package com.yuhang.novel.pirate.base

import android.view.SoundEffectConstants
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.listener.OnClickItemLongListener
import com.yuhang.novel.pirate.utils.AppManagerUtils
import com.yuhang.novel.pirate.widget.WrapContentLinearLayoutManager

abstract class BaseAdapter<T : Any> : RecyclerView.Adapter<BaseViewHolder<T, ViewDataBinding>>() {

    var mListener: Any? = null

    /**
     * 最后一次点击item角标
     */
    var lastClickItemPosition = -1
    /**
     * 分隔线高度
     */
    private var mDecorationSize: Int = 2

    /**
     * 分隔线颜色
     */
    @ColorInt
    private var mdecorationColor = -1

    /**
     * 分隔线左右间距
     */
    private var mDecorationMargin = 20

    /**
     * 横向/竖向
     */
    private var orientation = RecyclerView.VERTICAL

    /**
     * 管理模式
     */
    private var layoutManager: RecyclerView.LayoutManager? = null
    /**
     * 数据集
     */
    private var mList: ArrayList<T> = arrayListOf()

    /**
     * 获取列表
     */
    fun getList() = mList


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setListener(listener: Any?): BaseAdapter<T> {
        mListener = listener
        return this
    }

    fun setDecorationSize(size: Int): BaseAdapter<T> {
        mDecorationSize = size
        return this
    }

    fun setDecorationColor(color: Int): BaseAdapter<T> {
        mdecorationColor = color
        return this
    }

    fun setDecorationMargin(margin: Float): BaseAdapter<T> {
        mDecorationMargin = PirateApp.getInstance().niceDp2px(margin)
        return this
    }

    fun setorientation(orientation: Float): BaseAdapter<T> {
        this.orientation = PirateApp.getInstance().niceDp2px(orientation)
        return this
    }

    fun setlayoutManager(layoutManager: RecyclerView.LayoutManager?): BaseAdapter<T> {
        this.layoutManager = layoutManager
        return this
    }

    /**
     * 加载更多
     */
    fun loadMore(list: List<T>) {
        if (list.isEmpty()) return
        val startPosition = mList.size
        mList.addAll(list)
        notifyItemRangeInserted(startPosition, mList.size)
        notifyItemChanged(startPosition - 1)
    }

    /**
     * 下拉刷新
     */
    fun setRefersh(list: List<T>) {
//        if (list.isEmpty()) return
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * 初始化数据
     */
    open fun initData(list: List<T>) {
        mList.addAll(list)
    }

    open fun getObj(position: Int): T {
        return getList()[position]
    }


    /**
     * 设置RecyclerView
     */
    fun setRecyclerView(recyclerView: RecyclerView, isDecoration: Boolean = true): BaseAdapter<T> {
        if (mdecorationColor <= 0) {
            mdecorationColor = ContextCompat.getColor(recyclerView.context, R.color.list_divider_color)
        }
        val decoration = HorizontalDividerItemDecoration.Builder(recyclerView.context)
                .size(mDecorationSize)
                .color(mdecorationColor)
                .margin(mDecorationMargin, mDecorationMargin)
                .build()
        if (layoutManager == null) {
            layoutManager = WrapContentLinearLayoutManager(recyclerView.context)

        }

        if (layoutManager is LinearLayoutManager) {
            (layoutManager as LinearLayoutManager).orientation = RecyclerView.VERTICAL
        }


        initData(arrayListOf())
        if (isDecoration) {
            recyclerView.addItemDecoration(decoration!!)
        }

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = this
        return this
    }


    /**
     * 绑定ViewHolder
     */
    override fun onBindViewHolder(holder: BaseViewHolder<T, ViewDataBinding>, position: Int) {
        //设置Item点击事件
        holder.itemView.setOnClickListener {
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
        holder.setListener(mListener).onBindViewHolder(getObj(position), position)


    }


}