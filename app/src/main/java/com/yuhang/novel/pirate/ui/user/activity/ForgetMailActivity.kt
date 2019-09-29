package com.yuhang.novel.pirate.ui.user.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.databinding.ActivityForgetMailBinding
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.ui.user.viewmodel.ForgetMailViewModel

/**
 * 输入邮箱
 */
class ForgetMailActivity:BaseActivity<ActivityForgetMailBinding, ForgetMailViewModel>() {

    companion object{
        fun start(context: Activity) {
            val intent = Intent(context, ForgetMailActivity::class.java)
            startIntent(context, intent)
        }
    }
    override fun onLayoutId(): Int {
        return R.layout.activity_forget_mail
    }

    override fun initView() {
        super.initView()

        onClick()
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
        mBinding.btnNext.setOnClickListener {
            if (mViewModel.checkParams(mBinding)) {
                val email = mBinding.mailEt.text.toString()
                showProgressbar("检测用户是否存在...")
                mViewModel.checkEmailEmpty(email)
                        .compose(bindToLifecycle())
                        .subscribe({
                            closeProgressbar()
                            if (it.code == 200) {
                                ForgetActivity.start(this, email)
                            } else {
                                niceTipTop(mBinding.mailEt, it.msg)
                            }

                        },{
                            closeProgressbar()
                            niceToast("请求失败,请重试")
                        })

            }
        }
    }

}