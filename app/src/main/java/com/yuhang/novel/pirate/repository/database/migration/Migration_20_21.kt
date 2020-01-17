package com.yuhang.novel.pirate.repository.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_20_21 : Migration(20, 21) {
    override fun migrate(database: SupportSQLiteDatabase) {

        //把书箱详情表错误的看书源改成快读
        database.execSQL("update bookinfoksentity set resouce = 'KD' where length(bookid) > 12")
        //把收藏表错误的看书源改成快读
        database.execSQL("update bookcollectionksentity set resouce = 'KD' where length(bookid) > 12")
    }


    companion object {
        @JvmStatic
        fun instance(): Migration {
            return Migration_20_21()
        }
    }
}