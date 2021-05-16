package com.yuhang.novel.pirate.repository.network.data.kanshu.result

data class BookDetailsDataResult(val SameUserBooks: List<BooksKSResult>?,
                                 val Img: String = "",
                                 val BookVote: BookVote,
                                 val FirstChapterId: String = "",
                                 val Name: String = "",
                                 val BookStatus: String = "",
                                 val Desc: String = "",
                                 val LastChapter: String = "",
                                 val Author: String = "",
                                 val Id: String = "",
                                 val CName: String = "",
                                 val LastChapterId: String = "",
                                 val SameCategoryBooks: List<BooksKSResult?>,
                                 var LastTime: String = "",
                                 val CId: Int = 0)