package com.yuhang.novel.pirate.push

data class CustomPush(
    var message: String = "",
    var title: String = "",
    var type: String = ""
)