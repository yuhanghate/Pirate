package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.StoreEntity
import com.yuhang.novel.pirate.repository.database.entity.StoreRankingEntity
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BooksKSResult
import com.yuhang.novel.pirate.ui.common.model.RankingModel
import com.yuhang.novel.pirate.ui.common.model.StoreRankingModel
import com.yuhang.novel.pirate.ui.store.activity.KanShuRankingActivity
import kotlin.properties.Delegates

/**
 * 女生
 */
class LadyViewModel : BaseViewModel() {

    var adapter: DelegateAdapter by Delegates.notNull()

    val titleList = hashMapOf(
        "热门连载" to "女生畅销书", "火热新书" to "热门新书",
        "重磅推荐" to "主编推荐", "完本精选" to "完本精品"
    )

    val rankingMap = hashMapOf<String, String>(
        "女生畅销书" to KanShuRankingActivity.TYPE_HOT,
        "热门新书" to KanShuRankingActivity.TYPE_NEW,
        "主编推荐" to KanShuRankingActivity.TYPE_COMMEND,
        "完本精品" to KanShuRankingActivity.TYPE_OVER
    )

    /**
     * 热门连载
     */
    val hotList: ArrayList<BooksKSResult> by lazy { arrayListOf<BooksKSResult>() }

    /**
     * 火热新书
     */
    val newList: ArrayList<BooksKSResult> by lazy { arrayListOf<BooksKSResult>() }

    /**
     * 重磅推荐
     */
    val recommendList: ArrayList<BooksKSResult> by lazy { arrayListOf<BooksKSResult>() }

    /**
     * 完本精选
     */
    val goodList: ArrayList<BooksKSResult> by lazy { arrayListOf<BooksKSResult>() }

    /**
     * 榜单
     */
    val rankingList: ArrayList<StoreRankingModel> by lazy { arrayListOf<StoreRankingModel>() }

    /**
     * 榜单
     */
    val list = arrayListOf<RankingModel>()


    /**
     * 获取本地精选
     */
    suspend fun queryStoreMan(): List<StoreEntity> {
        return mDataRepository.queryStoreEntity("lady")
    }

    /**
     * 本地查询  书城 -> 榜单 -> 女生
     */
    suspend fun queryStoreRankingMan(): StoreRankingEntity {
        return mDataRepository.queryStoreRanking("lady")
            ?: return StoreRankingEntity()
    }


    /**
     * 获取精选
     */
    suspend fun getStoreLady(): List<StoreEntity> {
        val storeLady = mDataRepository.getStoreLady()
        storeLady.data.forEach { obj -> obj.apply { this.genderType = "lady" } }
        mDataRepository.deleteStoreEntity("lady")
        mDataRepository.insertStoreEntity(storeLady.data)
        return storeLady.data
    }

    /**
     * 书城 -> 榜单 -> 女生
     */
    suspend fun getStoreRankingLady(): StoreRankingEntity {
        val storeRankingLady = mDataRepository.getStoreRankingLady()
        storeRankingLady.data.apply { this.genderType = "lady" }
        mDataRepository.cleanStoreRanking("lady")
        mDataRepository.insertStoreRanking(storeRankingLady.data)
        return storeRankingLady.data
    }

    /**
     * 分解类型
     */
    fun buildBook(list: List<StoreEntity>) {
        hotList.clear()
        newList.clear()
        recommendList.clear()
        goodList.clear()
        list.forEach {
            when (it.Category) {
                "热门连载" -> hotList.addAll(it.Books)
                "火热新书" -> newList.addAll(it.Books)
                "重磅推荐" -> recommendList.addAll(it.Books)
                "完本精选" -> goodList.addAll(it.Books)
            }
        }
    }

    /**
     * 女生榜单
     */
    fun buildRanking(obj: StoreRankingEntity) {
        rankingList.clear()
        rankingList.add(
            StoreRankingModel(
                name = "女生热读榜",
                list = obj.hot,
                desc = "优质精品好书新鲜出炉!",
                background = R.color.md_pink_300
            ).apply { rankingMap[name] = KanShuRankingActivity.TYPE_HOT }
        )

        rankingList.add(
            StoreRankingModel(
                name = "完本榜",
                list = obj.over,
                desc = "不用等更了,喜欢的都有",
                background = R.color.md_purple_300
            ).apply { rankingMap[name] = KanShuRankingActivity.TYPE_OVER }
        )

        rankingList.add(
            StoreRankingModel(
                name = "收藏榜",
                list = obj.commend,
                desc = "火热好书",
                background = R.color.md_deep_orange_300
            ).apply { rankingMap[name] = KanShuRankingActivity.TYPE_COMMEND }
        )

        rankingList.add(
            StoreRankingModel(
                name = "潜力榜",
                list = obj.newX,
                desc = "更多好书等你来~",
                background = R.color.md_light_blue_300
            ).apply { rankingMap[name] = KanShuRankingActivity.TYPE_NEW }
        )

        rankingList.add(
            StoreRankingModel(
                name = "好评榜",
                list = obj.vote,
                desc = "超多好评,万人追更!",
                background = R.color.md_blue_300
            ).apply { rankingMap[name] = KanShuRankingActivity.TYPE_COLLECT }
        )

        rankingList.add(
            StoreRankingModel(
                name = "人气榜",
                list = obj.collect,
                desc = "超强人气,等待阅读",
                background = R.color.md_green_300
            ).apply { rankingMap[name] = KanShuRankingActivity.TYPE_VOTE }
        )
    }

    /**
     * 排行榜
     */
    fun getRankingModelList(): List<RankingModel> {
        list.clear()
        list.add(RankingModel(name = "起点中文网", type = 2))
        list.add(RankingModel(name = "纵横中文网", type = 4))
        list.add(RankingModel(name = "云起书院", type = 7))
        list.add(RankingModel(name = "红薯中文网", type = 9))
        list.add(RankingModel(name = "若初文学网", type = 8))

        return list
    }
}