package com.yuhang.novel.pirate.ui.main.viewmodel

import com.vondear.rxtool.RxDeviceTool
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.network.data.pirate.result.StatusResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.VersionResult
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MeViewModel:BaseViewModel() {

    /**
     * 检测版本
     */
    fun checkVersion(): Flowable<VersionResult> {
        return mDataRepository.checkVersion(RxDeviceTool.getAppVersionName(mFragment?.context))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun download() {

    }
}