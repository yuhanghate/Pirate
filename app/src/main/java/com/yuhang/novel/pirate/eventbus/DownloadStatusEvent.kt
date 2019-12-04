package com.yuhang.novel.pirate.eventbus

import com.liulishuo.filedownloader.model.FileDownloadStatus
import com.yuhang.novel.pirate.service.impl.DownloadServiceImpl

/**
 * 下载服务状态通知
 */
class DownloadStatusEvent {

    /**
     * 下载地址
     */
    var url:String = ""

    /**
     * 下载路径
     */
    var path:String = ""

    /**
     * 进度
     */
    var progress = 0

    /**
     * 下载状态
     */
    var status:String = DownloadServiceImpl.SERVICE_ACTION_START
}