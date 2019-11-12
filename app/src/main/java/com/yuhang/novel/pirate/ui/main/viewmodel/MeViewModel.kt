package com.yuhang.novel.pirate.ui.main.viewmodel

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
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


    /**
     * 是否有安装应用权限
     * true:有
     * false:没有
     */
    fun installProcess():Boolean {
        var haveInstallPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            haveInstallPermission = mFragment?.activity?.packageManager?.canRequestPackageInstalls()!!
            if (!haveInstallPermission) {//没有权限
                startInstallPermissionSettingActivity()
                return haveInstallPermission
            }
            return haveInstallPermission
        }
        return haveInstallPermission
    }

    /**
     * 打开允许安装界面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,  Uri.parse("package:" + mFragment?.activity?.packageName))
        mFragment?.activity?.startActivityForResult(intent, 10086)
    }


}