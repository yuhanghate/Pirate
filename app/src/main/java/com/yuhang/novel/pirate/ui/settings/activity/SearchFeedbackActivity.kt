package com.yuhang.novel.pirate.ui.settings.activity

import android.content.Context
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivitySearchFeedbackBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.ui.settings.viewmodel.SearchFeedbackViewModel
import com.yuhang.novel.pirate.ui.user.activity.LoginActivity
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

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
        mBinding.btnBack.clickWithTrigger { onBackPressedSupport() }
        mBinding.btnCommit.clickWithTrigger {
            //如果未登陆,请登陆以后求书
            if (PirateApp.getInstance().getToken().isEmpty()) {
                LoginActivity.start(this)
            }

            //提交反馈
            if (mViewModel.checkParams(mBinding)) {
                lifecycleScope.launch {
                    flow {
                        mViewModel.requestBook(
                            mBinding.bookNameEt.text.toString(),
                            mBinding.authorEt.text.toString()
                        )
                        emit(Unit)
                    }
                        .catch { Logger.e(it.message ?: "") }
                        .collect { niceToast("提交成功，我们会尽快收录您需要的书") }
                }
            }

        }
    }
}