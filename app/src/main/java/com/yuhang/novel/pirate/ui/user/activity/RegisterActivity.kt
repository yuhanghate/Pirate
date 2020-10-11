package com.yuhang.novel.pirate.ui.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.orhanobut.logger.Logger
import com.umeng.analytics.MobclickAgent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivityRegisterBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.ui.user.viewmodel.RegisterViewModel
import com.yuhang.novel.pirate.utils.AppManagerUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.NullPointerException

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
        mBinding.btnBack.clickWithTrigger { onBackPressed() }
        mBinding.btnCommit.clickWithTrigger {
            mViewModel.onUMEvent(this, UMConstant.TYPE_REGISTER, "点击注册")
            lifecycleScope.launch {
                flow<Unit> {
                    netRegister()
                }.catch { println("Exception : ${it.message}") }
                    .collect { }
            }

        }
    }

    /**
     * 注册
     */
    @SuppressLint("CheckResult")
    private fun netRegister() {
        val username = mBinding.mobileEt.text.toString()
        val password = mBinding.passwordEt.text.toString()
        val email = mBinding.mobileEt.text.toString()

        val mobileEt = mBinding.mobileEt
        val emailEt = mBinding.emailEt
        val passwordEt = mBinding.passwordEt
        val passwordAgainEt = mBinding.passwordAgainEt

        if (!mViewModel.checkParams(mobileEt, passwordEt, passwordAgainEt, emailEt)) {
            return
        }

        lifecycleScope.launch {
            flow {
                val userResult = mViewModel.register(username, password, email)
                if (userResult.code != 200) {
                    niceToast(userResult.msg)
                    return@flow
                }

                MobclickAgent.onProfileSignIn(userResult.data.id)
                PirateApp.getInstance().setToken(userResult.data.token)
                mViewModel.synCollection()
                mViewModel.saveAccount(userResult)
                emit(Unit)
            }
                .onStart { showProgressbar() }
                .onCompletion { closeProgressbar() }
                .catch { Logger.e(it.message ?: "") }
                .collect {
                    AppManagerUtils.getAppManager().finishActivity(LoginActivity::class.java)
                    onBackPressed()
                }
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