package com.yuhang.novel.pirate.repository.network.data.kanshu.result

/**
 * name : 作品相关
 * list : [{"id":9788879,"name":"仙界篇外传一","hasContent":1},{"id":9788880,"name":"仙界篇外传二","hasContent":1},null]
 */
data class ChapterDirResult(val name: String = "",
                            val list: List<ChapterResult?>?)