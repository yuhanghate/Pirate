package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity
import kotlin.properties.Delegates

class BookCategoryViewModel : BaseViewModel() {

    var lastTabEntity = 0

    var adapter: DelegateAdapter by Delegates.notNull()

    val man = arrayListOf<CategoryKDEntity>()
    val lady = arrayListOf<CategoryKDEntity>()
    val press = arrayListOf<CategoryKDEntity>()

    /**
     * 获取所有分类
     * 男生/女生/出版
     */
    suspend fun getCategoryList() {
        this.man.addAll(mDataRepository.queryCategoryMan())
        this.lady.addAll(mDataRepository.queryCategoryLady())
        this.press.addAll(mDataRepository.queryCategoryPress())
    }
}