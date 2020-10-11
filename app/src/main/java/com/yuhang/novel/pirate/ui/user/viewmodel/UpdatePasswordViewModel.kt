package com.yuhang.novel.pirate.ui.user.viewmodel

import android.annotation.SuppressLint
import android.widget.EditText
import com.tamsiree.rxkit.RxRegTool
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.repository.network.data.pirate.result.UserResult

class UpdatePasswordViewModel : BaseViewModel() {

    var email: String = ""

    /**
     * 检测手机参数
     */
    fun checkParams(usernameEt: EditText, passwordEt: EditText, againPasswordEt: EditText): Boolean {
        val username = usernameEt.text.toString()
        val password = passwordEt.text.toString()
        val againPassword = againPasswordEt.text.toString()
        if (username.isEmpty()) {
            mActivity?.niceTipTop(usernameEt, "手机号不能为空")
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

        if (againPassword.isEmpty()) {
            mActivity?.niceTipTop(againPasswordEt, "第二次密码不能为空")
            return false
        }

        if (password != againPassword) {
            mActivity?.niceTipTop(againPasswordEt, "两次密码不一一致")
            return false
        }
        return true
    }


    /**
     * 修改密码
     */
    suspend fun updatePassword(usernameEt: EditText, passwordEt: EditText, againPasswordEt: EditText): UserResult {
        val username = usernameEt.text.toString()
        val password = passwordEt.text.toString()
        val againPassword = againPasswordEt.text.toString()
        return mDataRepository.updatePassword(email, username, password, againPassword)
    }


    /**
     * 同步本地收藏数据到服务器
     */
    @SuppressLint("CheckResult")
    suspend fun synCollection() {

        //本地查找收藏列表
        val list =
            mDataRepository.queryBookInfoCollectionAll().filterNotNull().map { it }.toList()

        if (list.isEmpty()) {
            return
        }

        list.forEach list@{bookInfoEntity->
            //最新记录上传到服务器
            val lastOpenChapter = mDataRepository.queryLastOpenChapter(bookInfoEntity.bookid)?: return@list
            mDataRepository.updateReadHistory(
                bookName = bookInfoEntity.bookName,
                bookid = bookInfoEntity.bookid,
                chapterid = lastOpenChapter.chapterId,
                chapterName = lastOpenChapter.chapterName,
                author = bookInfoEntity.author,
                cover = bookInfoEntity.cover,
                description = bookInfoEntity.description,
                resouceType = mDataRepository.queryCollection(bookInfoEntity.bookid)?.resouce!!,
                content = mDataRepository.queryBookContent(bookInfoEntity.bookid, lastOpenChapter.chapterId)?.content!!
            )

            //收藏列表上传服务器
            if (bookInfoEntity.bookName.isNotEmpty()) {
                 mDataRepository.addCollection(
                    bookName = bookInfoEntity.bookName, bookid = bookInfoEntity.bookid,
                    author = bookInfoEntity.author, cover = bookInfoEntity.cover, description = bookInfoEntity.description,
                    bookStatus = bookInfoEntity.bookStatus, classifyName = bookInfoEntity.classifyName,
                    resouceType = mDataRepository.queryCollection(bookInfoEntity.bookid)?.resouce!!
                )
            }
        }

    }
}