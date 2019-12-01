package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * 快读分类
 */
@Entity
//TODO
class CategoryKDEntity {

    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
    /**
     * 封面 3张
     * 保存成json
     */

    var bookCover: String = ""

    /**
     * 书总数
     */
    var count: Int = 0

    /**
     * 分类类型
     * 男生/女生/出版
     */
    var gender: String = ""

    /**
     * 小说类型
     * 玄幻/言情
     */
    var majorCate: String = ""


    var order: Int = 0

    /**
     * 转化成List
     */
    fun toCover(): List<String> {
        return Gson().fromJson<List<String>>(
            this.bookCover,
            object : TypeToken<List<String?>?>() {}.type
        )
    }
}