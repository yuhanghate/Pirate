package com.yuhang.novel.pirate.ui.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.orhanobut.logger.Logger
import com.tamsiree.rxkit.RxKeyboardTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.databinding.ActivityForgetBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.network.data.pirate.result.EmailCodeResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.StatusResult
import com.yuhang.novel.pirate.ui.user.viewmodel.ForgetViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 忘记密码
 */
class ForgetActivity : BaseActivity<ActivityForgetBinding, ForgetViewModel>() {

    companion object {
        const val EMAIL = "email"
        fun start(context: Activity, email: String) {
            val intent = Intent(context, ForgetActivity::class.java)
            intent.putExtra(EMAIL, email)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_forget
    }

    override fun initView() {
        super.initView()
        mViewModel.email = intent.getStringExtra(EMAIL)!!
        onClick()
    }

    @SuppressLint("CheckResult")
    override fun initData() {

        lifecycleScope.launch {
            mViewModel.sendMailCodeView(mBinding)
            flow { emit(mViewModel.postMailCode(mViewModel.email)) }
                .onCompletion {
                    mBinding.btnCode.isEnabled = true
                    mBinding.btnCode.setBackgroundResource(R.drawable.bg_material_item_blue_round)
                    mBinding.btnCode.text = "获取验证码"
                    mBinding.btnCode.setTextColor(ContextCompat.getColor(this@ForgetActivity,
                        R.color.md_red_300))
                    niceToast("获取验证码失败,请重试")
                }
                .catch { Logger.e(it.message ?: "") }
                .collect { it.data?.let { code -> mViewModel.code = code } }
        }


    }

    @SuppressLint("CheckResult")
    private fun onClick() {
        mBinding.btnBack.clickWithTrigger { onBackPressedSupport() }
        mBinding.btnCode.clickWithTrigger {
            RxKeyboardTool.showSoftInput(this, mBinding.codeEt)
            initData()
        }
        mBinding.btnNext.clickWithTrigger {
            if (mViewModel.checkParams(mBinding)) {
                lifecycleScope.launch {
                    flow { emit(mViewModel.checkEmailCode(mBinding)) }
                        .onStart { showProgressbar("请等待...") }
                        .onCompletion { closeProgressbar() }
                        .catch { niceToast("网络问题,请重试") }
                        .collect {
                            if (it.code != 200) {
                                niceTipTop(mBinding.codeEt, it.msg)
                                return@collect
                            }
                            UpdatePasswordActivity.start(this@ForgetActivity, mViewModel.email)
                        }
                }
            }
        }
    }
}