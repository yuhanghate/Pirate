package com.yuhang.novel.pirate.ui.user.viewmodel

import android.annotation.SuppressLint
import android.widget.EditText
import com.vondear.rxtool.RxRegTool
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity
import com.yuhang.novel.pirate.repository.database.entity.UserEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.StatusResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.UserResult
import com.yuhang.novel.pirate.utils.BeanPropertiesUtil
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.concurrent.thread

class RegisterViewModel : BaseViewModel() {

    /**
     * 注册
     */
    fun register(username: String, password: String, email: String): Flowable<UserResult> {
        return mDataRepository.register(username, password, email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 检测手机参数
     */
    fun checkParams(usernameEt: EditText, passwordEt: EditText, passwordAgainEt: EditText, emailEt: EditText): Boolean {
        val username = usernameEt.text.toString()
        val password = passwordEt.text.toString()
        val passwordAgain = passwordAgainEt.text.toString()
        val email = emailEt.text.toString()
        if (username.isEmpty()) {
            mActivity?.niceTipTop(usernameEt, "用户名不能为空")
            return false
        }
        if (!RxRegTool.isMobileSimple(username)) {
            mActivity?.niceTipTop(usernameEt, "手机号不正确")
            return false
        }
        if (password.isEmpty()) {
            mActivity?.niceTipTop(passwordEt, "密码不能为空")
            return false
        }

        if (password.length < 6) {
            mActivity?.niceTipTop(passwordEt, "密码必须6位及以上")
            return false
        }
        if (passwordAgain.isEmpty()) {
            mActivity?.niceTipTop(passwordAgainEt, "密码不能为空")
            return false
        }

        if (passwordAgain.length < 6) {
            mActivity?.niceTipTop(passwordEt, "密码必须6位及以上")
            return false
        }

        if (password != passwordAgain) {
            mActivity?.niceTipTop(passwordAgainEt, "两次密码输入不一致")
            return false
        }
        if (email.isEmpty()) {
            mActivity?.niceTipTop(emailEt, "邮箱不能为空")
            return false
        }

        return true
    }

    /**
     * 保存帐号信息
     */
    fun saveAccount(userResult: UserResult) {
        thread {

            val userDataResult = userResult.data
            val user = mDataRepository.queryUser(userDataResult.username)

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

            mActivity?.runOnUiThread { EventBus.getDefault().postSticky(userResult) }

        }
    }

    /**
     * 同步本地收藏数据到服务器
     */
    @SuppressLint("CheckResult")
    fun synCollection(): Flowable<StatusResult> {
        return Flowable.just("")
            .map {
                //本地查找收藏列表
                mDataRepository.queryBookInfoCollectionAll().filterNotNull().map { it }.toList()
            }
            .flatMap {
                if (it.isEmpty()) {
                    return@flatMap Flowable.just<BookInfoKSEntity>(BookInfoKSEntity())
                }
                Flowable.fromArray(* it.toTypedArray())
            }
            .flatMap {bookINfoEntity ->

                //最新记录上传到服务器
                val lastOpenChapter = mDataRepository.queryLastOpenChapter(bookINfoEntity.bookid)?: return@flatMap Flowable.just(bookINfoEntity)
                return@flatMap mDataRepository.updateReadHistory(
                    bookName = bookINfoEntity.bookName,
                    bookid = bookINfoEntity.bookid,
                    chapterid = lastOpenChapter.chapterId,
                    chapterName = lastOpenChapter.chapterName,
                    author = bookINfoEntity.author,
                    cover = bookINfoEntity.cover,
                    description = bookINfoEntity.description,
                    resouceType = mDataRepository.queryCollection(bookINfoEntity.bookid)?.resouce!!,
                    content = mDataRepository.queryBookContent(bookINfoEntity.bookid, lastOpenChapter.chapterId)?.content!!
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map { bookINfoEntity }
            }
            .flatMap {
                //上传服务器
                if (it.bookName.isNotEmpty()) {
                    return@flatMap mDataRepository.addCollection(
                        bookName = it.bookName, bookid = it.bookid,
                        author = it.author, cover = it.cover, description = it.description,
                        bookStatus = it.bookStatus, classifyName = it.classifyName,
                        resouceType = mDataRepository.queryCollection(it.bookid)?.resouce!!
                    ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                } else {
                    return@flatMap Flowable.just(StatusResult(code = -1, msg = "本地收藏数据为空"))
                }

            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}