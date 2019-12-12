package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult

@Entity
class StoreEntity {

    @PrimaryKey(autoGenerate = true)
    var id : Int = 0

    var Category:String = ""

    var Books : List<BooksKSResult> = arrayListOf()

    /**
     * 男生/女生  类型
     */
    var genderType:String = "man"
}