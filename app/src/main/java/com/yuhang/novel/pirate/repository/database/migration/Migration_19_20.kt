package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_19_20 : Migration(19, 20) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //书城 -> 排行榜
        database.execSQL("CREATE TABLE IF NOT EXISTS `StoreRankingEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `hot` TEXT NOT NULL, `over` TEXT NOT NULL, `commend` TEXT NOT NULL, `collect` TEXT NOT NULL, `newX` TEXT NOT NULL, `vote` TEXT NOT NULL, `genderType` TEXT NOT NULL)")
        //书城 -> 列表
        database.execSQL("CREATE TABLE IF NOT EXISTS `StoreEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `Category` TEXT NOT NULL, `Books` TEXT NOT NULL, `genderType` TEXT NOT NULL)")
        //点击更多列表 + 排行榜列表
        database.execSQL("CREATE TABLE IF NOT EXISTS `BooksKSEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `Img` TEXT NOT NULL, `Score` REAL NOT NULL, `LastChapter` TEXT NOT NULL, `Author` TEXT NOT NULL, `bookId` TEXT NOT NULL, `LastChapterId` TEXT NOT NULL, `CName` TEXT NOT NULL, `Desc` TEXT NOT NULL, `Name` TEXT NOT NULL, `BookStatus` TEXT NOT NULL, `gender` TEXT NOT NULL, `type` TEXT NOT NULL, `date` TEXT NOT NULL, `toobarName` TEXT NOT NULL)")
        //书单列表页
        database.execSQL("CREATE TABLE IF NOT EXISTS `ShuDanEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `AddTime` TEXT NOT NULL, `BookCount` INTEGER NOT NULL, `CollectionCount` INTEGER NOT NULL, `CommendCount` INTEGER NOT NULL, `Cover` TEXT NOT NULL, `Description` TEXT NOT NULL, `ForMan` INTEGER NOT NULL, `IsCheck` INTEGER NOT NULL, `ListId` INTEGER NOT NULL, `Title` TEXT NOT NULL, `UpdateTime` TEXT NOT NULL, `UserName` TEXT NOT NULL, `toolbarName` TEXT NOT NULL, `type` TEXT NOT NULL, `gender` TEXT NOT NULL)")
    }


    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_19_20()
        }
    }
}