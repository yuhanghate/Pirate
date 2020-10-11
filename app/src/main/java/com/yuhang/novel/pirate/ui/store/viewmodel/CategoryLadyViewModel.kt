package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity
import kotlin.properties.Delegates

class CategoryLadyViewModel:BaseViewModel() {

    var adapter: DelegateAdapter by Delegates.notNull()

    var list = arrayListOf<CategoryKDEntity>()

    /**
     * 男生分类
     */
    suspend fun getCategoryLady(): List<CategoryKDEntity> {
        return mDataRepository.queryCategoryLady()
    }
}