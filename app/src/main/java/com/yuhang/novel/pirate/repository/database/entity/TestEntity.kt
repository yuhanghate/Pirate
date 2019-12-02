package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TestEntity {

    @PrimaryKey(autoGenerate = true)
    var id : Int = 0

    var testName :String = ""

    var testCode : Int = 0
}