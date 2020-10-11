package com.yuhang.novel.pirate.ui.user.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.databinding.ActivityForgetMailBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.network.data.pirate.result.StatusResult
import com.yuhang.novel.pirate.ui.user.viewmodel.ForgetMailViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 输入邮箱
 */
class ForgetMailActivity : BaseActivity<ActivityForgetMailBinding, ForgetMailViewModel>() {

    companion object {
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
        mBinding.btnBack.clickWithTrigger { onBackPressedSupport() }
        mBinding.btnNext.clickWithTrigger {
            if (mViewModel.checkParams(mBinding)) {
                val email = mBinding.mailEt.text.toString()
                lifecycleScope.launch {
                    flow { emit(mViewModel.checkEmailEmpty(email)) }
                        .onStart { showProgressbar("检测用户是否存在...") }
                        .onCompletion { closeProgressbar() }
                        .catch {
                            niceToast("请求失败,请重试")
                        }
                        .collect {
                            if (it.code != 200) {
                                niceTipTop(mBinding.mailEt, it.msg)
                            }
                            ForgetActivity.start(this@ForgetMailActivity, email)
                        }
                }
            }
        }
    }

}