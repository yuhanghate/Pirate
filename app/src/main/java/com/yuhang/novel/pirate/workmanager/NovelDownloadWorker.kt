package com.yuhang.novel.pirate.workmanager

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.eventbus.DownloadEvent
import com.yuhang.novel.pirate.eventbus.WorkInfoEvent
import com.yuhang.novel.pirate.repository.network.convert.ConvertRepository
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 后台下载整本小说
 */
class NovelDownloadWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {


    var isCancel = false

    companion object {

        const val BOOKS_RESULT = "books_result"
    }


    override fun doWork(): Result {



        EventBus.getDefault().register(this)

        val obj =
            Gson().fromJson<BooksResult>(inputData.getString(BOOKS_RESULT), BooksResult::class.java)
                ?: return Result.failure()


        GlobalScope.launch(start = CoroutineStart.UNDISPATCHED) {
            coroutineScope{
                val dataRepository = PirateApp.getInstance().getDataRepository()
                val convertRepository = ConvertRepository()
                val info = dataRepository.queryBookInfo(obj.getBookid())
                val chapterList = dataRepository.queryChapterObjList(obj.getBookid())
                var isStart = false
                chapterList.forEachIndexed { index, chapterKSEntity ->

                    //全本缓存或者指定章节开始
                    if (obj.lastChapterId.isEmpty() || obj.lastChapterId == chapterKSEntity.chapterId) {
                        isStart = true
                    }

                    if (!isStart) {
                        return@forEachIndexed
                    }

                    //如果点击取消就结束任务
                    if (isCancel) {
                        dataRepository.deleteDownload(obj.getBookid())
                        return@coroutineScope
                    }
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

                        //如果点击取消就结束任务
                        if (isCancel) {
                            dataRepository.deleteDownload(obj.getBookid())
                            return@coroutineScope
                        }
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
            }
        }


        EventBus.getDefault().unregister(this)
        return Result.success()

    }

    @Subscribe
    fun onEvent(info: WorkInfoEvent) {
        if (info.uuid == id.toString()) {
            isCancel = true
        }
    }


}