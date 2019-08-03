package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_3_4 : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
    }

    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_3_4()
        }
    }
}