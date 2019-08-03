package com.yuhang.novel.pirate.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager

abstract class BaseViewHolder<in T : Any, out D : ViewDataBinding>(
    viewGroup: ViewGroup?, @LayoutRes private val layoutId: Int,
    val isExecute: Boolean = true) : RecyclerView.ViewHolder(
    DataBindingUtil.inflate<D>(
        LayoutInflater.from(viewGroup?.context), layoutId, viewGroup,
        false).root) {

    open val mBinding: D = DataBindingUtil.getBinding(itemView)!!

    open val mContext: Context by lazy { mBinding.root.context }

    open var mListener : Any? = null

    var lastClickItemPosition = -1


    /**
     * 获取Glide
     */
    fun getGlide(): RequestManager {
        return when (mContext) {
            is AppCompatActivity -> Glide.with(mContext as AppCompatActivity)
            is Fragment -> Glide.with(mContext as Fragment)
            else -> Glide.with(mContext)
        }
    }

    /**
     * view绑定
     */
    open fun onBindViewHolder(obj: T, position: Int){}

    /**
     * VH所有的点击事件
     */
    fun setListener(listener: Any?):BaseViewHolder<T,*> {
        mListener = listener
        return this
    }

    /**
     * 获取点击事件
     */
    fun <listener> getListener():listener?{
        return mListener as? listener

    }
}