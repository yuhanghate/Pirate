package com.yuhang.novel.pirate.workmanager

import android.content.Context
import android.text.TextUtils
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.extension.niceBookContentKSEntity
import org.greenrobot.eventbus.EventBus

/**
 * 后台下载整本小说
 */
class NovelDownloadWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    companion object{

        /**
         * 章节ID
         */
        const val CHANPTER_ID = "chanpterid"

        /**
         * 小说ID
         */
        const val BOOKID = "bookid"
    }
    override fun doWork(): Result {

        val list = inputData.getStringArray(CHANPTER_ID)?:return Result.failure()
        val bookid = inputData.getString(BOOKID)
        if (TextUtils.isEmpty(bookid)) return Result.failure()
        val dataRepository = PirateApp.getInstance().getDataRepository()

        list.forEachIndexed { index, chanpterid ->
            val downloadNovel = dataRepository.downloadNovel(bookid!!, chanpterid)
            val execute = downloadNovel.execute()
            if (execute.isSuccessful) {
                execute.body()?.data?.let {contentDataResult ->
                    dataRepository.insertBookContent(contentDataResult.niceBookContentKSEntity())
                }
            }
            val data = Data.Builder().putString("progress", "${index}/${list.size}")
                .putString("index", "$index")
                .putString("size", "${list.size}")
                .build()

            EventBus.getDefault().post(data)
        }

        return Result.success()

    }
}