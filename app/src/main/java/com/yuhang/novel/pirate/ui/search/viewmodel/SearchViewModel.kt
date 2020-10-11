package com.yuhang.novel.pirate.ui.search.viewmodel

import androidx.lifecycle.viewModelScope
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceSearchResult
import com.yuhang.novel.pirate.repository.database.entity.SearchHistoryKSEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.SearchSuggestResult
import com.yuhang.novel.pirate.ui.search.adapter.ItemSearchSuggestAdapter
import com.yuhang.novel.pirate.ui.search.adapter.SearchAdapter
import com.yuhang.novel.pirate.ui.search.result.SearchResult
import kotlinx.coroutines.launch

class SearchViewModel : BaseViewModel() {

    /**
     * 最后一次搜索记录
     */
    var lastKeyword = ""

    /**
     * 搜索源
     * KD:快读
     * KS:看书
     */
    var resouce = "KD"

    val adapter by lazy { SearchAdapter() }

    /**
     * 联想搜索
     */
    val searchAdapter: ItemSearchSuggestAdapter by lazy { ItemSearchSuggestAdapter() }

    /**
     * 点击联想功能以后关键字
     */
    var searchSuggestStr = ""

    suspend fun searchBookV2(keyword: String): List<BooksResult> {
        return mConvertRepository.getSearchResult(resouce, keyword)
    }

    /**
     * 搜索历史记录
     */
    suspend fun getSearchHistory(): List<SearchResult?> {
        return mDataRepository.querySearchHistoryList()
            .map { obj -> obj?.niceSearchResult() }
            .toList()
    }

    /**
     * 插入搜索历史
     */
    fun insertSearchHistory(keyword: String) {
        if (keyword.trim().isEmpty()) return

        viewModelScope.launch {
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
     * 模糊匹配
     */
    suspend fun searchSuggest(keyword: String): List<SearchSuggestResult> {
        return mConvertRepository.searchSuggest(keyword)
    }

}