package com.yuhang.novel.pirate.ui.user.activity

import android.app.Activity
import android.content.Intent
import com.umeng.analytics.MobclickAgent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.databinding.ActivityUpdatePasswordBinding
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.service.UsersService
import com.yuhang.novel.pirate.service.impl.UsersServiceImpl
import com.yuhang.novel.pirate.ui.user.viewmodel.UpdatePasswordViewModel
import com.yuhang.novel.pirate.utils.AppManagerUtils
import org.greenrobot.eventbus.EventBus

/**
 * 修改密码
 */
class UpdatePasswordActivity : BaseActivity<ActivityUpdatePasswordBinding, UpdatePasswordViewModel>() {

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
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
        mBinding.btnCommit.setOnClickListener {
            if (mViewModel.checkParams(mBinding.mobileEt, mBinding.passwordEt, mBinding.passwordAgainEt)) {

                showProgressbar("加载中...")
                mViewModel.updatePassword(mBinding.mobileEt, mBinding.passwordEt, mBinding.passwordAgainEt)
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
                                        .subscribe({
                                        }, {
                                            onBackPressed()
                                        }, {
                                            mUsersService.updateUsersToLocal(userResult = userResult)
                                                    .compose(bindToLifecycle()).subscribe({
                                                        EventBus.getDefault().postSticky(userResult)
                                                        AppManagerUtils.getAppManager().finishActivity(UpdatePasswordActivity::class.java)
                                                        AppManagerUtils.getAppManager().finishActivity(ForgetActivity::class.java)
                                                        AppManagerUtils.getAppManager().finishActivity(ForgetMailActivity::class.java)
                                                        AppManagerUtils.getAppManager().finishActivity(LoginActivity::class.java)
                                                    }, {
                                                        onBackPressed()
                                                    }, {
                                                        com.orhanobut.logger.Logger.i("", "")
                                                    })
                                        })
                            } else {
                                niceTipTop(mBinding.btnCommit, it.msg)
                            }
                        }, {
                            closeProgressbar()
                        })
            }
        }
    }
}