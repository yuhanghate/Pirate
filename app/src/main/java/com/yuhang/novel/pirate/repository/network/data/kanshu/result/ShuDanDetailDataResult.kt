package com.yuhang.novel.pirate.repository.network.data.kanshu.result

/**
 * Id : 684748
 * BookId : 278977
 * BookName : 肆虐次元的无限剑制
 * BookImage : sinveciyuandewuxianjianzhi.jpg
 * Author : 梦入炎方
 * CategoryName : 玄幻奇幻
 * Score : 8.0
 * Description : 【尽管还不知道你是什么样地存在，但以后也请多多指教了，对了，我叫卫宫士郞。】
　　红衣英灵抬起了头，仰望着天空，轻轻的说出了这段话语。
　　。。。。。。。。。。
　　注意：本作主角为平行世界的卫宫士郞。
 */
data class ShuDanDetailDataResult(
    var Author: String = "",
    var BookId: Int = 0,
    var BookImage: String = "",
    var BookName: String = "",
    var CategoryName: String = "",
    var Description: String = "",
    var Id: Int = 0,
    var Score: Double = 0.0
)