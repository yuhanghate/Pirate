package com.yuhang.novel.pirate.repository.network.data.pirate.result

data class ChapterSexDataResult(
    var author: String = "",
    var bookId: Int = 0,
    var bookName: String = "",
    var bookUrl: String = "",
    var description: String = "",
    var lastChapterName: String = ""
)