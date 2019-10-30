package com.yuhang.novel.pirate.repository.network.data.pirate.result

data class AuthorBooksResult(
    var code: Int?,
    var data: List<BooksResult>?,
    var msg: String?
)