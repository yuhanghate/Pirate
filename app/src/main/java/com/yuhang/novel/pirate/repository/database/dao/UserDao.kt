package com.yuhang.novel.pirate.repository.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.UserEntity
import java.util.*

@Dao
interface UserDao {

    /**
     * 插入帐号
     */
    @Insert
    fun insert(userEntity: UserEntity)

    /**
     * 获取最后登陆时间
     */
    @Query("select * from userentity order by lastTime desc limit 1")
    fun queryUser(): UserEntity?

    /**
     * 修改最后登陆时间
     */
    @Query("update userentity set lastTime = :lastTime  where uid = :uid")
    fun updateLastTime(uid: String, lastTime: Date)

    /**
     * 删除用户名
     */
    @Delete
    fun delete(userEntity: UserEntity)

    /**
     * 根据uid查询
     */
    @Query("select * from userentity where username = :username order by username limit 1")
    fun queryUser(username:String):UserEntity?
}