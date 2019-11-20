package com.yuhang.novel.pirate.listener

import co.mobiwise.library.ProgressLayout
import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameDataResult

/**
 * 游戏推荐点击事件
 */
interface OnClickGameDownloadListener {

    /**
     * 点击开始下载
     */
    fun onGameDownloadStartListener(view:ProgressLayout, obj:GameDataResult, position:Int)

    /**
     * 点击暂停
     */
    fun onGameDownloadPauseListener(view:ProgressLayout, obj:GameDataResult, position: Int)

    /**
     * 点击打开
     */
    fun onGameDownloadOpenListener(view:ProgressLayout, obj:GameDataResult, position: Int)

    /**
     * 安装
     */
    fun onGameDownloadInstallListener(view:ProgressLayout, obj:GameDataResult, position: Int)
}