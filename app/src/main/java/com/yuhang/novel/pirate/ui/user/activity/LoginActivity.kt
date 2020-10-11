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
import com.yuhang.novel.pirate.databinding.ActivityLoginBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.service.UsersService
import com.yuhang.novel.pirate.service.impl.UsersServiceImpl
import com.yuhang.novel.pirate.ui.user.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 * 登录
 */
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    private val mUsersService: UsersService by lazy { UsersServiceImpl() }

    companion object {

        const val RESULT_CODE = 1000
        fun start(context: Activity) {
            val intent = Intent(context, LoginActivity::class.java)
            startIntent(context, intent)
        }

        fun startForResult(context: Activity) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivityForResult(intent, RESULT_CODE)
            context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        super.initView()
        onClick()
    }

    private fun onClick() {
        mBinding.btnBack.clickWithTrigger { onBackPressed() }
        mBinding.btnCommit.clickWithTrigger {
            mViewModel.onUMEvent(this, UMConstant.TYPE_LOGIN, "点击登录")
            netLogin()
        }
        mBinding.btnRegister.clickWithTrigger { RegisterActivity.start(this) }
        mBinding.forgetTv.clickWithTrigger { ForgetMailActivity.start(this) }
    }

    /**
     * 登陆
     */
    @SuppressLint("CheckResult")
    private fun netLogin() {
        val username = mBinding.mobileEt.text.toString()
        val password = mBinding.passwordEt.text.toString()

        if (mViewModel.checkParams(mBinding.mobileEt, mBinding.passwordEt)) {

            lifecycleScope.launch {
                flow<Unit> {
                    val userResult = mViewModel.login(username, password)
                    if (userResult.code != 200) {
                        niceToast(userResult.msg)
                        return@flow
                    }
                    //当用户使用自有账号登录时，可以这样统计：
                    MobclickAgent.onProfileSignIn(userResult.data.id)
                    PirateApp.getInstance().setToken(userResult.data.token)

                    mViewModel.synCollection()

                    mUsersService.updateUsersToLocal(userResult = userResult)
                    EventBus.getDefault().postSticky(userResult)
                    onBackPressed()
                }
                    .onStart { showProgressbar("登录中...") }
                    .onCompletion { closeProgressbar() }
                    .catch {
                        niceToast("登陆失败")
                        Logger.e("error", it.message)
                    }
                    .collect { }
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