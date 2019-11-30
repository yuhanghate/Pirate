package com.yuhang.novel.pirate.listener

import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity

interface OnClickCategoryListener {

    fun onClickCategoryListener(obj: CategoryKDEntity, position: Int)
}