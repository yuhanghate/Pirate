package com.yuhang.novel.pirate.base

import android.content.Context
import androidx.lifecycle.ViewModel
import com.umeng.analytics.MobclickAgent
import com.yuhang.novel.pirate.app.PirateApp
import org.greenrobot.eventbus.EventBus

open class BaseViewModel : ViewModel() {

    val mDataRepository by lazy { PirateApp.getInstance().getDataRepository() }

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