package com.yuhang.novel.pirate.ui.book.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import cn.bingoogolapple.swipebacklayout.BGAKeyboardUtil
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.book.viewmodel.ReadBookViewModel

class SexReadBookActivity : ReadBookActivity() {

    companion object {
        fun start(context: Activity, obj: BooksResult) {
            val intent = Intent(context, SexReadBookActivity::class.java)
            intent.putExtra(BOOKS_RESULT, obj.toJson())
            intent.putExtra(IS_INIT_CHAPTER, false)
            startIntent(context, intent)
        }
    }

    override fun getViewModelClass(): Class<out ReadBookViewModel> {
        return ReadBookViewModel::class.java
    }


    override fun initView() {
        super.initView()
        mBinding.layoutTop.downloadTv.visibility = View.GONE
        mBinding.layoutTop.resouceTv.visibility = View.GONE
    }

    override fun onBackPressedSupport() {
        if (mReadBookView.toggleMenuSwitch) {
            //如果上下切换栏显示就隐藏起来
            mReadBookView.toggleMenu()
        } else {
            BGAKeyboardUtil.closeKeyboard(this)
            finish()
            this.overridePendingTransition(
                R.anim.bga_sbl_activity_backward_enter,
                R.anim.bga_sbl_activity_backward_exit
            )
        }
    }
}