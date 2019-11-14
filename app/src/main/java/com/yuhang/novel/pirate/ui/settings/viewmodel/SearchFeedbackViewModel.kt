package com.yuhang.novel.pirate.ui.settings.viewmodel

import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.databinding.ActivitySearchFeedbackBinding
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.extension.niceTipTop
import io.reactivex.Flowable

class SearchFeedbackViewModel:BaseViewModel() {

    /**
     * 精确求书
     */
    fun requestBook(bookname: String, author: String) :Flowable<String>{
        return mDataRepository.getBookFeedback(bookname, author)
            .compose(io_main())
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