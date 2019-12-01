package com.yuhang.novel.pirate.repository.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.yuhang.novel.pirate.repository.database.dao.BookChapterKSDao;
import com.yuhang.novel.pirate.repository.database.dao.BookCollectionKSDao;
import com.yuhang.novel.pirate.repository.database.dao.BookContentKSDao;
import com.yuhang.novel.pirate.repository.database.dao.BookDownloadDao;
import com.yuhang.novel.pirate.repository.database.dao.BookInfoKSDao;
import com.yuhang.novel.pirate.repository.database.dao.BookReadHistoryDao;
import com.yuhang.novel.pirate.repository.database.dao.BookResouceTypeKDDao;
import com.yuhang.novel.pirate.repository.database.dao.CategoryKDDao;
import com.yuhang.novel.pirate.repository.database.dao.PushMessageDao;
import com.yuhang.novel.pirate.repository.database.dao.RankingListDao;
import com.yuhang.novel.pirate.repository.database.dao.SearchHistoryKSDao;
import com.yuhang.novel.pirate.repository.database.dao.UserDao;
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookCollectionKSEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookReadHistoryEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookResouceTypeKDEntity;
import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity;
import com.yuhang.novel.pirate.repository.database.entity.PushMessageEntity;
import com.yuhang.novel.pirate.repository.database.entity.RankingListEntity;
import com.yuhang.novel.pirate.repository.database.entity.SearchHistoryKSEntity;
import com.yuhang.novel.pirate.repository.database.entity.UserEntity;
import com.yuhang.novel.pirate.repository.database.migration.Migration_13_16;
import com.yuhang.novel.pirate.repository.database.migration.Migration_14_16;
import com.yuhang.novel.pirate.repository.database.migration.Migration_3_4;
import com.yuhang.novel.pirate.repository.database.migration.Migration_4_5;
import com.yuhang.novel.pirate.repository.database.migration.Migration_5_6;
import com.yuhang.novel.pirate.repository.database.migration.Migration_6_7;


/**
 * e-mail : 714610354@qq.com
 * time   : 2018/04/24
 * desc   : 数据库
 *
 * @author yuhang
 */
@Database(entities = {BookInfoKSEntity.class, BookCollectionKSEntity.class,
        SearchHistoryKSEntity.class, BookChapterKSEntity.class, BookContentKSEntity.class,
        UserEntity.class, RankingListEntity.class, BookReadHistoryEntity.class,
        PushMessageEntity.class, BookResouceTypeKDEntity.class, BookDownloadEntity.class,
        CategoryKDEntity.class},
        version = 16, exportSchema = false)
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
                            .allowMainThreadQueries()
                            .addMigrations(Migration_3_4.instance(),
                                    Migration_4_5.instance(),
                                    Migration_5_6.instance(),
                                    Migration_6_7.instance(),
                                    Migration_13_16.instance(),
                                    Migration_14_16.instance())
//                            .fallbackToDestructiveMigration()
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
     * 用户名
     *
     * @return
     */
    public abstract UserDao getUserDao();


    /**
     * 排行榜
     *
     * @return
     */
    public abstract RankingListDao getRankingListDao();

    /**
     * 最近阅读章节记录
     *
     * @return
     */
    public abstract BookReadHistoryDao getBookReadHistoryDao();


    /**
     * 推送类型
     *
     * @return
     */
    public abstract PushMessageDao getPushMessageDao();

    /**
     * 快读源类型
     *
     * @return
     */
    public abstract BookResouceTypeKDDao getBookResouceTypeKDDao();

    /**
     * 下载表
     *
     * @return
     */
    public abstract BookDownloadDao getBookDownloadDao();

    /**
     * 快读分类
     *
     * @return
     */
    public abstract CategoryKDDao getCategoryKDDao();

}
