package com.yuhang.novel.pirate.repository.network.data.pirate.result

import com.google.gson.annotations.Expose
import com.liulishuo.filedownloader.DownloadTask

class GameDataResult {

    /**
     * 游戏图片
     */
    var image:String = ""

    /**
     * 游戏名称
     */
    var name:String = ""

    /**
     * 游戏类型
     */
    var gameType:String = ""

    /**
     * 游戏大小
     */
    var size:Long = 0

    /**
     * 游戏简介
     */
    var description:String = ""

    /**
     * 下载地址
     */
    var downloadUrl: String = ""

    /**
     * 包名
     */
    var packageName :String = ""

    /**
     * 下载任务
     */
//    @Expose
//    var task: DownloadTask? = null
}