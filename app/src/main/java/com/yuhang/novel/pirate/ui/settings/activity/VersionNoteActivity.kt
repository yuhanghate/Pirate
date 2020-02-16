package com.yuhang.novel.pirate.ui.settings.activity

import android.app.Activity
import android.content.Intent
import br.tiagohm.markdownview.css.styles.Github
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityVersionNoteBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.ui.settings.viewmodel.VersionNoteViewModel


/**
 * 版本说明
 */
class VersionNoteActivity : BaseSwipeBackActivity<ActivityVersionNoteBinding, VersionNoteViewModel>() {

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, VersionNoteActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return com.yuhang.novel.pirate.R.layout.activity_version_note
    }

    override fun initView() {
        super.initView()

        onClick()
    }


    override fun initData() {
        mBinding.markdownView.addStyleSheet(Github())
        mBinding.markdownView.loadMarkdownFromAsset("version_note.md")
    }

    private fun onClick() {
        mBinding.btnBack.clickWithTrigger { onBackPressedSupport() }
    }
}