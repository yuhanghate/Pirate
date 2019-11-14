package com.yuhang.novel.pirate.ui.settings.activity

import android.content.Context
import android.content.Intent
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivitySearchFeedbackBinding
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.ui.settings.viewmodel.SearchFeedbackViewModel

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