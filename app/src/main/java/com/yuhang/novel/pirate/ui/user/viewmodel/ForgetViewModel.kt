package com.yuhang.novel.pirate.ui.user.viewmodel

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.databinding.ActivityForgetBinding
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.repository.network.data.pirate.result.EmailCodeResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.StatusResult
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ForgetViewModel : BaseViewModel() {

    /**
     * 邮箱验证码
     */
    var code: String = ""

    /**
     * 邮箱
     */
    var email: String = ""

    /**
     * 验证码View样式
     */
    @SuppressLint("CheckResult")
    fun sendMailCodeView(binding: ActivityForgetBinding) {

//        RxKeyboardTool.showSoftInput(mActivity, binding.codeEt)
        val count: Long = 60
        Flowable.interval(0, 1, TimeUnit.SECONDS)
                .take(count + 1)
                .map { count - it }
                .compose(mActivity?.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    binding.btnCode.isEnabled = false
                    binding.btnCode.background = null
                    binding.btnCode.setTextColor(ContextCompat.getColor(mActivity!!, R.color.secondary_text))
                }
                .subscribe({
                    binding.btnCode.text = "${it}秒后重新获取"
                }, {}, {
                    binding.btnCode.isEnabled = true
                    binding.btnCode.setBackgroundResource(R.drawable.bg_material_item_blue_round)
                    binding.btnCode.text = "获取验证码"
                    binding.btnCode.setTextColor(ContextCompat.getColor(mActivity!!, R.color.md_red_300))
                })
    }

    /**
     * 检查验证码
     */
    fun checkParams(binding: ActivityForgetBinding): Boolean {
        val code = binding.codeEt.text.toString()
        if (TextUtils.isEmpty(code)) {
            mActivity?.niceTipTop(binding.codeEt, "验证码不能为空")
            return false
        }
        if (code.length != 4) {
            mActivity?.niceTipTop(binding.codeEt, "验证码必须4位")
            return false
        }
        return true
    }

    /**
     * 发送验证码到服务器
     */
    fun postMailCode(email: String): Flowable<EmailCodeResult> {
        return mDataRepository.getMailCode(email).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 检测邮箱验证码
     */
    fun checkEmailCode(binding: ActivityForgetBinding):Flowable<StatusResult> {

        return mDataRepository.checkEmailCode(email, binding.codeEt.text.toString(), code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}
