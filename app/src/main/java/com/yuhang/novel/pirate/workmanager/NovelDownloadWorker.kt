package com.yuhang.novel.pirate.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.eventbus.DownloadEvent
import com.yuhang.novel.pirate.repository.network.convert.ConvertRepository
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import org.greenrobot.eventbus.EventBus

/**
 * 后台下载整本小说
 */
class NovelDownloadWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    companion object {

        /**
         *
         */
        const val BOOKS_RESULT = "books_result"
    }

    override fun doWork(): Result {

        val obj =
            Gson().fromJson<BooksResult>(inputData.getString(BOOKS_RESULT), BooksResult::class.java)
                ?: return Result.failure()


        val dataRepository = PirateApp.getInstance().getDataRepository()
        val convertRepository = ConvertRepository()
        val info = dataRepository.queryBookInfo(obj.getBookid())
        val chapterList = dataRepository.queryChapterObjList(obj.getBookid())
        chapterList.forEachIndexed { index, chapterKSEntity ->

            //返回本地数据
            val contentKSEntity =
                dataRepository.queryBookContent(obj.getBookid(), chapterKSEntity.chapterId)

            try {
                if (contentKSEntity == null) {
                    //返回服务器数据
                    val entity = convertRepository.downloadChapterContent(obj, chapterKSEntity)
                    dataRepository.insertBookContent(entity.apply { lastOpenTime = 0 })
                }

                //更新进度到数据库
                dataRepository.updateDownloadBook(
                    obj.getBookid(),
                    obj.bookName,
                    obj.resouce,
                    index + 1,
                    chapterList.size,
                    info?.cover ?: "",
                    author = info?.author ?: "",
                    uuid = id.toString()
                )
                EventBus.getDefault().post(DownloadEvent().apply {
                    this.bookName = info?.bookName ?: ""
                    this.progress = index + 1
                    this.total = chapterList.size
                    this.cover = info?.cover ?: ""
                    this.bookId = info?.bookid ?: ""
                    this.author = info?.author ?: ""
                })

            } catch (e: Exception) {
            }
        }

        return Result.success()

    }
}