package com.yuhang.novel.pirate.service.impl

import android.annotation.SuppressLint
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.extension.niceBookResouceTypeKDEntity
import com.yuhang.novel.pirate.extension.niceBooksResult
import com.yuhang.novel.pirate.repository.database.entity.UserEntity
import com.yuhang.novel.pirate.repository.network.convert.ConvertRepository
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.UserResult
import com.yuhang.novel.pirate.service.UsersService
import com.yuhang.novel.pirate.utils.BeanPropertiesUtil
import java.lang.NullPointerException
import java.util.*

class UsersServiceImpl : UsersService {

    override suspend fun updateContentToLocal(bookid: String): String {
        return bookid
    }

    val mDataRepository by lazy { PirateApp.getInstance().getDataRepository() }

    val mConvertRepository by lazy { ConvertRepository() }

    override suspend fun updateCollectionToLocal(): List<BooksResult> {

        val result = mDataRepository.getCollectionList(1)
        if (result.code != 200) {
            throw NullPointerException("获取收藏列表失败")
        }


        //请空账号
        mDataRepository.clearBookInfo()

        return result.data.list.filter { it.resouceType != "KD" }
            .map {
                mDataRepository.insertCollection(it.niceBooksResult())
                it.niceBooksResult()
            }.toList()

    }


    override suspend fun updateChapterListToLocal(obj: BooksResult): BooksResult {
        val list = mConvertRepository.getChapterList(obj)
        mDataRepository.insertChapterList(list)
        return obj
    }

    @SuppressLint("CheckResult")
    override suspend fun updateBookInfoToLocal(obj: BooksResult): BooksResult {

        val info = mConvertRepository.getDetailsInfo(obj)
        mDataRepository.insertBookInfo(info)
        return obj
    }

    override suspend fun updateUsersToLocal(userResult: UserResult) {
        val userDataResult = userResult.data
        val user = mDataRepository.queryUser(userDataResult.username)

        //登录帐号清除之前所有记录
        mDataRepository.clearUsers()
        if (user == null) {
            //插入帐号
            val userEntity = UserEntity()
            BeanPropertiesUtil.copyProperties(userDataResult, userEntity)
            userEntity.uid = userDataResult.id
            userEntity.lastTime = Date()
            userEntity.isVip = userDataResult.vip


            PirateApp.getInstance().setToken(userEntity.token)
            mDataRepository.insert(userEntity)
        } else {
            //更新帐号
            user.token = userDataResult.token
            user.lastTime = Date()
        }

    }

    override suspend fun updateReadHistoryToLocal(obj: BooksResult): BooksResult {
        val readHistoryBookResult = mDataRepository.getReadHistoryCollectionsList(obj.getBookid())
        val result = readHistoryBookResult.data ?: return BooksResult()
        result.niceBookResouceTypeKDEntity()?.let { mDataRepository.insertKuaiDuResouce(it) }
        mDataRepository.updateLocalREadHistory(
            result.bookid,
            result.chapterid,
            result.createTime
        )
        return obj
    }
}