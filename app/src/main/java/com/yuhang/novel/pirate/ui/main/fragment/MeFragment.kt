package com.yuhang.novel.pirate.ui.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import cc.shinichi.library.tool.text.MD5Util
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Progress
import com.netease.nim.demo.koltinapplication.repository.network.NetURL
import com.orhanobut.logger.Logger
import com.vondear.rxtool.RxAppTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.DialogVersionUpdateBinding
import com.yuhang.novel.pirate.databinding.FragmentMeBinding
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.network.data.pirate.result.VersionResult
import com.yuhang.novel.pirate.ui.book.activity.ReadHistoryActivity
import com.yuhang.novel.pirate.ui.main.viewmodel.MeViewModel
import com.yuhang.novel.pirate.ui.user.activity.LoginActivity
import com.yuhang.novel.pirate.utils.DownloadUtil
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File


/**
 * 我的
 */
@RuntimePermissions
class MeFragment : BaseFragment<FragmentMeBinding, MeViewModel>() {


    /**
     * 第一次加载
     */
    private var isInitView = false

    var binding: DialogVersionUpdateBinding? = null

    var dialog: AlertDialog? = null
    override fun onLayoutId(): Int {
        return R.layout.fragment_me
    }

    override fun initView() {
        super.initView()
//        FileDownloader.setup(mActivity)
        onClick()
        checkVersionWithPermissionCheck()
    }

    private fun onClick() {
        //分享
        mBinding.shareCl.setOnClickListener { showShareDialog() }
        //意见反馈
        mBinding.feedbackCl.setOnClickListener { sendEmail() }
        //最新浏览
        mBinding.historyCl.setOnClickListener { ReadHistoryActivity.start(mActivity!!) }
        //登陆界面
        mBinding.btnLogin.setOnClickListener { LoginActivity.start(mActivity!!) }
        //登陆
        mBinding.avatarCiv.setOnClickListener { LoginActivity.start(mActivity!!) }
        //检测版本更新
        mBinding.checkVersionCl.setOnClickListener {
            isInitView = true
            checkVersionWithPermissionCheck()
        }
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

    /**
     * 意见反馈
     */
    private fun sendEmail() {
        val data = Intent(Intent.ACTION_SENDTO);
        data.data = Uri.parse("mailto:yh714610354@gmail.com")
        data.putExtra(
            Intent.EXTRA_SUBJECT,
            "我对App有话说[${android.os.Build.BRAND}/${android.os.Build.MODEL}/${android.os.Build.VERSION.RELEASE}/随便看看]"
        )
        data.putExtra(Intent.EXTRA_TEXT, "")
        startActivity(data)
    }

    /**
     * 版本升级
     */
    @SuppressLint("CheckResult")
    @NeedsPermission(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun checkVersion() {
//        mActivity?.showProgressbar()
        mViewModel.checkVersion()
            .compose(bindToLifecycle())
            .subscribe({
                mActivity?.closeProgressbar()
                if (it.update == "Yes") {
                    mBinding.versionNameTv.text = "更新"

                    if (isInitView) {
                        showVersionUpdateDialog(it)
                        isInitView = false
                    }

                } else {
                    if (isInitView) {
                        niceToast("当前已是最新版本")
                        isInitView = false
                    }

                    mBinding.versionNameTv.text = "当前已是最新版本"
                }
            }, {
                niceToast("检测版本失败")
            })
    }


    /**
     * 新版本Dialog
     */
    private fun showVersionUpdateDialog(result: VersionResult) {

// 构建 OkHttpClient 时,将 OkHttpClient.Builder() 传入 with() 方法,进行初始化配置
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setTitle("检测到新版本")
        builder.setMessage(getMessage(result))
        //点击对话框以外的区域是否让对话框消失
        builder.setCancelable(true)
        builder.setNegativeButton("更新") { p0, p1 -> showVersionUpdateProgress(result) }
        builder.setPositiveButton("取消") { p0, p1 -> }
        builder.show()
    }

    /**
     * 更新进度条显示
     */
    @SuppressLint("CheckResult", "SetTextI18n")
    private fun showVersionUpdateProgress(result: VersionResult) {
        binding = DialogVersionUpdateBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setView(binding?.root)
        builder.setCancelable(false)
        dialog = builder.show()


        /**
         * 下载APK文件
         */
        val url = "${NetURL.HOST_RESOUCE}${result.apkFileUrl}"
        val outputApk = DownloadUtil.PATH_CHALLENGE_VIDEO + File.separator + MD5Util.md5Encode(url) + ".apk"
        OkGo.get<File>(url).execute(object :
            com.lzy.okgo.callback.FileCallback(DownloadUtil.PATH_CHALLENGE_VIDEO, MD5Util.md5Encode(url) + ".apk") {
            override fun onSuccess(response: com.lzy.okgo.model.Response<File>) {
                dialog?.dismiss()
                RxAppTool.installApp(mActivity, outputApk)
            }

            override fun onStart(request: com.lzy.okgo.request.base.Request<File, out com.lzy.okgo.request.base.Request<*, *>>?) {
                super.onStart(request)

            }

            override fun onError(response: com.lzy.okgo.model.Response<File>) {
                super.onError(response)
                dialog?.dismiss()
                niceToast("下载异常,请重试")
            }

            override fun downloadProgress(progress: Progress?) {
                super.downloadProgress(progress)

                val currentProgress = (progress?.fraction!! * 100).toInt()
                Logger.i("fraction = $currentProgress")
                binding?.progressTv?.text = "$currentProgress%"
                binding?.progressHorizontal?.progress = currentProgress
            }
        })
    }

    private fun getMessage(result: VersionResult): String {
        return StringBuilder()
            .append("\n\r")
            .append("建议在WLAN环境下进行升级")
            .append("\n\r\n\r")
            .append("版本: ${result.newVersion}")
            .append("\n\r\n\r")
            .append("大小: ${result.targetSize}")
            .append("\n\r\n\r")
            .append("更新说明:")
            .append("\n\r")
            .append(
                "1.小伙伴们有更新啦~~~攻城狮们正努力优化体验。。。。。\n" +
                        "2.修复已知Bug~~~"
            )
            .append("\n\r")
            .toString()
    }


//    override fun onProgress(progressInfo: ProgressInfo?) {
//        binding?.progressTv?.text = "${progressInfo?.speed}%"
//        binding?.progressHorizontal?.progress = progressInfo?.speed?.toInt()!!
//    }
//
//    override fun onError(id: Long, e: Exception?) {
//        dialog?.dismiss()
//        niceToast("更新失败")
//    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }


}