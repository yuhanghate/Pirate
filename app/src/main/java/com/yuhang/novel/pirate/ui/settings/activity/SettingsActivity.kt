package com.yuhang.novel.pirate.ui.settings.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivitySettingsBinding
import com.yuhang.novel.pirate.ui.settings.viewmodel.SettingsViewModel
import com.yuhang.novel.pirate.utils.GlideCacheUtil
import kotlin.concurrent.thread

/**
 * 设置
 */
class SettingsActivity : BaseSwipeBackActivity<ActivitySettingsBinding, SettingsViewModel>() {

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, SettingsActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_settings
    }

    override fun initView() {
        super.initView()
        onClick()
        initLogoutBtn()
        initCache()
    }

    private fun initCache() {
        thread {
            val cacheSize = GlideCacheUtil.getInstance().getCacheSize(this)
            runOnUiThread { mBinding.sizeTv.text = cacheSize }
        }
    }

    /**
     * 没登陆不显示退出按钮
     */
    @SuppressLint("CheckResult")
    private fun initLogoutBtn() {
        if (mViewModel.isLogin()) {
            mBinding.btnLogout.visibility = View.VISIBLE
        } else {
            mBinding.btnLogout.visibility = View.GONE
        }
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressed() }
        mBinding.btnLogout.setOnClickListener { mViewModel.logout() }
        mBinding.btnDisclaimer.setOnClickListener { DisclaimerActivity.start(this) }
        mBinding.btnClear.setOnClickListener { clear() }
    }

    private fun clear() {
        showProgressbar("清理中...")
        thread {
            GlideCacheUtil.getInstance().clearImageAllCache(this)
            initCache()
            runOnUiThread { closeProgressbar() }
        }
    }
}