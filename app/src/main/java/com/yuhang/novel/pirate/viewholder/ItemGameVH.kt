package com.yuhang.novel.pirate.viewholder

import android.annotation.SuppressLint
import android.view.ViewGroup
import co.mobiwise.library.ProgressLayoutListener
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.liulishuo.filedownloader.model.FileDownloadStatus
import com.vondear.rxtool.RxAppTool
import com.vondear.rxtool.RxDataTool
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemGameBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceCrossFade
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickGameDownloadListener
import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameDataResult
import com.yuhang.novel.pirate.service.impl.DownloadServiceImpl

class ItemGameVH(parent: ViewGroup) :
    BaseViewHolder<GameDataResult, ItemGameBinding>(parent, R.layout.item_game),
    ProgressLayoutListener {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(obj: GameDataResult, position: Int) {
        super.onBindViewHolder(obj, position)



        mBinding.nameTv.text = obj.name
        mBinding.descTv.text = obj.description
        mBinding.typeTv.text = "${obj.gameType} | ${RxDataTool.byte2FitSize(obj.size)}"


        val placeholder =
            RequestOptions().transforms(CenterCrop(), RoundedCorners(mContext.niceDp2px(20f)))
                .placeholder(R.drawable.ic_default_img_20dp)
                .error(R.drawable.ic_default_img_20dp)
        getGlide().load(obj.image)
            .apply(placeholder)
            .transition(niceCrossFade())
            .into(mBinding.avatarIv)

        onClick(obj, position)
        initDownloadStatus(obj, position)
    }

    /**
     * 初始化下载按钮状态
     */
    private fun initDownloadStatus(obj: GameDataResult, position: Int) {

        when (DownloadServiceImpl.getDownloadStatus(obj.downloadUrl)) {
           FileDownloadStatus.INVALID_STATUS ->  {
               mBinding.downloadTv.text = "下载"
               mBinding.progressLayout.stop()
           }
            FileDownloadStatus.error ->  {
                mBinding.downloadTv.text = "重试"
                mBinding.progressLayout.stop()
            }
            FileDownloadStatus.progress ->  {
                mBinding.downloadTv.text = "暂停"
                mBinding.progressLayout.start()
            }
            FileDownloadStatus.completed ->  {
                mBinding.downloadTv.text = "安装"
                mBinding.progressLayout.cancel()
            }
            FileDownloadStatus.paused ->  {
                mBinding.downloadTv.text = "继续"
                mBinding.progressLayout.stop()
            }
            FileDownloadStatus.pending ->  {
                mBinding.downloadTv.text = "暂停"
                mBinding.progressLayout.start()
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
        mBinding.downloadTv.clickWithTrigger {
            getListener<OnClickGameDownloadListener>()?.onGameDownloadListener(obj, position)
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

    }
}