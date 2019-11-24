package com.yuhang.novel.pirate.repository.network.data.kanshu.result

/**
 * ListId : 873
 * UserName : 1575676740
 * ForMan : true
 * Cover : zuiqiangzhuangbidalianxitong.jpg
 * Title : 小黄书，激情又澎湃?
 * Description : 看小黄书，节操全无！妹纸太单纯，老衲太善良。↓↓↓
 * BookCount : 21
 * CollectionCount : 15081
 * CommendCount : 2143
 * IsCheck : true
 * AddTime : 2018/01/18 23:20:18
 * UpdateTime : 2018/02/25 15:55:21
 * CommendImage : https://image.zsdfm.com/shudan/images/6.jpg
 */
data class BooksListDataResult(
    var AddTime: String = "",
    var BookCount: Int = 0,
    var CollectionCount: Int = 0,
    var CommendCount: Int = 0,
    var Cover: String = "",
    var Description: String = "",
    var ForMan: Boolean = false,
    var IsCheck: Boolean = false,
    var ListId: Int = 0,
    var Title: String = "",
    var UpdateTime: String = "",
    var UserName: String = ""
)