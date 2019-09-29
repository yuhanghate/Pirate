package com.yuhang.novel.pirate.ui.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.orhanobut.logger.Logger
import com.umeng.analytics.MobclickAgent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivityRegisterBinding
import com.yuhang.novel.pirate.extension.niceToast
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
            showProgressbar()
            mViewModel.register(username, password, email)
                .compose(bindToLifecycle())
                .subscribe({result ->
                    closeProgressbar()
                    if (result.code == 200) {
                        MobclickAgent.onProfileSignIn(result.data.id)
                        PirateApp.getInstance().setToken(result.data.token)
                        mViewModel.synCollection()
                            .subscribe({
                            }, {
                                Logger.t(TAG).i(it.message!!)
                            }, {
                                mViewModel.saveAccount(result)
                                AppManagerUtils.getAppManager().finishActivity(LoginActivity::class.java)
                                onBackPressed()
                            })

                    } else {
                        niceToast(result.msg)
                    }
                    Logger.t(TAG).i(result.msg)
                }, {
                    closeProgressbar()
                    Logger.t(TAG).i(it.message!!)
                    niceToast("注册失败, 请检查网络")
                })
        }

    }

    override fun onResume() {
        super.onResume()
        mViewModel.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        mViewModel.onPause(this)
    }
}