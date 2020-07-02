package com.yuhang.novel.pirate.ui.settings.activity

import android.content.Context
import android.content.Intent
import com.gyf.immersionbar.ImmersionBar
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityShareQrCodeBinding
import com.yuhang.novel.pirate.ui.settings.viewmodel.ShareQrcodeViewModel
import com.yuhang.novel.pirate.utils.QRCodeUtil
import com.yuhang.novel.pirate.utils.ScreenUtils
import kotlin.concurrent.thread

/**
 * 二维码
 */
class ShareQrcodeActivity : BaseActivity<ActivityShareQrCodeBinding, ShareQrcodeViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ShareQrcodeActivity::class.java)
            startIntent(context, intent)
        }
    }


    override fun onLayoutId(): Int {
        return R.layout.activity_share_qr_code
    }

    override fun initStatusTool() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarView(mBinding.statusBarV)
            .fitsSystemWindows(false)
            .navigationBarColor(android.R.color.transparent) //导航栏颜色，不写默认黑色
            .init()
    }


    override fun initView() {
        super.initView()
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }

        val dp250 = ScreenUtils.dpToPx(270)
        thread {
            QRCodeUtil.createQRCodeBitmap("https://www.baidu.com", dp250, dp250)?.let {
                runOnUiThread { mBinding.qrCodeIv.setImageBitmap(it) }
            }
        }

    }


}