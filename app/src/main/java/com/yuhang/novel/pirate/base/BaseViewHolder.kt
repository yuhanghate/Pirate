package com.yuhang.novel.pirate.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import java.lang.reflect.ParameterizedType

abstract class BaseViewHolder<in T : Any, D : ViewBinding>(
    viewGroup: ViewGroup, @LayoutRes private val layoutId: Int,
    val isExecute: Boolean = true) : RecyclerView.ViewHolder(
    LayoutInflater.from(viewGroup.context).inflate(layoutId, viewGroup, false)) {

    lateinit var mBinding: D

    open val mContext: Context by lazy { mBinding.root.context }

    open var mListener : Any? = null

    var lastClickItemPosition = -1

    /**
     * 初始化ViewBinding
     */
    private fun  initBinding(){
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[1] as Class<D>
            val method = clazz.getMethod("bind", View::class.java)
            mBinding = method.invoke(null, itemView) as D
        }
    }


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
    open fun onBindViewHolder(obj: T, position: Int){
        initBinding()
    }

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