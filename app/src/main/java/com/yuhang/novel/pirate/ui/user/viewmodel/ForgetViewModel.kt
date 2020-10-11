package com.yuhang.novel.pirate.ui.user.viewmodel

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.databinding.ActivityForgetBinding
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.repository.network.data.pirate.result.EmailCodeResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.StatusResult

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

        binding.btnCode.isEnabled = false
        binding.btnCode.background = null
        binding.btnCode.setTextColor(ContextCompat.getColor(mActivity!!, R.color.secondary_text))

        var count = 60
        object : CountDownTimer(60 * 1000, 1000) {
            override fun onFinish() {
                binding.btnCode.isEnabled = true
                binding.btnCode.setBackgroundResource(R.drawable.bg_material_item_blue_round)
                binding.btnCode.text = "获取验证码"
                binding.btnCode.setTextColor(ContextCompat.getColor(mActivity!!,
                    R.color.md_red_300))
            }

            override fun onTick(millisUntilFinished: Long) {
                binding.btnCode.text = "${count--}秒后重新获取"
            }
        }.start()
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
    suspend fun postMailCode(email: String): EmailCodeResult {
        return mDataRepository.getMailCode(email)
    }

    /**
     * 检测邮箱验证码
     */
    suspend fun checkEmailCode(binding: ActivityForgetBinding): StatusResult {
        return mDataRepository.checkEmailCode(email, binding.codeEt.text.toString(), code)
    }

}
