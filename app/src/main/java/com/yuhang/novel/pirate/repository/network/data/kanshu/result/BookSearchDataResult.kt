package com.yuhang.novel.pirate.repository.network.data.kanshu.result

import com.yuhang.novel.pirate.constant.BookConstant

/**
 * Id : 424
 * Name : 凡人修仙传
 * Author : 忘语
 * Img : https://imgapi.jiaston.com/BookFiles/BookImages/424.jpg
 * Desc : 凡人修仙传最新章节列：小说《凡人修仙传》忘语/著,凡人修仙传全文阅读一个普通山村小子，偶然下进入到当地江湖小门派，成了一名记名弟子。他以这样身份，如何在门派中立足,如何以平庸的资质进入到修仙者的行列，从而......
 * BookStatus : 完结
 * LastChapterId : 4807039
 * LastChapter : 忘语新书《玄界之门》
 * CName : 武侠仙侠
 * UpdateTime : 2017-03-18 00:00:00
 */
data class BookSearchDataResult(val BookStatus: String = "",
                                val Desc: String = "",
                                val Img: String = "",
                                val LastChapter: String = "",
                                val UpdateTime: String = "",
                                val Author: String = "",
                                val Id: String = "",
                                val LastChapterId: String = "",
                                val CName: String = "",
                                val resouceType:String = BookConstant.RESOUCE_TYPE_KS,//书源类型
                                val Name: String = "")