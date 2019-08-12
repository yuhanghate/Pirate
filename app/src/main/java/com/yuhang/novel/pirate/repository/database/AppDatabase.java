package com.yuhang.novel.pirate.repository.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.yuhang.novel.pirate.repository.database.dao.*;
import com.yuhang.novel.pirate.repository.database.entity.*;
import com.yuhang.novel.pirate.repository.database.migration.Migration_3_4;
import com.yuhang.novel.pirate.repository.database.migration.Migration_4_5;
import com.yuhang.novel.pirate.repository.database.migration.Migration_5_6;


/**
 * e-mail : 714610354@qq.com
 * time   : 2018/04/24
 * desc   : 数据库
 *
 * @author yuhang
 */
@Database(entities = {BookInfoKSEntity.class, AuthorBookKSEntity.class, BookCollectionKSEntity.class,
        SearchHistoryKSEntity.class, BookChapterKSEntity.class, BookContentKSEntity.class,
        BookReadHistoryEntity.class, UserEntity.class}, version = 5, exportSchema = false)
@TypeConverters({ConvertersFactory.class})
public abstract class AppDatabase
        extends RoomDatabase {
    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "Priate";

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
//                            .allowMainThreadQueries()
                            .addMigrations(Migration_3_4.instance(), Migration_4_5.instance(), Migration_5_6.instance())
                            .build();
        }
        return INSTANCE;
    }


    /**
     * 查询书籍详情
     *
     * @return 书籍详情
     */
    public abstract BookInfoKSDao getBookInfoKSDao();

    /**
     * 查询作者相关的书籍
     */
    public abstract AuthorBookKSDao getAuthorBookKSDao();

    /**
     * 查询搜索记录列表
     */
    public abstract SearchHistoryKSDao getSearchHistoryKSDao();

    /**
     * 章节列表
     */
    public abstract BookChapterKSDao getBookChapterKSDao();

    /**
     * 章节内容
     */
    public abstract BookContentKSDao getBookContentKSDao();

    /**
     * 收藏书籍
     *
     * @return
     */
    public abstract BookCollectionKSDao getBookCollectionKSDao();

    /**
     * 阅读历史记录
     *
     * @return
     */
    public abstract BookReadHistoryKSDao getBookReadHistoryKSDao();

    /**
     * 用户名
     * @return
     */
    public abstract  UserDao getUserDao();


}
