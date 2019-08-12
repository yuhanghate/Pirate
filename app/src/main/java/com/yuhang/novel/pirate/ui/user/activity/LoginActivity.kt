package com.yuhang.novel.pirate.ui.user.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.yuhang.novel.pirate.extension.niceToast
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.databinding.ActivityLoginBinding
import com.yuhang.novel.pirate.ui.user.viewmodel.LoginViewModel

/**
 * 登陆
 */
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(){

    companion object{
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
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
        mBinding.btnCommit.setOnClickListener { netLogin() }
        mBinding.btnRegister.setOnClickListener { RegisterActivity.start(this) }
    }

    /**
     * 登陆
     */
    @SuppressLint("CheckResult")
    private fun netLogin() {
        val username = mBinding.mobileEt.text.toString()
        val password = mBinding.passwordEt.text.toString()
        if (mViewModel.checkParams(mBinding.mobileEt, mBinding.passwordEt)) {
            mViewModel.login(username, password)
                .compose(bindToLifecycle())
                .subscribe({
                    if (it.code == 200) {
                        //保存到本地
                        mViewModel.saveAccount(it)
                        onBackPressed()
                    } else {
                        niceToast(it.msg)
                    }

                },{
                    niceToast("登陆失败")
                })
        }

    }
}