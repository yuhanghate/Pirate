package com.yuhang.novel.pirate.ui.main.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.navigation.navOptions
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.gyb.live.mitang.extension.niceToast
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.databinding.FragmentMeBinding
import com.yuhang.novel.pirate.extension.findNavController
import com.yuhang.novel.pirate.ui.book.activity.ReadHistoryActivity
import com.yuhang.novel.pirate.ui.main.viewmodel.MeViewModel

/**
 * 我的
 */
class MeFragment : BaseFragment<FragmentMeBinding, MeViewModel>() {
    override fun onLayoutId(): Int {
        return R.layout.fragment_me
    }

    override fun initView() {
        super.initView()

        onClick()
    }

    private fun onClick() {
        //分享
        mBinding.shareCl.setOnClickListener { showShareDialog() }

        //意见反馈
        mBinding.feedbackCl.setOnClickListener { sendEmail() }

        //最新浏览
        mBinding.historyCl.setOnClickListener { ReadHistoryActivity.start(mActivity!!) }
    }

    /**
     * 分享Dialog
     */
    private fun showShareDialog() {
        MaterialDialog(mActivity!!).show {
            title(text = "温馨提示")
            message(text = "链接复制成功,请分享给您的好友.发送给好友的复制内容是:\n\r \n\r我正在用随便看看APP看免费百万本小说。下载地址 https://fir.im/a9u7")
            negativeButton(text = "取消")
            positiveButton(text = "分享", click = object : DialogCallback {
                override fun invoke(p1: MaterialDialog) {
                    //获取剪贴板管理器：
                    val cm = mActivity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                    // 创建普通字符型ClipData
                    val mClipData = ClipData.newPlainText("Label", "我正在用随便看看APP看免费百万本小说。下载地址 https://fir.im/a9u7")
                    // 将ClipData内容放到系统剪贴板里。
                    cm!!.setPrimaryClip(mClipData)
                    niceToast("复制成功,可以分享给朋友了")
                }
            })
        }
    }

    private fun sendEmail() {
        val data = Intent(Intent.ACTION_SENDTO);
        data.data = Uri.parse("mailto:yh714610354@gmail.com")
        data.putExtra(Intent.EXTRA_SUBJECT, "我对App有话说[${android.os.Build.BRAND}/${android.os.Build.MODEL}/${android.os.Build.VERSION.RELEASE}/随便看看]")
        data.putExtra(Intent.EXTRA_TEXT, "")
        startActivity(data)
    }
}