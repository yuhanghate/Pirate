package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_13_16 : Migration(13, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `CategoryKDEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bookCover` TEXT NOT NULL, `count` INTEGER NOT NULL, `gender` TEXT NOT NULL, `majorCate` TEXT NOT NULL, `order` TEXT NOT NULL)")
    }

    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_13_16()
        }
    }
}