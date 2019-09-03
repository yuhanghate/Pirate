package com.yuhang.novel.pirate.ui.settings.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityDisclaimerBinding
import com.yuhang.novel.pirate.ui.settings.viewmodel.DisclaimerViewModel

/**
 * 免责申明
 */
class DisclaimerActivity :BaseSwipeBackActivity<ActivityDisclaimerBinding, DisclaimerViewModel>(){

    companion object{
        fun start(context: Activity) {
            val intent = Intent(context, DisclaimerActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_disclaimer
    }

    override fun initView() {
        super.initView()
        onClick()
    }

    private fun onClick() {

        mBinding.btnBack.setOnClickListener { onBackPressed() }
    }
}