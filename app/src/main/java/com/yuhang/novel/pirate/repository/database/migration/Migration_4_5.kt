package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_4_5 : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `BookReadHistoryEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `chapterid` INTEGER NOT NULL, `bookid` INTEGER NOT NULL, `lastReadTime` INTEGER NOT NULL)")
    }

    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_4_5()
        }
    }
}