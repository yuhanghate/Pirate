package com.yuhang.novel.pirate.ui.user.viewmodel

import android.annotation.SuppressLint
import android.widget.EditText
import com.vondear.rxtool.RxRegTool
import com.yuhang.novel.pirate.app.PirateApp
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
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

class LoginViewModel : BaseViewModel() {

    /**
     * 登录
     */
    fun login(username: String, password: String): Flowable<UserResult> {
        return mDataRepository.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                    content = ""
                ).compose(io_main()).map { bookINfoEntity }
            }
            .flatMap {
                //收藏列表上传服务器
                if (it.bookName.isNotEmpty()) {
                    return@flatMap mDataRepository.addCollection(
                        bookName = it.bookName, bookid = it.bookid,
                        author = it.author, cover = it.cover, description = it.description,
                        bookStatus = it.bookStatus, classifyName = it.classifyName,
                        resouceType = mDataRepository.queryCollection(it.bookid)?.resouce!!
                    ).compose(io_main())
                } else {
                    return@flatMap Flowable.just(StatusResult(code = -1, msg = "本地收藏数据为空"))
                }

            }.compose(io_main())
    }

}

