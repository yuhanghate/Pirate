package com.yuhang.novel.pirate.ui.payment.activity

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.icu.text.DecimalFormat
import android.view.View
import com.vondear.rxtool.RxDataTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.databinding.ActivityPaymentBinding
import com.yuhang.novel.pirate.ui.payment.viewmodel.PaymentViewModel
import java.math.BigDecimal

/**
 * 充值界面
 */
class PaymentActivity : BaseActivity<ActivityPaymentBinding, PaymentViewModel>() {

    val views by lazy {
        listOf(
            mBinding.btnProduct48,
            mBinding.btnProduct96,
            mBinding.btnProduct198,
            mBinding.btnProduct298
        )
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PaymentActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_payment
    }

    override fun initView() {
        super.initView()
        mBinding.amount48Tv.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
        mBinding.amount96Tv.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
        mBinding.amount198Tv.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
        mBinding.amount298Tv.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
        mBinding.btnPayment.text = "支付 ¥48.00"
        onClick()
    }

    fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
        mBinding.btnProduct48.setOnClickListener { setSelectProduct(it) }
        mBinding.btnProduct96.setOnClickListener { setSelectProduct(it) }
        mBinding.btnProduct198.setOnClickListener { setSelectProduct(it) }
        mBinding.btnProduct298.setOnClickListener { setSelectProduct(it) }
    }

    /**
     * 选择金额
     */
    private fun setSelectProduct(view:View) {
        val decimalFormat = BigDecimal(0.00);//构造方法的字符格式这里如果小数不足2位,会以0补足.
        views.forEach {
            if (it == view) {
                it.setBackgroundResource(R.drawable.bg_payment_selected)
                mViewModel.currentAmount = it.tag.toString().toInt()
                mBinding.btnPayment.text = "支付 ¥${mViewModel.currentAmount}.00"
            } else {
                it.setBackgroundResource(R.drawable.bg_payment_normal)
            }
        }
    }
}