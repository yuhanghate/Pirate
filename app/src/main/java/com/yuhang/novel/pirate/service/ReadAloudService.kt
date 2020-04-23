package com.yuhang.novel.pirate.service

import android.content.Context
import android.content.Intent
import com.yuhang.novel.pirate.constant.sealed.AloudSealed
import com.yuhang.novel.pirate.repository.audio.model.AloudModel
import com.yuhang.novel.pirate.service.impl.ReadAloudServiceImpl

/**
 * 朗读服务
 */
interface ReadAloudService {

    companion object{

        /**
         * 打开服务
         */
        fun play(context: Context, model:AloudModel){
            val intent = Intent(context, ReadAloudServiceImpl::class.java)
            intent.action = AloudSealed.Play.toString()
            context.startService(intent)
        }

        /**
         * 结束服务
         */
        fun stop(context: Context){
            if (!ReadAloudServiceImpl.isRuning) return
            val intent = Intent(context, ReadAloudServiceImpl::class.java)
            intent.action = AloudSealed.Stop.toString()
            context.startService(intent)
        }

        /**
         * 对应生命周期
         */
        fun resume(context: Context){
            if (!ReadAloudServiceImpl.isRuning) return
            val intent = Intent(context, ReadAloudServiceImpl::class.java)
            intent.action = AloudSealed.Resume.toString()
            context.startService(intent)
        }

        /**
         * 对应生命周期
         */
        fun pause(context: Context){
            if (!ReadAloudServiceImpl.isRuning) return
            val intent = Intent(context, ReadAloudServiceImpl::class.java)
            intent.action = AloudSealed.Pause.toString()
            context.startService(intent)
        }

        /**
         * 上一段落
         */
        fun prevParagraph(context: Context){
            if (!ReadAloudServiceImpl.isRuning) return
            val intent = Intent(context, ReadAloudServiceImpl::class.java)
            intent.action = AloudSealed.PrevParagraph.toString()
            context.startService(intent)
        }

        /**
         * 下一段落
         */
        fun nextParagraph(context: Context){
            if (!ReadAloudServiceImpl.isRuning) return
            val intent = Intent(context, ReadAloudServiceImpl::class.java)
            intent.action = AloudSealed.NextParagraph.toString()
            context.startService(intent)
        }

        /**
         * 更新 TTS 语速
         */
        fun upTtsSpeechRate(context: Context){
            if (!ReadAloudServiceImpl.isRuning) return
            val intent = Intent(context, ReadAloudServiceImpl::class.java)
            intent.action = AloudSealed.UpdateTtsSpeechRate.toString()
            context.startService(intent)
        }

        /**
         * 设置定时
         */
        fun setTimer(context: Context, minute: Int){
            if (!ReadAloudServiceImpl.isRuning) return
            val intent = Intent(context, ReadAloudServiceImpl::class.java)
            intent.action = AloudSealed.SetTimer.toString()
            context.startService(intent)
        }

    }


}