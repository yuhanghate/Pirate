package com.yuhang.novel.pirate.repository.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yuhang.novel.pirate.repository.database.entity.StoreRankingEntity

class BookConverters {

    @TypeConverter
    fun stringToObject(value: String): List<StoreRankingEntity> {
        val listType = object : TypeToken<List<StoreRankingEntity>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun objectToString(list: List<StoreRankingEntity>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

}