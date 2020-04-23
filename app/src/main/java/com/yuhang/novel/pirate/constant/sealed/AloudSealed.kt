package com.yuhang.novel.pirate.constant.sealed

/**
 * 朗读功能
 */
sealed class AloudSealed {

    object Start : AloudSealed() //开始

    object Play : AloudSealed() //播放

    object Stop : AloudSealed() //停止

    object Resume : AloudSealed() //播放中

    object Pause : AloudSealed() //暂停

    object AddTimer : AloudSealed() //增加定时任务

    object SetTimer : AloudSealed() //设置定时任务

    object PrevParagraph : AloudSealed() //上一段落

    object NextParagraph : AloudSealed() // 下一段落

    object UpdateTtsSpeechRate : AloudSealed() //更新 TTS 语速

    object Prev : AloudSealed() //上一章有声小说

    object Next : AloudSealed() //下一章有声小说

    /**
     * 通知 ID
     */
    object Notification{
        const val ID = 1144771 //通知 ID
        const val CHANNEL = "channel_read_aloud" //通知 渠道名
    }

}