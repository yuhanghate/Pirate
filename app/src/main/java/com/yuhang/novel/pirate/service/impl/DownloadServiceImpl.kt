package com.yuhang.novel.pirate.service.impl

import android.app.Activity
import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.model.FileDownloadStatus
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.vondear.rxtool.RxFileTool
import com.vondear.rxtool.RxTool
import com.yuhang.novel.pirate.eventbus.DownloadStatusEvent
import com.yuhang.novel.pirate.service.listener.DownloadListener
import com.yuhang.novel.pirate.utils.NotificationUtils
import org.greenrobot.eventbus.EventBus
import java.io.File
import kotlin.properties.Delegates

/**
 * 下载服务
 */
class DownloadServiceImpl : IntentService("DownloadService") {

    var utils: NotificationUtils by Delegates.notNull()

    //任务id
    var taskId: Int = 0

    //应用名称
    var name: String = ""

    /**
     * 下载任务id
     */
    val download = hashMapOf<String, Int>()

    companion object {


        //安装应用
        const val SERVICE_ACTION_INSTALL = "install"
        //暂停任务
        const val SERVICE_ACTION_PAUSE = "pause"
        //开始任务
        const val SERVICE_ACTION_START = "start"
        //下载进度
        const val SERVICE_ACTION_PROGRESS = "progress"
        //等待
        const val SERVICE_ACTION_PENDING = "pending"
        //异常
        const val SERVICE_ACTION_ERROR = "error"
        //完成
        const val SERVICE_ACTION_COMPLETED = "completed"
        //删除任务
        const val SERVICE_ACTION_DELETE = "delete"

        var path =
            FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "download" + File.separator

        //打开服务
        fun start(context: Activity, url: String, name: String) {
            Intent(context, DownloadServiceImpl::class.java).also { intent ->
                intent.putExtra("download_url", url)
                intent.putExtra("app_name", name)
                context.startService(intent)
            }
        }


        /**
         * 获取下载地址
         */
        fun getDownloadPath(url: String): String {
            return path + RxTool.Md5(url) + ".apk"
        }

        /**
         * 下载状态
         */
        fun getDownloadStatus(url: String): Byte {
            return FileDownloader.getImpl().getStatus(url, getDownloadPath(url))
        }

        /**
         * 删除任务
         */
        fun deleteDownload(context: Context, url: String) {
            Intent(context, DownloadServiceImpl::class.java).also { intent ->
                intent.putExtra("delete_download", url)
                context.startService(intent)
            }
        }

        /**
         * 获取下载id
         */
        fun getDownloadid(url: String): Int {
            return FileDownloadUtils.generateId(url, getDownloadPath(url))
        }
    }


    override fun onCreate() {
        super.onCreate()
        utils = NotificationUtils(this)
    }

    /**
     * 开始下载
     */
    override fun onHandleIntent(intent: Intent?) {
        intent?.getStringExtra("app_name")?.also { name = it }

        //运行任务
        intent?.getStringExtra("download_url")?.also {
            RxFileTool.createOrExistsDir(path)
            when (getDownloadStatus(it)) {
                //任务未创建
                FileDownloadStatus.INVALID_STATUS -> createDownload(it)
                //任务完成 -> 安装应用
                FileDownloadStatus.completed -> installApplication(it)
                //任务暂停 -> 开始任务
                FileDownloadStatus.paused -> createDownload(it)
                //任务异常 -> 开始任务
                FileDownloadStatus.error -> createDownload(it)
                //任务下载中 -> 暂停任务
                FileDownloadStatus.progress -> pauseDownload(it)
                //任务等待中 -> 暂停任务
                FileDownloadStatus.pending -> pauseDownload(it)

            }
        }
        //删除任务
        intent?.getStringExtra("delete_download")?.also { url ->
            val downloadid = FileDownloadUtils.generateId(url, getDownloadPath(url))
            FileDownloader.getImpl().clear(downloadid, getDownloadPath(url))
            utils.clearProgress()

            EventBus.getDefault().post(DownloadStatusEvent().apply {
                this.url = url
                this.path = getDownloadPath(url)
                this.status = SERVICE_ACTION_DELETE
            })
        }
    }

    /**
     * 创建下载
     */
    private fun createDownload(url: String) {
        val downloadid = FileDownloader.getImpl().create(url)
            .setPath(getDownloadPath(url))
            .setCallbackProgressTimes(300)
            .setMinIntervalUpdateSpeed(400)
            .setListener(DownloadListener(utils, context = this, name = name))
            .start()
        download[url] = downloadid
        EventBus.getDefault().post(DownloadStatusEvent().apply {
            this.url = url
            this.path = getDownloadPath(url)
            this.status = SERVICE_ACTION_START
        })
    }

    /**
     * 安装应用
     */
    private fun installApplication(url: String) {
        EventBus.getDefault().post(DownloadStatusEvent().apply {
            this.url = url
            this.path = getDownloadPath(url)
            this.status = SERVICE_ACTION_INSTALL
        })
    }

    /**
     * 开始下载
     */
    private fun startDownload(url: String) {
        FileDownloader.getImpl().start(DownloadListener(utils, context = this, name = name), false)
        EventBus.getDefault().post(DownloadStatusEvent().apply {
            this.url = url
            this.path = getDownloadPath(url)
            this.status = SERVICE_ACTION_START
        })
    }

    /**
     * 暂停下载
     */
    private fun pauseDownload(url: String) {
        FileDownloader.getImpl().pause(getDownloadid(url))
        EventBus.getDefault().post(DownloadStatusEvent().apply {
            this.url = url
            this.path = getDownloadPath(url)
            this.status = SERVICE_ACTION_PAUSE
        })
    }


}