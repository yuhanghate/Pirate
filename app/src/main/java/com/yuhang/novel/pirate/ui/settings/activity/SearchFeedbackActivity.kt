package com.yuhang.novel.pirate.ui.settings.activity

import android.content.Context
import android.content.Intent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivitySearchFeedbackBinding
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.ui.settings.viewmodel.SearchFeedbackViewModel
import com.yuhang.novel.pirate.ui.user.activity.LoginActivity

/**
 * 求书反馈
 */
class SearchFeedbackActivity :
    BaseSwipeBackActivity<ActivitySearchFeedbackBinding, SearchFeedbackViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SearchFeedbackActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_search_feedback
    }

    override fun initView() {
        super.initView()

        onClick()
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
        mBinding.btnCommit.setOnClickListener {
            //如果未登陆,请登陆以后求书
            if (PirateApp.getInstance().getToken().isEmpty()) {
                LoginActivity.start(this)
            }

            //提交反馈
            if (mViewModel.checkParams(mBinding)) {
                mViewModel.requestBook(
                    mBinding.bookNameEt.text.toString(),
                    mBinding.authorEt.text.toString()
                ).compose(bindToLifecycle())
                    .subscribe({
                        niceToast("提交成功，我们会尽快收录您需要的书")
                    },{})
            }

        }
    }
}