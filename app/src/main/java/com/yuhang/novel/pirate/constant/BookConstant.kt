package com.yuhang.novel.pirate.constant

import android.graphics.Color

object BookConstant {

    /**
     * 文字转换
     * 简体 > 繁体
     *
     */
    const val TEXT_CONVERT_TYPE = "text_convert_type"

    /**
     * 章节内容文字大小
     */
    const val TEXT_PAGE_SIZE = 18f

    /**
     * 章节内容背影颜色
     */
    var TEXT_PAGE_BACKGROUND = Color.parseColor("#F6EFDD")

    /**
     * 是否第一次安装app
     */
    const val IS_FIRST_INSTALL = "is_first_install"

    /**
     * 下一页加载触发数
     */
    const val LOAD_MORE_COUNT = 3

    /**
     * 上一页加载触发数
     */
    const val LOAD_REFRESH_COUNT = 3

}