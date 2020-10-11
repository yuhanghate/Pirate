package com.yuhang.novel.pirate.ui.user.viewmodel

import android.annotation.SuppressLint
import android.widget.EditText
import com.tamsiree.rxkit.RxRegTool
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.repository.database.entity.UserEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.UserResult
import com.yuhang.novel.pirate.utils.BeanPropertiesUtil
import org.greenrobot.eventbus.EventBus
import java.util.*

class RegisterViewModel : BaseViewModel() {

    /**
     * 注册
     */
    suspend fun register(username: String, password: String, email: String): UserResult {
        return mDataRepository.register(username, password, email)
    }

    /**
     * 检测手机参数
     */
    fun checkParams(
        usernameEt: EditText,
        passwordEt: EditText,
        passwordAgainEt: EditText,
        emailEt: EditText,
    ): Boolean {
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
    suspend fun saveAccount(userResult: UserResult) {
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

        EventBus.getDefault().postSticky(userResult)
    }

    /**
     * 同步本地收藏数据到服务器
     */
    @SuppressLint("CheckResult")
    suspend fun synCollection() {
        //本地查找收藏列表
        val list =
            mDataRepository.queryBookInfoCollectionAll().filterNotNull().map { it }.toList()

        if (list.isEmpty()) return

        list.forEach list@{ bookInfoEntity ->
            //最新记录上传到服务器
            val lastOpenChapter =
                mDataRepository.queryLastOpenChapter(bookInfoEntity.bookid) ?: return@list
            mDataRepository.updateReadHistory(
                bookName = bookInfoEntity.bookName,
                bookid = bookInfoEntity.bookid,
                chapterid = lastOpenChapter.chapterId,
                chapterName = lastOpenChapter.chapterName,
                author = bookInfoEntity.author,
                cover = bookInfoEntity.cover,
                description = bookInfoEntity.description,
                resouceType = mDataRepository.queryCollection(bookInfoEntity.bookid)?.resouce!!,
                content = mDataRepository.queryBookContent(bookInfoEntity.bookid,
                    lastOpenChapter.chapterId)?.content!!
            )

            //上传服务器
            if (bookInfoEntity.bookName.isNotEmpty()) {
                mDataRepository.addCollection(
                    bookName = bookInfoEntity.bookName,
                    bookid = bookInfoEntity.bookid,
                    author = bookInfoEntity.author,
                    cover = bookInfoEntity.cover,
                    description = bookInfoEntity.description,
                    bookStatus = bookInfoEntity.bookStatus,
                    classifyName = bookInfoEntity.classifyName,
                    resouceType = mDataRepository.queryCollection(bookInfoEntity.bookid)?.resouce!!
                )
            }
        }

    }
}