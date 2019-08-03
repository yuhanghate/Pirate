package com.yuhang.novel.pirate.base

import androidx.lifecycle.ViewModel
import com.yuhang.novel.pirate.app.PirateApp
import org.greenrobot.eventbus.EventBus

open class BaseViewModel : ViewModel() {

    val mDataRepository by lazy { PirateApp.getInstance().getDataRepository() }

    var mFragment: BaseFragment<*, *>? = null

    var mActivity:BaseActivity<*,*>? = null
    /**
     * 发送Eventbus
     */
    fun postEvenbus(any: Any) {
        EventBus.getDefault().post(any)
    }
}