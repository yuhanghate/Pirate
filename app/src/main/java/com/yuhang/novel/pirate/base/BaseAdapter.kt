package com.yuhang.novel.pirate.base

import android.graphics.Color
import android.view.SoundEffectConstants
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.yuhang.novel.pirate.app.PirateApp
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.listener.OnClickItemLongListener
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
    private var mDecorationSize:Int  = 2

    /**
     * 分隔线颜色
     */
    private var mdecorationColor = Color.parseColor("#F2F2F2")

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

    fun setlayoutManager(layoutManager: RecyclerView.LayoutManager): BaseAdapter<T> {
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
        if (list.isEmpty()) return
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
    fun setRecyclerView(recyclerView: RecyclerView, isDecoration :Boolean = true): BaseAdapter<T> {
        val decoration = HorizontalDividerItemDecoration.Builder(recyclerView.context)
            .size(mDecorationSize)
            .color(mdecorationColor)
            .margin(mDecorationMargin, mDecorationMargin)
            .build()
        if (layoutManager == null) {
            layoutManager = WrapContentLinearLayoutManager(recyclerView.context)

        }

        if (layoutManager is WrapContentLinearLayoutManager) {
            (layoutManager as WrapContentLinearLayoutManager).orientation = RecyclerView.VERTICAL
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

    /**
     * 创建ViewHolder
     */
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, ViewDataBinding> {
//        if (viewTypeMap.size > 1) {
//            return createViewHolder(viewTypeMap[viewType]?: error("类型不匹配,请检测 ViewHolder类型是否对应 viewType "), parent)
//        } else {
//            //一个类型
//            val iterator = viewTypeMap.iterator()
//            while (iterator.hasNext()) {
//                return createViewHolder(iterator.next().value, parent)
//            }
//        }
//        return EmptyLayoutVH(parent)
//    }

    /**
     * 创建ViewHolder
     */
//    private fun createViewHolder(clazz:Class<BaseViewHolder<T, ViewDataBinding>>, parent: ViewGroup): BaseViewHolder<T, ViewDataBinding> {
//        val constructor = clazz.getDeclaredConstructor(ViewGroup::class.java)
//        constructor.isAccessible = true
//        return constructor.newInstance(parent)
//    }

//    /**
//     * 初始化一个ViewHolder
//     * 不能和ViewHolder列表
//     */
//    fun addViewHolder(clazz: Class<? : BaseViewHolder<T, *>>):BaseAdapter<T> {
//        viewTypeMap[0] = clazz
//        return this
//    }
//
//    /**
//     * 需要加载的ViewHolder列表
//     */
//    fun addViewHolder(viewType: Int, clazz: Class<BaseViewHolder<T, ViewDataBinding>>) :BaseAdapter<T>{
//
//        if (viewTypeMap.keys.contains(viewType)) {
//            error("viewType 不能重复,角标必须从0开始")
//        }
//        viewTypeMap[viewType] = clazz
//        return this
//    }

//    abstract fun initAdapter()


}