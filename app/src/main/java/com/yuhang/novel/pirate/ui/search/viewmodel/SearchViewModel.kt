package com.yuhang.novel.pirate.ui.search.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceSearchResult
import com.yuhang.novel.pirate.repository.database.entity.SearchHistoryKSEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookSearchResult
import com.yuhang.novel.pirate.ui.search.adapter.SearchAdapter
import com.yuhang.novel.pirate.ui.search.result.SearchResult
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.concurrent.thread

class SearchViewModel : BaseViewModel() {

    /**
     * 最后一次搜索记录
     */
    var lastKeyword = ""

    /**
     * 每次输入的内容
     */
    var searchKeyword = ""

    /**
     * 源id
     */
    var resouceList = arrayListOf<String>()

    val adapter by lazy { SearchAdapter() }

    /**
     * 搜索小说
     */
    fun searchBook(keyword: String): Flowable<BookSearchResult> {
        return mDataRepository.searchBook(keyword).subscribeOn(Schedulers.io())
            .compose(mActivity!!.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 搜索历史记录
     */
    fun getSearchHistory(): Flowable<List<SearchResult?>> {
        return Flowable.just("")
            .map { mDataRepository.querySearchHistoryList().map { obj -> obj?.niceSearchResult() }.toList() }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 根据关键字模糊匹配
     */
    fun queryListHisotry(keyword: String):Flowable<List<SearchResult?>> {
        return Flowable.just(keyword)
                .map { mDataRepository.queryListHisotry(it).map { obj -> obj?.niceSearchResult() }.toList() }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 插入搜索历史
     */
    fun insertSearchHistory(keyword: String) {
        thread {
            val hisotry = mDataRepository.querySearchHisotry(keyword)
            if (hisotry != null) {
                mDataRepository.deleteSearchHistory(hisotry)
            }
            mDataRepository.insertSearchHistory(
                SearchHistoryKSEntity(
                    keyword = keyword,
                    updateTime = System.currentTimeMillis()
                )
            )
        }
    }

    /**
     * 清空搜索记录
     */
    fun clearSearchHistory() {
        thread { mDataRepository.clearSearchHistory() }

    }

    /**
     * 获取源标题
     */
    fun queryResouceList():Flowable<List<String>> {
        return Flowable.just("")
            .map { mDataRepository.queryResouceCheckTitleList() }
            .compose(io_main())
    }

    fun updateResouceList() {
        thread {

        }
    }
}