package com.yuhang.novel.pirate.ui.settings.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.RadioButton
import android.widget.Toast
import com.github.aakira.expandablelayout.Utils
import com.vondear.rxtool.RxDeviceTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.constant.ConfigConstant
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivitySettingsBinding
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil
import com.yuhang.novel.pirate.ui.settings.viewmodel.SettingsViewModel
import com.yuhang.novel.pirate.utils.GlideCacheUtil
import kotlin.concurrent.thread


/**
 * 设置
 */
class SettingsActivity : BaseSwipeBackActivity<ActivitySettingsBinding, SettingsViewModel>() {

    private val mInterpolator: Interpolator =
        Utils.createInterpolator(Utils.LINEAR_OUT_SLOW_IN_INTERPOLATOR) as Interpolator

    private val mPageTypeList by lazy {
        arrayListOf(
            mBinding.pageVerticalRb,
            mBinding.pageHorizontalRb
        )
    }
    private val mPageTimeList by lazy {
        arrayListOf(
            mBinding.pageTimeShowRb,
            mBinding.pageTimeHideRb
        )
    }
    private val mPageTypeTitle by lazy { arrayListOf("上下翻页", "左右翻页") }
    private val mPageTimeTitle by lazy { arrayListOf("显示时间", "显示电池百分比") }

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, SettingsActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_settings
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        super.initView()
        onClick()
        initLogoutBtn()
        initCache()
        initRadioButtonTitle()
        mBinding.versionTv.setText("v ${RxDeviceTool.getAppVersionName(this)}", null)
    }

    /**
     * 显示默认标题
     */
    private fun initRadioButtonTitle() {
        mBinding.pageTypeTv.text =
            mPageTypeTitle[PreferenceUtil.getInt(
                ConfigConstant.PAGE_TYPE,
                ConfigConstant.PAGE_TYPE_HORIZONTAL
            )]
        mBinding.pageTimeTv.text =
            mPageTimeTitle[PreferenceUtil.getInt(
                ConfigConstant.PAGE_TIME,
                ConfigConstant.PAGE_TIME_SHOW
            )]
        resetRadioButton(
            mPageTypeList,
            PreferenceUtil.getInt(ConfigConstant.PAGE_TYPE, ConfigConstant.PAGE_TYPE_HORIZONTAL)
        )
        resetRadioButton(
            mPageTimeList,
            PreferenceUtil.getInt(ConfigConstant.PAGE_TIME, ConfigConstant.PAGE_TIME_SHOW)
        )
        mBinding.btnNextPage.isChecked =
            PreferenceUtil.getBoolean(BookConstant.CLICK_NEXT_PAGE, false)

    }

    /**
     * 初始化缓存数据
     */
    private fun initCache() {
        thread {
            val cacheSize = GlideCacheUtil.getInstance().getCacheSize(this)
            runOnUiThread { mBinding.sizeTv.text = cacheSize }
        }
    }

    /**
     * 没登录不显示退出按钮
     */
    @SuppressLint("CheckResult")
    private fun initLogoutBtn() {
        if (mViewModel.isLogin()) {
            mBinding.btnLogout.visibility = View.VISIBLE
        } else {
            mBinding.btnLogout.visibility = View.GONE
        }
    }

    /**
     * 点击事件
     */
    private fun onClick() {
        mBinding.pageTypeEll.setInterpolator(mInterpolator)
        mBinding.pageTimeEll.setInterpolator(mInterpolator)
        mBinding.btnBack.setOnClickListener { onBackPressed() }
        mBinding.btnPrivacy.setOnClickListener { PrivacyActivity.start(this) }
        mBinding.btnNoteLl.setOnClickListener { VersionNoteActivity.start(this) }
        mBinding.btnLogout.setOnClickListener {
            mViewModel.onUMEvent(this, UMConstant.TYPE_SETTINGS_CLICK_LOGOUT, "设置 -> 退出登录")
            mViewModel.logout()
        }
        mBinding.btnDisclaimer.setOnClickListener {
            mViewModel.onUMEvent(this, UMConstant.TYPE_SETTINGS_DISCLAIMER, "设置 -> 免责声明")
            DisclaimerActivity.start(this)
        }
        mBinding.btnClear.setOnClickListener {
            mViewModel.onUMEvent(this, UMConstant.TYPE_SETTINGS_CLEAN_CACHE, "设置 -> 清除缓存")
            clear()
        }
        mBinding.pageTypeLl.setOnClickListener {
            expandedItemRightView(mBinding.pageTypeIv, mBinding.pageTypeEll.isExpanded)
            mBinding.pageTypeEll.toggle()
        }
        mBinding.pageTimeLl.setOnClickListener {
            expandedItemRightView(mBinding.pageTimeIv, mBinding.pageTimeEll.isExpanded)
            mBinding.pageTimeEll.toggle()
        }

        //翻页方式
        mBinding.pageVerticalLl.setOnClickListener {

            mViewModel.onUMEvent(this, UMConstant.TYPE_SETTINGS_PAGE_MODEL, "上下翻页")
            resetRadioButton(mPageTypeList, ConfigConstant.PAGE_TYPE_VERTICAL)
            PreferenceUtil.commitInt(ConfigConstant.PAGE_TYPE, ConfigConstant.PAGE_TYPE_VERTICAL)
            mBinding.pageTypeTv.text = mPageTypeTitle[ConfigConstant.PAGE_TYPE_VERTICAL]
            PirateApp.getInstance().setPageType(ConfigConstant.PAGE_TYPE_VERTICAL)
        }
        mBinding.pageHorizontalLl.setOnClickListener {
            mViewModel.onUMEvent(this, UMConstant.TYPE_SETTINGS_PAGE_MODEL, "左右翻页")
            resetRadioButton(mPageTypeList, ConfigConstant.PAGE_TYPE_HORIZONTAL)
            PreferenceUtil.commitInt(ConfigConstant.PAGE_TYPE, ConfigConstant.PAGE_TYPE_HORIZONTAL)
            mBinding.pageTypeTv.text = mPageTypeTitle[ConfigConstant.PAGE_TYPE_HORIZONTAL]
            PirateApp.getInstance().setPageType(ConfigConstant.PAGE_TYPE_HORIZONTAL)
        }

        //阅读界面显示
        mBinding.pageTimeShowLl.setOnClickListener {
            mViewModel.onUMEvent(
                this,
                UMConstant.TYPE_SETTINGS_READ_MODEL,
                mPageTimeTitle[ConfigConstant.PAGE_TIME_SHOW]
            )
            PreferenceUtil.commitInt(ConfigConstant.PAGE_TIME, ConfigConstant.PAGE_TIME_SHOW)
            resetRadioButton(mPageTimeList, ConfigConstant.PAGE_TIME_SHOW)
            mBinding.pageTimeTv.text = mPageTimeTitle[ConfigConstant.PAGE_TIME_SHOW]
        }
        mBinding.pageTimeHideLl.setOnClickListener {
            mViewModel.onUMEvent(
                this,
                UMConstant.TYPE_SETTINGS_READ_MODEL,
                mPageTimeTitle[ConfigConstant.PAGE_TIME_HIDE]
            )
            PreferenceUtil.commitInt(ConfigConstant.PAGE_TIME, ConfigConstant.PAGE_TIME_HIDE)
            resetRadioButton(mPageTimeList, ConfigConstant.PAGE_TIME_HIDE)
            mBinding.pageTimeTv.text = mPageTimeTitle[ConfigConstant.PAGE_TIME_HIDE]
        }

        mBinding.btnVolume.isChecked = PreferenceUtil.getBoolean(BookConstant.VOLUME_STATUS, false)
        //使用音量键翻页
        mBinding.btnVolume.setOnCheckedChangeListener { _, status ->
            PreferenceUtil.commitBoolean(BookConstant.VOLUME_STATUS, status)
        }

        //全屏翻下页
        mBinding.btnNextPage.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtil.commitBoolean(BookConstant.CLICK_NEXT_PAGE, isChecked)
        }

        //意见反馈
        mBinding.feedbackLl.setOnClickListener {
            mViewModel.onUMEvent(this, UMConstant.TYPE_ME_CLICK_FEEDBACK, "我的 -> 意见反馈")
            mViewModel.sendEmail()
        }

        //去应用市场评分
        mBinding.btnMarket.setOnClickListener {
            try {
                val uri: Uri = Uri.parse("market://details?id=$packageName")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "您的手机没有安装Android应用市场", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    /**
     * 清除缓存
     */
    private fun clear() {
        showProgressbar("清理中...")
        thread {
            GlideCacheUtil.getInstance().clearImageAllCache(this)
            initCache()
            runOnUiThread { closeProgressbar() }
        }
    }

    /**
     * 右边图标伸展动画
     */
    private fun expandedItemRightView(view: View, isToggle: Boolean) {
        val animation: Animation? = if (isToggle) {
            AnimationUtils.loadAnimation(this, R.anim.rotate_expanded_button)

        } else {
            AnimationUtils.loadAnimation(this, R.anim.rotate_expanded_top)
        }
        animation?.interpolator = mInterpolator
        animation?.fillAfter = true
        view.startAnimation(animation)
    }

    /**
     * 单选
     */
    private fun resetRadioButton(list: List<RadioButton>, position: Int) {
        list.forEachIndexed { index, radioButton ->
            radioButton.isChecked = index == position
        }

    }

    override fun onResume() {
        super.onResume()
        mViewModel.onResume(this)

    }

    override fun onPause() {
        super.onPause()
        mViewModel.onPause(this)
    }

}