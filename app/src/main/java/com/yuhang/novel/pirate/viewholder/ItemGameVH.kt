package com.yuhang.novel.pirate.viewholder

import android.annotation.SuppressLint
import android.view.ViewGroup
import co.mobiwise.library.ProgressLayoutListener
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.liulishuo.okdownload.StatusUtil
import com.vondear.rxtool.*
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemGameBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickGameDownloadListener
import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameDataResult
import java.io.File

class ItemGameVH(parent: ViewGroup) :
    BaseViewHolder<GameDataResult, ItemGameBinding>(parent, R.layout.item_game),
    ProgressLayoutListener {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(obj: GameDataResult, position: Int) {
        super.onBindViewHolder(obj, position)



        mBinding.nameTv.text = obj.name
        mBinding.descTv.text = obj.description


        val placeholder =
            RequestOptions().transforms(CenterCrop(), RoundedCorners(mContext.niceDp2px(6f)))
                .placeholder(R.drawable.ic_default_img_5dp)
                .error(R.drawable.ic_default_img_5dp)
        getGlide().load(obj.image)
            .apply(placeholder)
            .into(mBinding.avatarIv)

        onClick(obj, position)
        initDownloadStatus(obj)
    }

    /**
     * 初始化下载按钮状态
     */
    private fun initDownloadStatus(obj: GameDataResult) {

        when (StatusUtil.getStatus(obj.task!!)) {
            StatusUtil.Status.COMPLETED -> mBinding.downloadTv.text = "安装"
            StatusUtil.Status.IDLE -> {
                mBinding.downloadTv.text = "下载"
            }
            StatusUtil.Status.RUNNING -> {
                mBinding.downloadTv.text = "暂停"
            }
            StatusUtil.Status.PENDING -> {
                mBinding.downloadTv.text = "暂停"
            }
            //任务完成,判断文件是否存在
            StatusUtil.Status.UNKNOWN -> {
                val path = RxFileTool.getCacheFolder(mContext).absolutePath+File.separator+"${RxTool.Md5(obj.downloadUrl)}.apk"
                if (RxFileTool.isFileExists(path)) {
                    mBinding.downloadTv.text = "安装"
                } else {
                    //没有文件就下载
                    mBinding.downloadTv.text = "下载"
                }
            }
        }
        if (RxAppTool.isInstallApp(mContext, obj.packageName)) {
            mBinding.downloadTv.text = "打开"
        }

    }

    /**
     * 点击事件
     */
    private fun onClick(obj: GameDataResult, position: Int) {
        mBinding.progressLayout.setProgressLayoutListener(this, obj)
        mBinding.downloadTv.setOnClickListener {
            when (mBinding.downloadTv.text.toString()) {
                "下载" -> {
                    mBinding.downloadTv.text = "暂停"
                    getListener<OnClickGameDownloadListener>()?.onGameDownloadStartListener(
                        mBinding.progressLayout,
                        obj,
                        position
                    )
                }
                "暂停" -> {
                    mBinding.downloadTv.text = "下载"
                    getListener<OnClickGameDownloadListener>()?.onGameDownloadPauseListener(
                        mBinding.progressLayout,
                        obj,
                        position
                    )
                }
                "安装" -> {
                    getListener<OnClickGameDownloadListener>()?.onGameDownloadInstallListener(
                        mBinding.progressLayout,
                        obj,
                        position
                    )
                }
                "打开" -> {
                    getListener<OnClickGameDownloadListener>()?.onGameDownloadOpenListener(
                        mBinding.progressLayout,
                        obj,
                        position
                    )
                }
            }
        }
    }

    override fun onProgressChanged(seconds: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressCompleted(obj: Any?) {
        val result = obj as? GameDataResult ?: return
        val isInstall = RxAppTool.isInstallApp(mContext, result.packageName)
        if (!isInstall) {
            mBinding.downloadTv.text = "安装"
        } else {
            mBinding.downloadTv.text = "打开"
        }

        val length = obj.task?.file?.length()?:0
        if (length > 0) {
            mBinding.typeTv.text = "${obj.gameType} | ${RxDataTool.byte2FitSize(length)}"
        } else {
            mBinding.typeTv.text = obj.gameType
        }
    }
}