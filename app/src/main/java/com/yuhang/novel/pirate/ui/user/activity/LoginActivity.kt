package com.yuhang.novel.pirate.ui.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.umeng.analytics.MobclickAgent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivityLoginBinding
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.service.UsersService
import com.yuhang.novel.pirate.service.impl.UsersServiceImpl
import com.yuhang.novel.pirate.ui.user.viewmodel.LoginViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Logger

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
        mBinding.btnBack.setOnClickListener { onBackPressed() }
        mBinding.btnCommit.setOnClickListener {
            mViewModel.onUMEvent(this, UMConstant.TYPE_LOGIN, "点击登录")
            netLogin()
        }
        mBinding.btnRegister.setOnClickListener { RegisterActivity.start(this) }
        mBinding.forgetTv.setOnClickListener { ForgetMailActivity.start(this) }
    }

    /**
     * 登陆
     */
    @SuppressLint("CheckResult")
    private fun netLogin() {
        val username = mBinding.mobileEt.text.toString()
        val password = mBinding.passwordEt.text.toString()

        if (mViewModel.checkParams(mBinding.mobileEt, mBinding.passwordEt)) {
            showProgressbar("登录中...")
            mViewModel.login(username, password)
                .compose(bindToLifecycle())
                .subscribe({
                    closeProgressbar()
                    if (it.code == 200) {
                        //保存到本地
                        val userResult = it
                        //当用户使用自有账号登录时，可以这样统计：
                        MobclickAgent.onProfileSignIn(userResult.data.id)
                        PirateApp.getInstance().setToken(userResult.data.token)
                        mViewModel.synCollection()
                            .compose(bindToLifecycle())
                            .subscribe({
                            }, {
                                closeProgressbar()
                                onBackPressed()
                            },{
                                mUsersService.updateUsersToLocal(userResult = userResult).subscribe({
                                    EventBus.getDefault().postSticky(userResult)
                                    onBackPressed()
                                }, {
                                    closeProgressbar()
                                    onBackPressed()
                                },{
                                    com.orhanobut.logger.Logger.i("","")
                                })
                            })


                    } else {
                        niceToast(it.msg)
                    }

                }, {
                    closeProgressbar()
                    niceToast("登录失败")
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