package com.yuhang.novel.pirate.repository.network.data.pirate.result

data class BookResouceListResult(
        var checkTime: Long?,//检测时间
        var heat: Int?,//热度
        var id: String="",//id
        var resouceRule: String?,//html提取规则
        var status: String?,//源状态
        var title: String?,//源标题
        var updateTime: Long?,//更新规则时间
        var websiteUrl: String?,//源地址
        var isCheck: Int = 0//是否选中
)