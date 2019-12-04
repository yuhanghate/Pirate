package com.yuhang.novel.pirate.service


interface DownloadService {

    /**
     * 开始任务
     */
    fun onStartTask()

    /**
     * 暂停任务
     */
    fun onPauseTask()

    /**
     * 恢复任务
     */
    fun onResume()

    /**
     * 取消任务
     */
    fun onCancelTask()

}