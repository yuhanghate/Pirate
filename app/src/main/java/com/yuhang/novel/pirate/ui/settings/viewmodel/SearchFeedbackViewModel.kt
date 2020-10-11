package com.yuhang.novel.pirate.ui.settings.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.databinding.ActivitySearchFeedbackBinding
import com.yuhang.novel.pirate.extension.niceTipTop

class SearchFeedbackViewModel:BaseViewModel() {

    /**
     * 精确求书
     */
    suspend fun requestBook(bookname: String, author: String) :String{
        return mDataRepository.getBookFeedback(bookname, author)
    }

    fun checkParams(binding: ActivitySearchFeedbackBinding):Boolean {
        val book = binding.bookNameEt.text.toString()
        val author = binding.authorEt.text.toString()
        if (book.isEmpty()) {
            mActivity?.niceTipTop(binding.bookNameEt, "小说名称不能为空")
            return false
        }
        if (author.isEmpty()) {
            mActivity?.niceTipTop(binding.authorEt, "作者名称不能为空")
            return false
        }
        return true
    }
}