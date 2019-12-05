package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_16_17 : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //章节表增加索引
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_BookChapterKSEntity_bookId` ON `BookChapterKSEntity` (`bookId`)")
        //内容表增加索引
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_BookContentKSEntity_bookId` ON `BookContentKSEntity` (`bookId`)")
    }


    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_16_17()
        }
    }
}