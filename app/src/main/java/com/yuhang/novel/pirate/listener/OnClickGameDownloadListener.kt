package com.yuhang.novel.pirate.listener

import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameDataResult
import com.yuhang.novel.pirate.widget.progressLayout.ProgressLayout

/**
 * 游戏推荐点击事件
 */
interface OnClickGameDownloadListener {

    /**
     * 点击开始下载
     */
    fun onGameDownloadStartListener(view: ProgressLayout, obj:GameDataResult, position:Int)



    /**
     * 按钮点击事件
     * 不区分状态
     */
    fun onGameDownloadListener(obj:GameDataResult, position: Int)
}