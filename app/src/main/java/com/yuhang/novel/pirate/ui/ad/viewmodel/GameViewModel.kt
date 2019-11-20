package com.yuhang.novel.pirate.ui.ad.viewmodel

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import co.mobiwise.library.ProgressLayout
import com.liulishuo.okdownload.DownloadTask
import com.vondear.rxtool.RxFileTool
import com.vondear.rxtool.RxTool
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.network.data.pirate.result.GameDataResult
import com.yuhang.novel.pirate.ui.ad.adapter.GameAdapter

class GameViewModel : BaseViewModel() {

    val adapter: GameAdapter by lazy { GameAdapter() }

    val objList by lazy { hashMapOf<String, Int>() }
    /**
     * 下载view
     */
    val progressView by lazy { hashMapOf<String, ProgressLayout>() }

    fun getTestData(): List<GameDataResult> {
        val list = arrayListOf<GameDataResult>()
        list.add(GameDataResult().apply {
            this.name = "克鲁赛德战记"
            this.image = "https://img.tapimg.com/market/lcs/8d47be91bf3e1a672f281a6615f3765a_360.png"
            this.gameType = "动作类游戏"
            this.size = 123432493
            this.description = "可勾起回忆的复古的像素风格"
            this.downloadUrl = "http://cqaostd.redbeancorp.com/cq2/com.nhnst.SKCQCN.otaku_REAL03_4.20.0.CK_level2.apk"
            this.packageName = "com.nhnst.SKCQCN.otaku"
        })
        list.add(GameDataResult().apply {
            this.name = "戰國幻武"
            this.image = "https://img.tapimg.com/market/lcs/03f1fd1285ac27142b6322cc0cb7ef15_360.png"
            this.gameType = "策略类游戏"
            this.size = 124432493
            this.description = "体验不一样的战场"
            this.downloadUrl = "http://gncn.comicocn.com/707/otaku2017_12_19_19_53_51_1000000059.apk"
            this.packageName = "com.Otaku.dazhanguowuyu"
        })
        list.add(GameDataResult().apply {
            this.name = "欢乐魏蜀吴"
            this.image = "https://img.tapimg.com/market/icons/bac029492cc43c00ebb22a30fe92bcfd_360.png"
            this.gameType = "动作类游戏"
            this.size = 156432493
            this.description = "最强三国志RPG游戏"
            this.downloadUrl = "http://ksaostd.redbeancorp.com/apk/com.nhnst.KSCN.otaku_v2.36.2_REAL01_1000000052_level2.apk"
            this.packageName = "com.nhnst.KSCN.otaku"
        })
        return list
    }

    /**
     * 创建下载任务
     */
    fun createTask(list : List<GameDataResult>):List<GameDataResult> {
        list.forEachIndexed { index, gameDataResult ->
            val task = DownloadTask.Builder(gameDataResult.downloadUrl, RxFileTool.getCacheFolder(mActivity))
                .setFilename("${RxTool.Md5(gameDataResult.downloadUrl)}.apk")
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(300)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false)
                .setAutoCallbackToUIThread(true)
                .setSyncBufferSize(65536)
                .setFlushBufferSize(16384)
                .build()
            task.tag = gameDataResult.downloadUrl
            objList[gameDataResult.downloadUrl] = index
            gameDataResult.task = task
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
        obj.task?.file?.let {
            RxFileTool.deleteFile(it)
        }
        adapter.notifyDataSetChanged()
        mActivity?.niceToast("清除成功")
    }
}