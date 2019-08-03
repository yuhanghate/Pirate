package com.yuhang.novel.pirate.ui.search.result

import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import kotlinx.android.parcel.Parcelize

/**
 * 搜索结果页
 */
@Parcelize
class SearchResult : SearchSuggestion, Parcelable {

    var keyword = ""
    override fun getBody(): String {
        return keyword
    }


}