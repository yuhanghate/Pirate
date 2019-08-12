package com.netease.nim.demo.koltinapplication.repository.network

import com.yuhang.novel.pirate.repository.network.NetType

object NetURL {
    //    var type = if (BuildConfig.APPServiceDebug) {
//        NetType.DEVELOP
//    } else {
//        NetType.MASTER
//    }
    var type = NetType.DEVELOP


    //后台API调用接口
    private const val HOST_DEVELOP = "http://47.97.41.233:8088/"
    private const val HOST_MASTER = "http://zhanxin.nbmankan.com/"
    private const val HOST_QA = "http://47.100.216.212:3000/mock/17/"
    private const val HOST_STAGE = "http://47.100.216.212:3000/mock/17/"

    /**
     * 资源文件
     */
    val HOST_RESOUCE = "http://47.97.41.233:8090"

    val HOST :String by lazy {
        when (type) {
            NetType.DEVELOP -> return@lazy HOST_DEVELOP
            NetType.MASTER -> return@lazy HOST_MASTER
            NetType.QA -> return@lazy HOST_QA
            NetType.STAGE -> return@lazy HOST_STAGE
            else -> return@lazy HOST_DEVELOP
        }
    }

    /**
     * 看书神器API
     */
    val HOST_KANSHU = "https://infos.xllxdg.com"

    /**
     * 用户注册
     */
    val USER_REGISTER = "${HOST}mock/17/uaa/api/register"
}