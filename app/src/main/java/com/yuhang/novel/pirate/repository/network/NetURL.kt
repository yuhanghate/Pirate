package com.yuhang.novel.pirate.repository.network

object NetURL {


//    var type = NetType.DEVELOP
    var type = NetType.MASTER


    //后台API调用接口
    private const val HOST_DEVELOP = "http://192.168.123.159:8081/"
//    private const val HOST_DEVELOP = "http://192.168.1.143:8081"
    private const val HOST_MASTER = "http://www.suibiankanshu.com:8088"
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
     * 快读
     */
    val HOST_KUAIDU = "http://api.wgfgr.cn"

    /**
     * 用户注册
     */
    val USER_REGISTER = "${HOST}mock/17/uaa/api/register"
}