package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity
import kotlin.properties.Delegates

class CategoryManViewModel : BaseViewModel() {

    var adapter: DelegateAdapter by Delegates.notNull()

    var list = arrayListOf<CategoryKDEntity>()

    /**
     * 男生分类
     */
    suspend fun getCategoryMan(): List<CategoryKDEntity> {
        return mDataRepository.queryCategoryMan()
    }
}