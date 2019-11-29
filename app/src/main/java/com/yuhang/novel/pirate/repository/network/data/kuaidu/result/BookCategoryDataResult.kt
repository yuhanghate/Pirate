package com.yuhang.novel.pirate.repository.network.data.kuaidu.result

data class BookCategoryDataResult(
    var bookCover: List<String> = listOf(),
    var count: Int = 0,
    var gender: String = "",
    var majorCate: String = "",
    var order: Int = 0
)