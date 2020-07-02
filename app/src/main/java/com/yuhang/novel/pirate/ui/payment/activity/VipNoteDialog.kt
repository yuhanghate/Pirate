package com.yuhang.novel.pirate.ui.payment.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import androidx.core.text.HtmlCompat
import com.gyf.immersionbar.ImmersionBar
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.databinding.DialogVipNoteBinding
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.ui.book.activity.SexReadBookActivity
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
        mBinding.contentTv.text = HtmlCompat.fromHtml("<font color=\"#FFB74D\">本产品为纯商业化运作,氪金极重!产品专注挖坑二十年,极不要脸.充值就一定能看到想看的,体验前务必慎重!</font><br><br>还要啰嗦几句..<br><br><font color=\"#9E9E9E\">本着雁过拨毛的推广原则,我们不想放过任何年龄的用户,决定1-134岁通吃!</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun onClick() {
        mBinding.btnPayment.setOnClickListener { PaymentActivity.start(this) }
        mBinding.btnClose.setOnClickListener {
            finish()
            AppManagerUtils.getAppManager().finishActivity(ReadBookActivity::class.java)
            AppManagerUtils.getAppManager().finishActivity(SexReadBookActivity::class.java)

        }
    }

    override fun onBackPressedSupport() {
        finish()
        AppManagerUtils.getAppManager().finishActivity(ReadBookActivity::class.java)
        AppManagerUtils.getAppManager().finishActivity(SexReadBookActivity::class.java)
    }
}