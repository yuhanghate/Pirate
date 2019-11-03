package com.yuhang.novel.pirate.repository.network.data.pirate.result

import com.google.gson.Gson

class BooksResult {

    /**
     * 书名
     */
    var bookName: String = ""

    /**
     * 作者
     */
    var author: String = ""

    /**
     * 封面
     */
    var cover: String = ""

    /**
     * 简介
     */
    var description: String = ""

    /**
     * 最后章节
     */
    var lastChapterName: String = ""

    /**
     * 看书id
     */
    var bookKsId: String = ""

    /**
     * 书本分类
     */
    var kind: String = ""

    /**
     * 有看书源
     */
    var typeKs = 2

    /**
     * 有快读源
     */
    var typeKd = 2

    /**
     * 快读id
     */
    var bookKdId: String = ""

    /**
     * 默认使用看书源
     */
    var resouce = "KS"

    /**
     * 获取书本id
     */
    fun getBookid(): String {
        if (resouce == "KS" && typeKs == 1) {
            return bookKsId
        }
        if (resouce == "KD" && typeKd == 1) {
            return bookKdId
        }
        if (typeKs == 1) {
            return bookKsId
        }
        if (typeKd == 2) {
            return bookKdId
        }
        return ""
    }

    /**
     * 是否快读
     */
    fun isKuaiDu(): Boolean {
        //如果没有快读id就返回false
        if (typeKd != 1) {
            return false
        }

        if (resouce == "KD" && typeKd == 1) {
            return true
        }
        return true
    }

    /**
     * 是否看书
     */
    fun isKanShu(): Boolean {
        //如果id不存在就返回false
        if (typeKs != 1) {
            return false
        }
        if (resouce == "KS" && typeKs == 1) {
            return true
        }
        return true
    }

    /**
     * 获取源(看书/快读)
     */
    fun getType(): String {
        return resouce
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}