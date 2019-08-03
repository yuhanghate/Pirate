package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        @ColumnInfo(name = "access_token")
        var accessToken: String = "",

        @ColumnInfo(name = "refresh_token")
        var refreshToken: String = "",

        @ColumnInfo(name = "nickname")
        var nickname: String = "",

        var uid: String = "",

        var gender: String = "",
        /**
         * 云信id
         */
        @ColumnInfo(name = "imId")
        var imId: String = "",

        /**
         * 云信token
         */
        @ColumnInfo(name = "imToken")
        var imToken: String = ""
)