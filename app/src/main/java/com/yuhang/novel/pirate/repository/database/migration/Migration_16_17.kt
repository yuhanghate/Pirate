package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_16_17 : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //分类表
        database.execSQL("CREATE TABLE `TestEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NUll, `testName` TEXT NOT NULL, `testCode` INTEGER NOT NULL)")

        //后台下载表
//        database.execSQL("CREATE TABLE IF NOT EXISTS `BookDownloadEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bookName` TEXT NOT NULL, `progress` INTEGER NOT NULL, `total` INTEGER NOT NULL, `resouce` TEXT NOT NULL, `bookId` TEXT NOT NULL, `cover` TEXT NOT NULL, `author` TEXT NOT NULL, `uuid` TEXT NOT NULL)")
    }


    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_16_17()
        }
    }
}