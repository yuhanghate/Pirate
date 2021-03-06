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
import com.yuhang.novel.pirate.repository.database.dao.BooksKSDao;
import com.yuhang.novel.pirate.repository.database.dao.CategoryKDDao;
import com.yuhang.novel.pirate.repository.database.dao.ConfigDao;
import com.yuhang.novel.pirate.repository.database.dao.PushMessageDao;
import com.yuhang.novel.pirate.repository.database.dao.RankingListDao;
import com.yuhang.novel.pirate.repository.database.dao.SearchHistoryKSDao;
import com.yuhang.novel.pirate.repository.database.dao.SexBooksDao;
import com.yuhang.novel.pirate.repository.database.dao.ShuDanDao;
import com.yuhang.novel.pirate.repository.database.dao.StoreDao;
import com.yuhang.novel.pirate.repository.database.entity.BooksKSEntity;
import com.yuhang.novel.pirate.repository.database.entity.SexBooksEntity;
import com.yuhang.novel.pirate.repository.database.entity.ShuDanEntity;
import com.yuhang.novel.pirate.repository.database.entity.StoreEntity;
import com.yuhang.novel.pirate.repository.database.dao.StoreRankingDao;
import com.yuhang.novel.pirate.repository.database.dao.UserDao;
import com.yuhang.novel.pirate.repository.database.entity.BookChapterKSEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookCollectionKSEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookDownloadEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookInfoKSEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookReadHistoryEntity;
import com.yuhang.novel.pirate.repository.database.entity.BookResouceTypeKDEntity;
import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity;
import com.yuhang.novel.pirate.repository.database.entity.ConfigEntity;
import com.yuhang.novel.pirate.repository.database.entity.PushMessageEntity;
import com.yuhang.novel.pirate.repository.database.entity.RankingListEntity;
import com.yuhang.novel.pirate.repository.database.entity.SearchHistoryKSEntity;
import com.yuhang.novel.pirate.repository.database.entity.StoreRankingEntity;
import com.yuhang.novel.pirate.repository.database.entity.UserEntity;
import com.yuhang.novel.pirate.repository.database.migration.Migration_13_16;
import com.yuhang.novel.pirate.repository.database.migration.Migration_14_16;
import com.yuhang.novel.pirate.repository.database.migration.Migration_16_17;
import com.yuhang.novel.pirate.repository.database.migration.Migration_17_18;
import com.yuhang.novel.pirate.repository.database.migration.Migration_18_19;
import com.yuhang.novel.pirate.repository.database.migration.Migration_19_20;
import com.yuhang.novel.pirate.repository.database.migration.Migration_20_21;
import com.yuhang.novel.pirate.repository.database.migration.Migration_21_22;
import com.yuhang.novel.pirate.repository.database.migration.Migration_22_23;
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
        CategoryKDEntity.class, ConfigEntity.class, StoreRankingEntity.class, StoreEntity.class,
        BooksKSEntity.class, ShuDanEntity.class, SexBooksEntity.class},
        version = 23, exportSchema = false)
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
                            .addMigrations(
                                    Migration_3_4.instance(),
                                    Migration_4_5.instance(),
                                    Migration_5_6.instance(),
                                    Migration_6_7.instance(),
                                    Migration_13_16.instance(),
                                    Migration_14_16.instance(),
                                    Migration_16_17.instance(),
                                    Migration_17_18.instance(),
                                    Migration_18_19.instance(),
                                    Migration_19_20.instance(),
                                    Migration_20_21.instance(),
                                    Migration_21_22.instance(),
                                    Migration_22_23.instance()
                            )
                            .fallbackToDestructiveMigration()
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

    /**
     * 获取配置
     * @return
     */
    public abstract ConfigDao getConfigDao();

    /**
     * 书城排行榜
     * @return
     */
    public abstract StoreRankingDao getStoreRankingDao();

    /**
     * 返回书城列表页
     * @return
     */
    public abstract StoreDao getStoreDao();

    /**
     * 获取看书神器 存储对象
     * 书城 -> 点击更多
     *
     * 书城 -> 排行榜
     * @return
     */
    public abstract BooksKSDao getBooksKSDao();

    /**
     * 书城 -> 书单
     * @return
     */
    public abstract ShuDanDao getShuDanDao();

    /**
     * 小黄书列表
     * @return
     */
    public abstract SexBooksDao getSexBooksDao();

}
