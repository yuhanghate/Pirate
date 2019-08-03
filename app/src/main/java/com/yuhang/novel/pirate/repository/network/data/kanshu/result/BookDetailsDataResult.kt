package com.yuhang.novel.pirate.repository.network.data.kanshu.result

data class BookDetailsDataResult(val SameUserBooks: List<RecommendBooksResult?>,
                                 val Img: String = "",
                                 val BookVote: BookVote,
                                 val FirstChapterId: Int = 0,
                                 val Name: String = "",
                                 val BookStatus: String = "",
                                 val Desc: String = "",
                                 val LastChapter: String = "",
                                 val Author: String = "",
                                 val Id: Int = 0,
                                 val CName: String = "",
                                 val LastChapterId: Int = 0,
                                 val SameCategoryBooks: List<RecommendBooksResult?>,
                                 val LastTime: String = "",
                                 val CId: Int = 0)