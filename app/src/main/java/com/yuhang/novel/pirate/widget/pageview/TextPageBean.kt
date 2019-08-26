package com.yuhang.novel.pirate.widget.pageview

import com.yuhang.novel.pirate.utils.PageUtils

/**
 * 每页的Bean对象
 */
 class TextPageBean{
        /**
         * 一章节总页数
         */
        var maxPage:Int = 0

        /**
         * 当前页数
         */
        var currentPage:Int = 0

        /**
         * 章节名称
         */
        var chapterName:String = ""

        /**
         * 每页所有行数据
         */
        var lines:List<PageUtils.LinesBean> = arrayListOf()
}