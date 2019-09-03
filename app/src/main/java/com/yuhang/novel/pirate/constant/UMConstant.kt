package com.yuhang.novel.pirate.constant

/**
 * 友盟统计相关
 */
object UMConstant {

    /**
     * 统计小说:名称|章节|时间|背景|字体大小
     */
    const val TYPE_BOOK = "read_book"

    object TypeBook{

        /**
         * 小说名称
         */
        const val BOOK_NAME = "book_name"

        /**
         * 章节名称
         */
        const val BOOK_CHANPTER = "book_chanpter"

        /**
         * 阅读时间(格式化以后)
         */
        const val BOOK_READ_TIME = "book_read_time"

        /**
         * 阅读时间戳,用来基础删选
         */
        const val BOOK_READ_TIME_STAMP = "book_read_time_stamp"

        /**
         * 小说背景
         */
        const val BOOK_BACKGROUND = "book_page_background"

        /**
         * 阅读文字大小
         */
        const val BOOK_FONT = "book_font"
    }

}