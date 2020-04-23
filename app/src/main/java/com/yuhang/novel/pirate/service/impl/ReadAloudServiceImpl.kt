package com.yuhang.novel.pirate.service.impl

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.CallSuper
import androidx.core.app.NotificationCompat
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.constant.sealed.AloudSealed
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.receiver.MediaButtonReceiver
import com.yuhang.novel.pirate.repository.audio.model.AloudModel
import com.yuhang.novel.pirate.service.ReadAloudService
import com.yuhang.novel.pirate.ui.book.activity.ReadBookActivity
import com.yuhang.novel.pirate.utils.IntentHelp
import com.yuhang.novel.pirate.utils.MediaHelp

/**
 * 朗读模块
 */
abstract open class ReadAloudServiceImpl : Service(), ReadAloudService, AudioManager.OnAudioFocusChangeListener {


    companion object {
        var isRuning: Boolean = false //播放中状态

        var pause = true //是否暂停状态

        var timeMinute: Int = 0 //朗读时间

        /**
         * 是否播放
         */
        fun isPlay(): Boolean {
            return isRuning && !pause
        }
    }

    internal val handler = Handler()
    private lateinit var audioManager: AudioManager
    private var mFocusRequest: AudioFocusRequest? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    private var mediaSessionCompat: MediaSessionCompat? = null
    private var title: String = ""
    private var subtitle: String = ""
    internal val contentList = arrayListOf<String>()
    internal var nowSpeak: Int = 0
    internal var readAloudNumber: Int = 0
    internal var pageIndex = 0

    lateinit var aloudModel: AloudModel

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        isRuning = true
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mFocusRequest = MediaHelp.getFocusRequest(this)
        initMediaSession()
        initBroadcastReceiver()
        upNotification()
        upMediaSessionPlaybackState(PlaybackStateCompat.STATE_PLAYING)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRuning = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val onStartCommand = super.onStartCommand(intent, flags, startId)
        intent ?: return onStartCommand

        aloudModel = intent.getParcelableExtra<AloudModel>(AloudModel.TAG) ?: return onStartCommand
        aloudModel.text

        when (intent.action) {
            AloudSealed.Start.toString() -> {
            }
            AloudSealed.Stop.toString() -> {
            }
            AloudSealed.Play.toString() -> {
            }
            AloudSealed.Resume.toString() -> {
            }
            AloudSealed.Pause.toString() -> {
            }
            AloudSealed.AddTimer.toString() -> {
            }
            AloudSealed.SetTimer.toString() -> {
            }
            AloudSealed.PrevParagraph.toString() -> {
            }
            AloudSealed.NextParagraph.toString() -> {
            }
            AloudSealed.UpdateTtsSpeechRate.toString() -> {
            }

        }
        return onStartCommand
    }

    /**
     * 初始化MediaSession, 注册多媒体按钮
     */
    private fun initMediaSession() {
        mediaSessionCompat = MediaSessionCompat(this, "readAloud")
        mediaSessionCompat?.setCallback(object : MediaSessionCompat.Callback() {
            override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                mediaButtonEvent?.let {
                    MediaButtonReceiver.handleIntent(
                        this@ReadAloudServiceImpl,
                        it
                    )
                }
                return super.onMediaButtonEvent(mediaButtonEvent)
            }
        })
        mediaSessionCompat?.setMediaButtonReceiver(
            PendingIntent.getBroadcast(
                this, 0,
                Intent(
                    Intent.ACTION_MEDIA_BUTTON,
                    null,
                    PirateApp.mInstance,
                    MediaButtonReceiver::class.java
                ),
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        )
        mediaSessionCompat?.isActive = true
    }

    /**
     * 断开耳机监听
     */
    private fun initBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                    pauseReadAloud(true)
                }
            }
        }
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    @CallSuper
    open fun pauseReadAloud(pause: Boolean) {
        ReadAloudServiceImpl.pause = pause
        upNotification()
        upMediaSessionPlaybackState(PlaybackStateCompat.STATE_PAUSED)
    }

    @CallSuper
    open fun resumeReadAloud() {
        pause = false
        upMediaSessionPlaybackState(PlaybackStateCompat.STATE_PLAYING)
    }

    /**
     * 更新媒体状态
     */
    private fun upMediaSessionPlaybackState(state: Int) {
        mediaSessionCompat?.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(MediaHelp.MEDIA_SESSION_ACTIONS)
                .setState(state, nowSpeak.toLong(), 1f)
                .build()
        )
    }

    /**
     * 更新通知
     */
    private fun upNotification() {
        var nTitle: String = when {
            pause -> "朗读暂停"
            timeMinute in 1..60 -> "正在朗读(还剩${timeMinute}分钟)"
            else -> "正在朗读"
        }
        nTitle += ": $title"
        var nSubtitle = subtitle
        if (subtitle.isEmpty())
            nSubtitle = "点击打开阅读界面"



        PirateApp.getInstance().getDataRepository().queryBookInfo(aloudModel.bookid)?.let {
            ReadBookActivity.start(this@ReadAloudServiceImpl, it.niceBooksResult(), false)
            val intent = Intent(this, ReadBookActivity::class.java)
            intent.putExtra(ReadBookActivity.BOOKS_RESULT, it.niceBooksResult().toJson())
            intent.putExtra(ReadBookActivity.IS_INIT_CHAPTER, false)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val builder = NotificationCompat.Builder(this, AloudSealed.Notification.CHANNEL)
                .setSmallIcon(R.drawable.ic_volume_up)
                .setOngoing(true)
                .setContentTitle(nTitle)
                .setContentText(nSubtitle)
                .setContentIntent(pendingIntent)
            if (pause) {
                builder.addAction(
                    R.drawable.ic_play_24dp,
                    "继续",
                    aloudServiceIntent(AloudSealed.Resume)
                )
            } else {
                builder.addAction(
                    R.drawable.ic_pause_24dp,
                    "暂停",
                    aloudServiceIntent(AloudSealed.Pause)
                )
            }
            builder.addAction(
                R.drawable.ic_stop_black_24dp,
                "停止",
                aloudServiceIntent(AloudSealed.Stop)
            )
            builder.addAction(
                R.drawable.ic_time_add_24dp,
                "定时",
                aloudServiceIntent(AloudSealed.AddTimer)
            )
            builder.setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionCompat?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            val notification = builder.build()
            startForeground(AloudSealed.Notification.ID, notification)
        }


    }

    /**
     * 音频焦点变化
     */
    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // 重新获得焦点,  可做恢复播放，恢复后台音量的操作
                if (!pause) resumeReadAloud()
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                // 永久丢失焦点除非重新主动获取，这种情况是被其他播放器抢去了焦点，  为避免与其他播放器混音，可将音乐暂停
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // 暂时丢失焦点，这种情况是被其他应用申请了短暂的焦点，可压低后台音量
                if (!pause) pauseReadAloud(false)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // 短暂丢失焦点，这种情况是被其他应用申请了短暂的焦点希望其他声音能压低音量（或者关闭声音）凸显这个声音（比如短信提示音），
            }
        }
    }

    /**
     * @return 音频焦点
     */
    fun requestFocus(): Boolean {
        return MediaHelp.requestFocus(audioManager, this, mFocusRequest)
    }

    private fun aloudServiceIntent(aloudSealed: AloudSealed): PendingIntent? {
        return IntentHelp.servicePendingIntent<ReadAloudServiceImpl>(this, aloudSealed.toString())
    }

    open fun play() {
        pause = false
        upNotification()
    }


    abstract fun upSpeechRate(reset: Boolean = false)

    abstract fun prevP()

    abstract fun nextP()
}