package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.orhanobut.logger.Logger


class Migration_1_16 : Migration(1, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Logger.i("")
    }

    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_1_16()
        }
    }
}