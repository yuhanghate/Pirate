package com.yuhang.novel.pirate.listener

import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity

/**
 * 小说下载
 */
interface OnBookDownloadListener {

    fun onBookDownloadListener(obj: BookDownloadEntity, position: Int, isDownload:Boolean)

}