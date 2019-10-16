package com.yuhang.novel.pirate.repository.network.data.resouce.result

/**
 * 书源信息
 */
class ResouceRuleResult {
    var bookSourceUrl: String = ""
    var bookSourceName: String = ""
    var bookSourceGroup: String = ""
    var bookSourceType: String = ""
    var loginUrl: String = ""
    var lastUpdateTime: Long = 0
    var serialNumber: Int = 0
    var weight = 0
    var enable: Boolean = false
    //发现规则
    var ruleFindUrl: String = ""
    var ruleFindList: String = ""
    var ruleFindName: String = ""
    var ruleFindAuthor: String = ""
    var ruleFindKind: String = ""
    var ruleFindIntroduce: String = ""
    var ruleFindLastChapter: String = ""
    var ruleFindCoverUrl: String = ""
    var ruleFindNoteUrl: String = ""
    //搜索规则
    var ruleSearchUrl: String = ""
    var ruleSearchList: String = ""
    var ruleSearchName: String = ""
    var ruleSearchAuthor: String = ""
    var ruleSearchKind: String = ""
    var ruleSearchIntroduce: String = ""
    var ruleSearchLastChapter: String = ""
    var ruleSearchCoverUrl: String = ""
    var ruleSearchNoteUrl: String = ""
    //详情页规则
    var ruleBookUrlPattern: String = ""
    var ruleBookInfoInit: String = ""
    var ruleBookName: String = ""
    var ruleBookAuthor: String = ""
    var ruleCoverUrl: String = ""
    var ruleIntroduce: String = ""
    var ruleBookKind: String = ""
    var ruleBookLastChapter: String = ""
    var ruleChapterUrl: String = ""
    //目录页规则
    var ruleChapterUrlNext: String = ""
    var ruleChapterList: String = ""
    var ruleChapterName: String = ""
    var ruleContentUrl: String = ""
    //正文页规则
    var ruleContentUrlNext: String = ""
    var ruleBookContent: String = ""
    var httpUserAgent: String = ""
}