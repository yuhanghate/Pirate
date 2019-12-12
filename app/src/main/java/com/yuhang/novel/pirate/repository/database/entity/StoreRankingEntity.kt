package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult

@Entity
class StoreRankingEntity {

    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    /**
     * 最热
     */
    var hot: List<BooksKSResult> = arrayListOf()

    /**
     * 完结
     */
    var over: List<BooksKSResult> = arrayListOf()

    /**
     * 推荐
     */
    var commend: List<BooksKSResult> = arrayListOf()

    /**
     * 收藏
     */
    var collect: List<BooksKSResult> = arrayListOf()

    /**
     * 最新
     */
    @SerializedName("new")
    var newX: List<BooksKSResult> = arrayListOf()

    /**
     * 评分
     */
    var vote: List<BooksKSResult> = arrayListOf()

    /**
     * 男生/女生
     * man:男生 lady:女生
     */
    var genderType :String = "man"
}