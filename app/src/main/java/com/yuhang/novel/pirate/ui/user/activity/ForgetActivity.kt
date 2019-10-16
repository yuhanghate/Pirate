package com.yuhang.novel.pirate.ui.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat
import com.vondear.rxtool.RxKeyboardTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.databinding.ActivityForgetBinding
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.ui.user.viewmodel.ForgetViewModel

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

        mViewModel.sendMailCodeView(mBinding)
        mViewModel.postMailCode(mViewModel.email)
            .compose(bindToLifecycle())
            .subscribe({
                it.data?.let { code -> mViewModel.code = code }
            }, {
                mBinding.btnCode.isEnabled = true
                mBinding.btnCode.setBackgroundResource(R.drawable.bg_material_item_blue_round)
                mBinding.btnCode.text = "获取验证码"
                mBinding.btnCode.setTextColor(ContextCompat.getColor(this, R.color.md_red_300))
                niceToast("获取验证码失败,请重试")
            })
    }

    @SuppressLint("CheckResult")
    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
        mBinding.btnCode.setOnClickListener {
            RxKeyboardTool.showSoftInput(this, mBinding.codeEt)
            initData()
        }
        mBinding.btnNext.setOnClickListener {
            if (mViewModel.checkParams(mBinding)) {
                showProgressbar("请等待...")
                mViewModel.checkEmailCode(mBinding)
                    .compose(bindToLifecycle())
                    .subscribe({
                        closeProgressbar()
                        if (it.code == 200) {
                            UpdatePasswordActivity.start(this, mViewModel.email)
                        } else {
                            niceTipTop(mBinding.codeEt, it.msg)
                        }
                    }, {
                        closeProgressbar()
                        niceToast("网络问题,请重试")
                    })

            }
        }
    }
}