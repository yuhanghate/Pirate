package com.yuhang.novel.pirate.ui.settings.viewmodel

import android.text.TextUtils
import com.umeng.analytics.MobclickAgent
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.eventbus.LogoutEvent
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import kotlin.concurrent.thread

class SettingsViewModel : BaseViewModel() {

    /**
     * 是否登陆
     */
    fun isLogin(): Boolean {
        return !TextUtils.isEmpty(PirateApp.getInstance().getToken())


    }

    /**
     * 退出登陆
     */
    fun logout() {

        thread {
            mDataRepository.clearUsers()
            PreferenceUtil.commitString("token", "")
            PirateApp.getInstance().setToken("")
            //登出
            MobclickAgent.onProfileSignOff()
            mActivity?.runOnUiThread {
                mActivity?.onBackPressed()
                EventBus.getDefault().postSticky(LogoutEvent())
            }
        }

    }
}