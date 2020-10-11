package com.yuhang.novel.pirate.ui.ad.viewmodel

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameDataResult
import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameRecommentResult
import com.yuhang.novel.pirate.service.impl.DownloadServiceImpl
import com.yuhang.novel.pirate.ui.ad.adapter.GameAdapter

class GameViewModel : BaseViewModel() {

    val adapter: GameAdapter by lazy { GameAdapter() }

    val objList by lazy { hashMapOf<String, Int>() }


    /**
     * 获取游戏推荐列表
     */
    suspend fun getGameRecommentList(pageNum:Int): GameRecommentResult {
        return mDataRepository.getGameRecommentList(pageNum)
    }

    /**
     * 创建下载任务
     */
    fun createTask(list : List<GameDataResult>):List<GameDataResult> {
        list.forEachIndexed { index, gameDataResult ->
            objList[gameDataResult.downloadUrl] = index
        }
        return list
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
            haveInstallPermission = mActivity?.packageManager?.canRequestPackageInstalls()!!
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
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,  Uri.parse("package:" + mActivity?.packageName))
        mActivity?.startActivityForResult(intent, 10086)
    }

    /**
     * 删除缓存
     */
    fun deleteDownload(obj:GameDataResult) {

        DownloadServiceImpl.deleteDownload(mActivity!!,obj.downloadUrl)
//        RxFileTool.deleteFile(DownloadServiceImpl.getDownloadPath(obj.downloadUrl))
        adapter.notifyDataSetChanged()
        mActivity?.niceToast("清除成功")
    }
}