package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_14_16 : Migration(14, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //分类表
        database.execSQL("CREATE TABLE IF NOT EXISTS `CategoryKDEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bookCover` TEXT NOT NULL, `count` INTEGER NOT NULL, `gender` TEXT NOT NULL, `majorCate` TEXT NOT NULL, `order` INTEGER NOT NULL)")

        //后台下载表
//        database.execSQL("CREATE TABLE IF NOT EXISTS `BookDownloadEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bookName` TEXT NOT NULL, `progress` INTEGER NOT NULL, `total` INTEGER NOT NULL, `resouce` TEXT NOT NULL, `bookId` TEXT NOT NULL, `cover` TEXT NOT NULL, `author` TEXT NOT NULL, `uuid` TEXT NOT NULL)")
    }


    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_14_16()
        }
    }
}