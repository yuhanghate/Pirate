package com.yuhang.novel.pirate.base

import android.content.Context
import androidx.lifecycle.ViewModel
import com.umeng.analytics.MobclickAgent
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.repository.network.convert.ConvertRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import org.greenrobot.eventbus.EventBus

open class BaseViewModel : ViewModel() {

    val catch = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }

    val mDataRepository by lazy { PirateApp.getInstance().getDataRepository() }

    val mConvertRepository by lazy { ConvertRepository() }

    var mFragment: BaseFragment<*, *>? = null

    var mActivity: BaseActivity<*, *>? = null
    /**
     * 发送Eventbus
     */
    fun postEvenbus(any: Any) {
        EventBus.getDefault().post(any)
    }

    /**
     * 友盟统计页面路径
     * 开始
     */
    fun onPageStart(pageName:String) {
        MobclickAgent.onPageStart(pageName)
    }

    /**
     * 友盟统计页面路径
     */
    fun onResume(context: Context) {
        MobclickAgent.onResume(context)
    }

    /**
     * 友盟统计页面路径
     */
    fun onPause(context: Context) {
        MobclickAgent.onPause(context)
    }

    /**
     * 友盟统计页面路径
     */
    fun onPageEnd(pageName:String) {
        MobclickAgent.onPageEnd(pageName)
    }

    /**
     * 友盟统计事件
     */
    fun onUMEvent(context:Context, eventId:String, label:String) {
        MobclickAgent.onEvent(context, eventId, label)
    }

    /**
     * 友盟统计事件
     */
    fun onUMEvent(context: Context, eventId: String, map: Map<String, String>) {
        MobclickAgent.onEvent(context, eventId, map)
    }
}