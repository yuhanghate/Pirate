package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_18_19 : Migration(18, 19) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //增加一列vip
        database.execSQL("ALTER TABLE `UserEntity` ADD COLUMN `isVip` INTEGER NOT NULL DEFAULT 0")
        //增加一列是否开启vip
        database.execSQL("ALTER TABLE `ConfigEntity` ADD COLUMN `isOpenVip` INTEGER NOT NULL DEFAULT 0")
    }


    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_18_19()
        }
    }
}