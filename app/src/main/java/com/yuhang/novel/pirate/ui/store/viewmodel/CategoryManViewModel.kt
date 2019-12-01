package com.yuhang.novel.pirate.ui.store.viewmodel

import com.alibaba.android.vlayout.DelegateAdapter
import com.yuhang.novel.pirate.base.BaseViewModel
import com.yuhang.novel.pirate.extension.io_main
import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity
import io.reactivex.Flowable
import kotlin.properties.Delegates

class CategoryManViewModel : BaseViewModel() {

    var adapter: DelegateAdapter by Delegates.notNull()

    var list = arrayListOf<CategoryKDEntity>()

    /**
     * 男生分类
     */
    fun getCategoryMan(): Flowable<List<CategoryKDEntity>> {
        return Flowable.just("")
            .map { mDataRepository.queryCategoryMan() }
            .compose(io_main())
    }
}