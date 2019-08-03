package com.gyb.live.mitang.repository.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.yuhang.novel.pirate.repository.database.entity.UserEntity

@Dao
interface UserDao {


    @Query("Select * from UserEntity")
    fun getAll() : LiveData<List<UserEntity>>


    /**
     * 保存用户信息
     */
    @Insert
    fun saveUser(userEntity: UserEntity):Long

    /**
     * 获取当前用户信息
     */
    @Query("Select * from UserEntity order by UserEntity.id desc limit 1")
    fun getUserEntity(): UserEntity?

    /**
     * 获取指定id用户
     */
    @Query("Select * from UserEntity where uid = :id")
    fun getUserEntity(id:String): UserEntity?

    @Delete
    fun deleteUserEntity(userEntity: UserEntity)

}