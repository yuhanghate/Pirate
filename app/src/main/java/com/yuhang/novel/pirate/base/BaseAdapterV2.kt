package com.yuhang.novel.pirate.base

import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.yuhang.novel.pirate.base.BaseAdapter
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.listener.OnClickItemListener

abstract class BaseAdapterV2<T : Any> : DelegateAdapter.Adapter<BaseViewHolder<T, ViewDataBinding>>() {

    /**
     * 点击事件
     */
    var mListener: Any? = null

    /**
     * 数据集
     */
    private var mList: ArrayList<T> = arrayListOf()

    var count: Int = 0

    /**
     * 布局
     */
    var layoutHelper: LayoutHelper = LinearLayoutHelper()

    /**
     * 尺寸参数
     */
    var layoutParams: ViewGroup.LayoutParams =
            VirtualLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    /**
     * 获取列表
     */
    fun getList() = mList

    /**
     * 初始化数据
     */
    open fun initData(list: List<T>):BaseAdapterV2<T> {
        mList.addAll(list)
        return this
    }

    fun setRefresh(list: List<T>):BaseAdapterV2<T> {
        mList.clear()
        mList.addAll(list)
        return this
    }

    fun setMore(list: List<T>): BaseAdapterV2<T> {
        mList.addAll(list)
        return this
    }

    /**
     * 初始化数据
     */
    open fun initData(obj: T):BaseAdapterV2<T> {
        mList.addAll(arrayListOf(obj))
        return this
    }

    open fun getObj(position: Int): T {
        return getList()[position]
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    /**
     * 绑定ViewHolder
     */
    override fun onBindViewHolder(holder: BaseViewHolder<T, ViewDataBinding>, position: Int) {
        holder.itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


        //设置Item点击事件
        holder.itemView.setOnClickListener {
            (mListener as? OnClickItemListener)?.onClickItemListener(it, position)
        }

        holder.setListener(mListener).onBindViewHolder(getObj(position), position)
    }


    override fun onCreateLayoutHelper(): LayoutHelper {
        return layoutHelper
    }


    /*********************** 对外提供方法 ******************************/


    /**
     * 设置Item条数
     */
    fun setCount(count: Int): BaseAdapterV2<T> {
        this.count = count
        return this
    }

    /**
     * 设置布局方式
     */
    fun setLayoutHelper(helper: LayoutHelper): BaseAdapterV2<T> {
        this.layoutHelper = helper
        return this
    }

    fun setLayoutParams(layoutParams: ViewGroup.LayoutParams): BaseAdapterV2<T> {

        this.layoutParams = layoutParams
        return this
    }

    /**
     * 点击事件
     */
    fun setListener(listener: Any?): BaseAdapterV2<T> {
        mListener = listener
        return this
    }

    fun toAdapter():DelegateAdapter.Adapter<RecyclerView.ViewHolder> {
        return this as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
    }


}