package com.yuhang.novel.pirate.ui.user.activity

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.orhanobut.logger.Logger
import com.umeng.analytics.MobclickAgent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.databinding.ActivityUpdatePasswordBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.service.UsersService
import com.yuhang.novel.pirate.service.impl.UsersServiceImpl
import com.yuhang.novel.pirate.ui.user.viewmodel.UpdatePasswordViewModel
import com.yuhang.novel.pirate.utils.AppManagerUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 * 修改密码
 */
class UpdatePasswordActivity :
    BaseActivity<ActivityUpdatePasswordBinding, UpdatePasswordViewModel>() {

    private val mUsersService: UsersService by lazy { UsersServiceImpl() }

    companion object {
        const val EMAIL = "email"
        fun start(context: Activity, email: String) {
            val intent = Intent(context, UpdatePasswordActivity::class.java)
            intent.putExtra(EMAIL, email)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_update_password
    }

    override fun initView() {
        super.initView()
        mViewModel.email = intent.getStringExtra(EMAIL)!!
        onClick()
    }

    private fun onClick() {
        mBinding.btnBack.clickWithTrigger { onBackPressedSupport() }
        mBinding.btnCommit.clickWithTrigger {
            val mobileEt = mBinding.mobileEt
            val passwordEt = mBinding.passwordEt
            val passwordAgainEt = mBinding.passwordAgainEt
            if (mViewModel.checkParams(mobileEt, passwordEt, passwordAgainEt)) {

                lifecycleScope.launch {
                    flow {
                        val userResult =
                            mViewModel.updatePassword(mobileEt, passwordEt, passwordAgainEt)
                        if (userResult.code != 200) {
                            niceTipTop(mBinding.btnCommit, userResult.msg)
                        }

                        //当用户使用自有账号登录时，可以这样统计：
                        MobclickAgent.onProfileSignIn(userResult.data.id)
                        PirateApp.getInstance().setToken(userResult.data.token)
                        mViewModel.synCollection()
                        mUsersService.updateUsersToLocal(userResult = userResult)
                        EventBus.getDefault().postSticky(userResult)
                        emit(Unit)
                    }
                        .onStart { showProgressbar("加载中...") }
                        .onCompletion { closeProgressbar() }
                        .catch { Logger.e(it.message ?: "") }
                        .collect {
                            AppManagerUtils.getAppManager()
                                .finishActivity(UpdatePasswordActivity::class.java)
                            AppManagerUtils.getAppManager()
                                .finishActivity(ForgetActivity::class.java)
                            AppManagerUtils.getAppManager()
                                .finishActivity(ForgetMailActivity::class.java)
                            AppManagerUtils.getAppManager()
                                .finishActivity(LoginActivity::class.java)
                            onBackPressed()
                        }
                }


            }
        }
    }
}