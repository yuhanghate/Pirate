package com.yuhang.novel.pirate.ui.settings.viewmodel

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.eventbus.LogoutEvent
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

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

        viewModelScope.launch {
            mDataRepository.clearUsers()
            PreferenceUtil.commitString("token", "")
            PirateApp.getInstance().setToken("")
            //登出
            mActivity?.runOnUiThread {
                mActivity?.onBackPressed()
                EventBus.getDefault().postSticky(LogoutEvent())
            }
        }

    }

    /**
     * 意见反馈
     */
    fun sendEmail() {
        val data = Intent(Intent.ACTION_SENDTO);
        data.data = Uri.parse("mailto:yh714610354@gmail.com")
        data.putExtra(
            Intent.EXTRA_SUBJECT,
            "我对App有话说[${android.os.Build.BRAND}/${android.os.Build.MODEL}/${android.os.Build.VERSION.RELEASE}/随便看书]"
        )
        data.putExtra(Intent.EXTRA_TEXT, "")
        try {
            mActivity?.startActivity(data)
        } catch (e: Exception) {
            mActivity?.niceToast("未安装邮箱或安装的版本不支持")
        }

    }
}