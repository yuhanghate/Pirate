package com.yuhang.novel.pirate.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class UserEntity {
    /**
     * 用户id
     */
    @PrimaryKey(autoGenerate = false)
    var uid: String = ""

    /**
     * 昵称
     */
    var username: String = ""

    /**
     * 性别
     */
    var gender: String = ""

    /**
     * 邮箱
     */
    var email: String = ""

    /**
     * 手机号
     */
    var tel: String = ""

    /**
     * 令牌
     */
    var token :String = ""

    /**
     * 是否会员
     */
    var isVip : Boolean = false

    /**
     * 最后登陆时间
     */
    var lastTime:Date  = Date()
}