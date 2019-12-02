package com.yuhang.novel.pirate.service

/**
 * 朗读服务
 */
interface ReadAloudService {

    /**
     * 开始
     */
    fun start()

    /**
     * 暂停
     */
    fun pause()

    /**
     * 继续
     */
    fun resume()

    /**
     * 停止
     */
    fun stop()

}