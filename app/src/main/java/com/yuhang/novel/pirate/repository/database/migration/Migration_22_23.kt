package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_22_23 : Migration(22, 23) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL("CREATE TABLE IF NOT EXISTS `SexBooksEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `author` TEXT NOT NULL, `bookId` INTEGER NOT NULL, `bookName` TEXT NOT NULL, `bookUrl` TEXT NOT NULL, `description` TEXT NOT NULL, `lastChapterName` TEXT NOT NULL)")
    }


    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_22_23()
        }
    }
}