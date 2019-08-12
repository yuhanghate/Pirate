package com.yuhang.novel.pirate.ui.user.viewmodel

import android.widget.EditText
import com.vondear.rxtool.RxRegTool
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.repository.network.data.pirate.result.UserResult
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RegisterViewModel : BaseViewModel() {

    /**
     * 注册
     */
    fun register(username: String, password: String, email: String): Flowable<UserResult> {
        return mDataRepository.register(username, password, email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 检测手机参数
     */
    fun checkParams(usernameEt: EditText, passwordEt: EditText, passwordAgainEt: EditText, emailEt: EditText): Boolean {
        val username = usernameEt.text.toString()
        val password = passwordEt.text.toString()
        val passwordAgain = passwordAgainEt.text.toString()
        val email = emailEt.text.toString()
        if (username.isEmpty()) {
            mActivity?.niceTipTop(usernameEt, "用户名不能为空")
            return false
        }
        if (!RxRegTool.isMobileSimple(username)) {
            mActivity?.niceTipTop(usernameEt, "手机号不正确")
            return false
        }
        if (password.isEmpty()) {
            mActivity?.niceTipTop(passwordEt, "密码不能为空")
            return false
        }

        if (password.length < 6) {
            mActivity?.niceTipTop(passwordEt, "密码必须6位及以上")
            return false
        }
        if (passwordAgain.isEmpty()) {
            mActivity?.niceTipTop(passwordAgainEt, "密码不能为空")
            return false
        }

        if (passwordAgain.length < 6) {
            mActivity?.niceTipTop(passwordEt, "密码必须6位及以上")
            return false
        }

        if (password != passwordAgain) {
            mActivity?.niceTipTop(passwordAgainEt, "两次密码输入不一致")
            return false
        }
        if (email.isEmpty()) {
            mActivity?.niceTipTop(emailEt, "密码不能为空")
            return false
        }

        return true
    }
}