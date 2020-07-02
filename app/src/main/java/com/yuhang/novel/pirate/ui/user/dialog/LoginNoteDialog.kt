package com.yuhang.novel.pirate.ui.user.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.components.RxDialogFragment
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.databinding.DialogLoginNoteBinding
import com.yuhang.novel.pirate.ui.user.activity.LoginActivity
import com.yuhang.novel.pirate.ui.user.viewmodel.LoginNoteViewModel
import com.yuhang.novel.pirate.utils.ScreenUtils


/**
 * 提示登陆
 */
class LoginNoteDialog : BaseActivity<DialogLoginNoteBinding, LoginNoteViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginNoteDialog::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.dialog_login_note
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val lp = window.attributes
        lp.gravity = Gravity.CENTER
//
        window.attributes = lp // 设置参数给window

        super.onCreate(savedInstanceState)
        this.setFinishOnTouchOutside(true)
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
        mBinding.root.setOnClickListener { finish() }
        mBinding.btnClose.setOnClickListener { finish() }
        mBinding.btnLogin.setOnClickListener {
            LoginActivity.start(this)
            Handler(Looper.getMainLooper()).postDelayed({finish()},400)

        }
    }
}