package com.yuhang.novel.pirate.service.impl

import android.annotation.SuppressLint
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.extension.niceBookChapterKSEntity
import com.yuhang.novel.pirate.extension.niceBookContentKSEntity
import com.yuhang.novel.pirate.extension.niceBookInfoKSEntity
import com.yuhang.novel.pirate.repository.database.entity.UserEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.UserResult
import com.yuhang.novel.pirate.service.UsersService
import com.yuhang.novel.pirate.utils.BeanPropertiesUtil
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.*

class UsersServiceImpl : UsersService {

    override fun updateContentToLocal(bookid: Int): Flowable<Int> {
        return Flowable.just(bookid)
            .map { mDataRepository.queryFirstChapterid(it) }
            .flatMap { mDataRepository.getChapterContent(bookid, it) }
            .map { mDataRepository.insertBookContent(it.data.niceBookContentKSEntity()) }
            .map { bookid }
    }

    val mDataRepository by lazy { PirateApp.getInstance().getDataRepository() }

    override fun updateCollectionToLocal(): Flowable<Int> {
        return mDataRepository.getCollectionList(1)
            .flatMap { Flowable.fromArray(*it.data.list.toTypedArray()) }
            .map {
                mDataRepository.insertCollection(it.bookid.toInt())
                it.bookid.toInt()
            }
    }


    override fun updateChapterListToLocal(bookid: Int): Flowable<Int> {
        return mDataRepository.getBookChapterList(bookid)
            .map {
                mDataRepository.insertChapterList(it.data.niceBookChapterKSEntity())
                bookid
            }
    }

    @SuppressLint("CheckResult")
    override fun updateBookInfoToLocal(bookid: Int): Flowable<Int> {

        return mDataRepository.getBookDetails(bookid)
            .map {
                mDataRepository.insertBookInfo(it.data.niceBookInfoKSEntity())
                bookid
            }

//        //获取收藏列表
//        mDataRepository.getCollectionList(1)
//            .flatMap { Flowable.fromArray(*it.data.list.toTypedArray()) }
//            .map {
//                //插入小说收藏到本地
//                mDataRepository.insertCollection(it.bookid.toInt())
//                it
//            }
//            //获取小说详情
//            .flatMap { mDataRepository.getBookDetails(it.bookid.toInt()) }
//            .map {
//                val infoKSEntity = it.data.niceBookInfoKSEntity()
//                //插入小说详情到本地
//                mDataRepository.insertBookInfo(infoKSEntity)
//                infoKSEntity
//            }
//            //获取章节列表
//            .flatMap { mDataRepository.getBookChapterList(it.bookid) }
//            //插入章节列表到本地
//            .map { mDataRepository.insertChapterList(it.data.niceBookChapterKSEntity()) }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({}, {})
    }

    override fun updateUsersToLocal(userResult: UserResult): Flowable<Unit> {
        return Flowable.just(userResult)
            .map {
                val userDataResult = userResult.data
                val user = mDataRepository.queryUser(userDataResult.username)

                //登陆帐号清除之前所有记录
                mDataRepository.clearUsers()
                if (user == null) {
                    //插入帐号
                    val userEntity = UserEntity()
                    BeanPropertiesUtil.copyProperties(userDataResult, userEntity)
                    userEntity.uid = userDataResult.id
                    userEntity.lastTime = Date()


                    PirateApp.getInstance().setToken(userEntity.token)
                    mDataRepository.insert(userEntity)
                } else {
                    //更新帐号
                    user.token = userDataResult.token
                    user.lastTime = Date()
                }
            }.subscribeOn(Schedulers.io())

    }
}