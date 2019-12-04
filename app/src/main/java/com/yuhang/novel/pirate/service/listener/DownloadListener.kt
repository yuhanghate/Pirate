package com.yuhang.novel.pirate.service.listener

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.eventbus.DownloadStatusEvent
import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameDataResult
import com.yuhang.novel.pirate.service.impl.DownloadServiceImpl
import com.yuhang.novel.pirate.ui.ad.activity.GameActivity
import com.yuhang.novel.pirate.utils.NotificationUtils
import org.greenrobot.eventbus.EventBus

class DownloadListener(val utils: NotificationUtils, val context: Context, val name: String?): FileDownloadListener() {
    override fun warn(task: BaseDownloadTask?) {
        Logger.i("")
    }

    /**
     * 下载完成
     */
    override fun completed(task: BaseDownloadTask?) {
        utils.sendNotificationProgress(
            name,
            "下载完成",
            100,
            PendingIntent.getActivity(
                context,
                100,
                Intent(context, GameActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        EventBus.getDefault().post(DownloadStatusEvent().apply {
            this.url = task?.url!!
            this.path = DownloadServiceImpl.getDownloadPath(task.url)
            this.status = DownloadServiceImpl.SERVICE_ACTION_COMPLETED
        })
    }

    /**
     * 等待
     */
    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
        EventBus.getDefault().post(DownloadStatusEvent().apply {
            this.url = task?.url!!
            this.path = DownloadServiceImpl.getDownloadPath(task.url)
            this.status = DownloadServiceImpl.SERVICE_ACTION_PENDING
        })
    }

    /**
     * 异常
     */
    override fun error(task: BaseDownloadTask?, e: Throwable?) {
        EventBus.getDefault().post(DownloadStatusEvent().apply {
            this.url = task?.url!!
            this.path = DownloadServiceImpl.getDownloadPath(task.url)
            this.status = DownloadServiceImpl.SERVICE_ACTION_ERROR
        })
    }

    /**
     * 进度
     */
    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
        task?.also {result ->
            val percent: Int = ((soFarBytes.toFloat() / totalBytes.toFloat()) * 100).toInt()
            Logger.i("progress:${percent}%")
            utils.sendNotificationProgress(name,"下载 ${percent}%",percent,
                PendingIntent.getActivity(
                    context,
                    percent,
                    Intent(context, GameActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            EventBus.getDefault().post(DownloadStatusEvent().apply {
                this.url = task.url
                this.progress = percent
                this.path = DownloadServiceImpl.getDownloadPath(task.url)
                this.status = DownloadServiceImpl.SERVICE_ACTION_PROGRESS
            })
        }
    }

    /**
     * 暂停
     */
    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
        EventBus.getDefault().post(DownloadStatusEvent().apply {
            this.url = task?.url!!
            this.path = DownloadServiceImpl.getDownloadPath(task.url)
            this.status = DownloadServiceImpl.SERVICE_ACTION_PAUSE
        })
    }
}