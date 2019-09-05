package com.yuhang.novel.pirate.ui.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivityRegisterBinding
import com.yuhang.novel.pirate.ui.user.viewmodel.RegisterViewModel
import com.yuhang.novel.pirate.utils.AppManagerUtils

/**
 * 注册
 */
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>() {
    val TAG: String = RegisterActivity::class.java.simpleName

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, RegisterActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_register
    }

    override fun initView() {
        super.initView()
        onClick()
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressed() }
        mBinding.btnCommit.setOnClickListener {
            mViewModel.onUMEvent(this, UMConstant.TYPE_REGISTER, "点击注册")
            netRegister()
        }
    }

    /**
     * 注册
     */
    @SuppressLint("CheckResult")
    private fun netRegister() {

        val username = mBinding.mobileEt.text.toString()
        val password = mBinding.passwordEt.text.toString()
        val email = mBinding.emailEt.text.toString()
        if (mViewModel.checkParams(
                mBinding.mobileEt,
                mBinding.passwordEt,
                mBinding.passwordAgainEt,
                mBinding.emailEt
            )
        ) {
            mViewModel.register(username, password, email)
                .compose(bindToLifecycle())
                .subscribe({
                    if (it.code == 200) {
//                        MobclickAgent.onProfileSignIn(it.data.id)
                        mViewModel.saveAccount(it)
                        AppManagerUtils.getAppManager().finishActivity(LoginActivity::class.java)
                        onBackPressed()
                    }
                    Logger.t(TAG).i(it.msg)
                }, {
                    Logger.t(TAG).i(it.message!!)
                })
        }

    }

    override fun onResume() {
        super.onResume()
        mViewModel.onPageStart("注册页")
    }

    override fun onPause() {
        super.onPause()
        mViewModel.onPageEnd("注册页")
    }
}