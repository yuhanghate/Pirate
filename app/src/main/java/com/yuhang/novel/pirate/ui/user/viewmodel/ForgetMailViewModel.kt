package com.yuhang.novel.pirate.ui.user.viewmodel

import android.text.TextUtils
import com.vondear.rxtool.RxRegTool
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.databinding.ActivityForgetBinding
import com.yuhang.novel.pirate.databinding.ActivityForgetMailBinding
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.repository.network.data.pirate.result.StatusResult
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ForgetMailViewModel : BaseViewModel() {

    /**
     * 检查邮箱
     */
    fun checkParams(binding: ActivityForgetMailBinding):Boolean {
        val code = binding.mailEt.text.toString()
        if (TextUtils.isEmpty(code)) {
            mActivity?.niceTipTop(binding.mailEt, "邮箱不能为空")
            return false
        }
        if (!RxRegTool.isEmail(binding.mailEt.text.toString())) {
            mActivity?.niceTipTop(binding.mailEt, "邮箱格式不正确")
            return false
        }
        return true
    }

    /**
     * 检测用户邮箱是否存在
     */
    fun checkEmailEmpty(email: String): Flowable<StatusResult> {
        return mDataRepository.checkEmailEmpty(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}