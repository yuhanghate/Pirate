package com.yuhang.novel.pirate.ui.settings.activity

import android.app.Activity
import android.content.Intent
import br.tiagohm.markdownview.css.styles.Github
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityProblemNoteBinding
import com.yuhang.novel.pirate.ui.settings.viewmodel.ProblemViewModel

/**
 * 帮助与问题
 */
class ProblemActivity :BaseSwipeBackActivity<ActivityProblemNoteBinding, ProblemViewModel>(){

    companion object{
        fun start(context: Activity) {
            val intent = Intent(context, ProblemActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_problem_note
    }

    override fun initView() {
        super.initView()

        onClick()
        initData()
    }

    private fun initData() {
        mBinding.markdownView.addStyleSheet(Github())
        mBinding.markdownView.loadMarkdownFromAsset("problem.md")
    }

    private fun onClick() {
        mBinding.btnBack.setOnClickListener { onBackPressedSupport() }
    }
}