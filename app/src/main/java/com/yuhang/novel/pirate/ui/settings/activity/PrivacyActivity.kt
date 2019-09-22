package com.yuhang.novel.pirate.ui.settings.activity

import android.content.Context
import android.content.Intent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityPrivacyBinding
import com.yuhang.novel.pirate.ui.settings.viewmodel.PrivacyViewModel

/**
 * 隐私协议
 */
class PrivacyActivity:BaseSwipeBackActivity<ActivityPrivacyBinding, PrivacyViewModel>() {

    companion object{
        fun start(context: Context) {
            val intent = Intent(context, PrivacyActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onLayoutId(): Int {
        return R.layout.activity_privacy
    }

    override fun initView() {
        super.initView()
        onClick()
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
    }
}