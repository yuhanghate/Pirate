package com.yuhang.novel.pirate.viewholder

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.impl.model.WorkTypeConverters.StateIds.CANCELLED
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.databinding.ItemBookDownloadBinding
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceGlideInto
import com.yuhang.novel.pirate.listener.OnBookDownloadListener
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity
import com.yuhang.novel.pirate.utils.DownloadUtil
import java.util.*

class ItemBookDownloadVH(parent: ViewGroup) :
    BaseViewHolder<BookDownloadEntity, ItemBookDownloadBinding>(
        parent,
        R.layout.item_book_download
    ) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(obj: BookDownloadEntity, position: Int) {
        super.onBindViewHolder(obj, position)
        mBinding.titleTv.text = obj.bookName
        mBinding.authorTv.text = obj.author
        upateProgress(obj)

        /**
         * 加载头像
         */
        val drawable = mContext.getDrawable(R.drawable.ic_default_img)
        val placeholder =
            RequestOptions().transforms(CenterCrop(), RoundedCorners(mContext.niceDp2px(3f)))
                .placeholder(drawable)
                .error(drawable)

        getGlide().load(niceCoverPic(obj.cover))
            .apply(placeholder)
            .into(niceGlideInto(mBinding.coverIv))


        mBinding.btnProgress.setOnClickListener {
            getListener<OnClickItemListener>()?.onClickItemListener(it, position)
        }
    }

    /**
     * 下载状态
     */
    private fun downloadStatus(obj:BookDownloadEntity) {
        WorkManager.getInstance().getWorkInfoByIdLiveData(UUID.fromString(obj.uuid))
            .value?.state?.let {
            when (it) {
                //任务取消或者失败,暂停
                WorkInfo.State.FAILED -> mBinding.btnProgress.text = "暂停"
                WorkInfo.State.CANCELLED -> mBinding.btnProgress.text = "暂停"
                else -> mBinding.btnProgress.text = "下载"
            }
        }

    }

    /**
     * 刷新进度条
     */
    fun upateProgress(obj: BookDownloadEntity) {

        val progress = DownloadUtil.calcProgressToView(
            mBinding.progressLayout,
            obj.progress.toLong(),
            obj.total.toLong()
        )

        mBinding.progressTv.text = "${obj.progress} / ${obj.total}章"

        if (progress == 100) {
            mBinding.progressLayout.setCurrentProgress(0)
        }
    }
}