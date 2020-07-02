package com.yuhang.novel.pirate.ui.payment.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import com.gyf.immersionbar.ImmersionBar
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.databinding.DialogVipNoteBinding
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.payment.viewmodel.VipViewModel
import com.yuhang.novel.pirate.utils.AppManagerUtils

/**
 * 跳VIP
 */
class VipNoteDialog : BaseActivity<DialogVipNoteBinding, VipViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, VipNoteDialog::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.dialog_vip_note
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val lp = window.attributes
        lp.gravity = Gravity.CENTER
//
        window.attributes = lp // 设置参数给window

        super.onCreate(savedInstanceState)
        this.setFinishOnTouchOutside(false)
    }

    override fun initStatusTool() {
        ImmersionBar.with(this)
            .fullScreen(true)
            .init()
    }

    override fun initView() {
        super.initView()
        onClick()
    }

    fun onClick() {
        mBinding.btnPayment.setOnClickListener { PaymentActivity.start(this) }
        mBinding.btnClose.setOnClickListener {
            finish()
            AppManagerUtils.getAppManager().finishActivity(ReadBookActivity::class.java)

        }
    }
}