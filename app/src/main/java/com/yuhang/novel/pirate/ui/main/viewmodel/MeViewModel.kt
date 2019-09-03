package com.yuhang.novel.pirate.ui.main.viewmodel

import com.vondear.rxtool.RxDeviceTool
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.UserEntity
import com.yuhang.novel.pirate.repository.network.data.pirate.result.VersionResult
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MeViewModel : BaseViewModel() {

    /**
     * 检测版本
     */
    fun checkVersion(): Flowable<VersionResult> {
        return mDataRepository.checkVersion(RxDeviceTool.getAppVersionName(mFragment?.context))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(): Flowable<UserEntity?> {
        return Flowable.just("")
            .map { mDataRepository.getLastUser() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getMessage(result: VersionResult): String {
        return StringBuilder()
            .append("\n")
            .append("建议在WLAN环境下进行升级")
            .append("\n\n")
            .append("版本: ${result.newVersion}")
            .append("\n\n")
            .append("大小: ${result.targetSize}")
            .append("\n\n")
            .append("更新说明:")
            .append("\n")
            .append(result.updateLog)
            .append("\n")
            .toString()
    }
}