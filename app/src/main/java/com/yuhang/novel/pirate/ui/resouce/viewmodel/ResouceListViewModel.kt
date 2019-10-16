package com.yuhang.novel.pirate.ui.resouce.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BookResouceListResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BookResouceResult
import com.yuhang.novel.pirate.ui.resouce.adapter.ResouceAdapter
import io.reactivex.Flowable
import kotlin.concurrent.thread

class ResouceListViewModel : BaseViewModel() {

    val list = arrayListOf<BookResouceListResult>()

    val adapter by lazy { ResouceAdapter() }

    /**
     * 书源列表
     */
    fun getResouceList(pageNum: Int): Flowable<BookResouceResult> {
        return mDataRepository.getResouceList(pageNum)
            .compose(io_main())
    }

    /**
     * 插入书源列表
     */
    fun insertResouceList(list: List<BookResouceListResult>) {
        list.forEach {
            if (it.isCheck == 1 && !this.list.contains(it)) {
                this.list.add(it)
            }
        }
        thread {
            mDataRepository.insertResouceList(list)

        }
    }

    /**
     * 查询默认选中的源id
     */
    private fun queryResouceList(): Flowable<List<String>> {
        return Flowable.just("")
            .map { mDataRepository.queryResouceCheckList() }
            .compose(io_main())
    }

    /**
     * 更新源选中状态
     */
    fun updateResouceCheckStatuts(list: List<BookResouceListResult>) {
        thread {
            mDataRepository.updateResouceList(list)
        }
    }

    /**
     * 获取默认的选中的源
     */
    fun getDefaultCheckResouce(list: List<BookResouceListResult>): Flowable<List<BookResouceListResult>> {
        return queryResouceList()
            .map { resouceids ->

                list.forEach {
                    if (resouceids.contains(it.id)) {
                        it.isCheck = 1
                    } else {
                        it.isCheck = 0
                    }
                }
                return@map list
            }
    }
}