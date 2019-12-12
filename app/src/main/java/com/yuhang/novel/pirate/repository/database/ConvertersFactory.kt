package com.yuhang.novel.pirate.repository.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yuhang.novel.pirate.repository.database.entity.StoreRankingEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import java.util.*

/**
 * e-mail : 714610354@qq.com
 * time   : 2018/04/24
 * desc   : 转换
 * @author yuhang
 */
class ConvertersFactory {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun booksKSResultToString(list : List<BooksKSResult>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToBooksKSResult(value: String): List<BooksKSResult> {
        val listType = object : TypeToken<List<BooksKSResult>>() {}.type
        return Gson().fromJson(value, listType)
    }
}