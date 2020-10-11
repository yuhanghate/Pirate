package com.yuhang.novel.pirate.ui.user.viewmodel

import android.annotation.SuppressLint
import android.widget.EditText
import com.tamsiree.rxkit.RxRegTool
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceTipTop
import com.yuhang.novel.pirate.repository.network.data.pirate.result.StatusResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.UserResult

class LoginViewModel : BaseViewModel() {

    /**
     * 登录
     */
    suspend fun login(username: String, password: String): UserResult {
        return mDataRepository.login(username, password)
    }

    /**
     * 检测手机参数
     */
    fun checkParams(usernameEt: EditText, passwordEt: EditText): Boolean {
        val username = usernameEt.text.toString()
        val password = passwordEt.text.toString()
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
        return true
    }


    /**
     * 同步本地收藏数据到服务器
     */
    @SuppressLint("CheckResult")
    suspend fun synCollection(): StatusResult {

        //本地查找收藏列表
        val list = mDataRepository.queryBookInfoCollectionAll().filterNotNull().map { it }.toList()
        if (list.isEmpty()) {
            return StatusResult(code = -1, msg = "本地收藏数据为空")
        }

        list.forEach { bookInfoEntity ->
            //最新记录上传到服务器
            val lastOpenChapter = mDataRepository.queryLastOpenChapter(bookInfoEntity.bookid)
                ?: return StatusResult(code = -1, msg = "本地收藏数据为空")
            val status = mDataRepository.updateReadHistory(
                bookName = bookInfoEntity.bookName,
                bookid = bookInfoEntity.bookid,
                chapterid = lastOpenChapter.chapterId,
                chapterName = lastOpenChapter.chapterName,
                author = bookInfoEntity.author,
                cover = bookInfoEntity.cover,
                description = bookInfoEntity.description,
                resouceType = mDataRepository.queryCollection(bookInfoEntity.bookid)?.resouce!!,
                content = ""
            )

            if (bookInfoEntity.bookName.isEmpty()) {
                return StatusResult(code = -1, msg = "本地收藏数据为空")
            }

            //收藏列表上传服务器
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

        return StatusResult(200,"成功")

    }

}

