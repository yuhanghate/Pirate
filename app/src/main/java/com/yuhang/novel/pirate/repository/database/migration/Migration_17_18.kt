package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_17_18 : Migration(17, 18) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //增加配置表
        database.execSQL("CREATE TABLE IF NOT EXISTS `ConfigEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `showGameRecommended` INTEGER NOT NULL, `showSexBook` INTEGER NOT NULL)")
    }


    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_17_18()
        }
    }
}