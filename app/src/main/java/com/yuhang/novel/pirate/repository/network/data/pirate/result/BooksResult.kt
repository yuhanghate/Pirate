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
     * 最后章节id
     */
    var lastChapterId:String = ""

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
     * 最后更新时间
     */
    var lastTime :Long = 0

    /**
     * 小黄书id
     */
    var bookSexId :String = ""

    /**
     * 获取书本id
     */
    fun getBookid(): String {
        if (resouce == "SEX") {//小黄书
            return bookSexId
        }

        if (resouce == "KS" && typeKs == 1) {//看书
            return bookKsId
        }

        if (resouce == "KD" && typeKd == 1) {//快读
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
     * 是否小黄书
     */
    fun isSex(): Boolean {
        if (resouce == "SEX") {
            return true
        }
        return false
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
        return false
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