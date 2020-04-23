package com.yuhang.novel.pirate.service.impl

import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.yuhang.novel.pirate.constant.ConfigConstant
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.service.TtsAloudService
import com.yuhang.novel.pirate.utils.MediaHelp
import java.util.*

/**
 * TTS 朗读
 */
class TtsAloudServiceImpl : ReadAloudServiceImpl(), TtsAloudService, TextToSpeech.OnInitListener {

    private var ttsIsSuccess: Boolean = false

    companion object {
        var textToSpeech: TextToSpeech? = null

        fun clearTTS() {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
        upSpeechRate()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearTTS()
    }


    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.CHINA
//            textToSpeech?.setOnUtteranceProgressListener(TTSUtteranceListener())
            ttsIsSuccess = true
            play()
        } else {
            niceToast("TTS初始化失败")
        }
    }

    @Suppress("DEPRECATION")
    override fun play() {
        if (contentList.isEmpty() || !ttsIsSuccess) {
            return
        }
        if (requestFocus()) {
            MediaHelp.playSilentSound(this)
            super.play()
            for (i in nowSpeak until contentList.size) {
                if (i == 0) {
                    speak(contentList[i], TextToSpeech.QUEUE_FLUSH, ConfigConstant.App + i)
                } else {
                    speak(contentList[i], TextToSpeech.QUEUE_ADD, ConfigConstant.App + i)
                }
            }
        }
    }

    private fun speak(content: String, queueMode: Int, utteranceId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech?.speak(content, queueMode, null, utteranceId)
        } else {
            @Suppress("DEPRECATION")
            textToSpeech?.speak(
                content,
                queueMode,
                hashMapOf(Pair(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId))
            )
        }
    }

    /**
     * 更新朗读速度
     */
    override fun upSpeechRate(reset: Boolean) {
//        if (this.getPrefBoolean("ttsFollowSys", true)) {
//            if (reset) {
//                clearTTS()
//                textToSpeech = TextToSpeech(this, this)
//            }
//        } else {
//            textToSpeech?.setSpeechRate((1 + 5) / 10f)
//        }

        clearTTS()
        textToSpeech = TextToSpeech(this, this)
    }

    /**
     * 上一段
     */
    override fun prevP() {
        if (nowSpeak > 0) {
            textToSpeech?.stop()
            nowSpeak--
            readAloudNumber -= contentList[nowSpeak].length.minus(1)
            play()
        }
    }

    /**
     * 下一段
     */
    override fun nextP() {
        if (nowSpeak < contentList.size - 1) {
            textToSpeech?.stop()
            readAloudNumber += contentList[nowSpeak].length.plus(1)
            nowSpeak++
            play()
        }
    }

    /**
     * 暂停朗读
     */
    override fun pauseReadAloud(pause: Boolean) {
        super.pauseReadAloud(pause)
        textToSpeech?.stop()
    }

    /**
     * 恢复朗读
     */
    override fun resumeReadAloud() {
        super.resumeReadAloud()
        play()
    }

    /**
     * 朗读监听
     */
//    private inner class TTSUtteranceListener : UtteranceProgressListener() {
//
//        override fun onStart(s: String) {
//            textChapter?.let {
//                if (readAloudNumber + 1 > it.getReadLength(pageIndex + 1)) {
//                    pageIndex++
//                    ReadBook.moveToNextPage()
//                }
//            }
//            postEvent(EventBus.TTS_START, readAloudNumber + 1)
//        }
//
//        override fun onDone(s: String) {
//            readAloudNumber += contentList[nowSpeak].length + 1
//            nowSpeak++
//            if (nowSpeak >= contentList.size) {
//                nextChapter()
//            }
//        }
//
//        override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
//            super.onRangeStart(utteranceId, start, end, frame)
//            textChapter?.let {
//                if (readAloudNumber + start > it.getReadLength(pageIndex + 1)) {
//                    pageIndex++
//                    ReadBook.moveToNextPage()
//                    postEvent(EventBus.TTS_START, readAloudNumber + start)
//                }
//            }
//        }
//
//        override fun onError(s: String) {
//            launch {
//                toast(s)
//            }
//        }
//
//    }
}