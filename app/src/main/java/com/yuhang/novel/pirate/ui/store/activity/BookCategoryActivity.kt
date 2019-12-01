package com.yuhang.novel.pirate.ui.store.activity

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityBookCategoryBinding
import com.yuhang.novel.pirate.listener.OnClickCategoryListener
import com.yuhang.novel.pirate.repository.database.entity.CategoryKDEntity
import com.yuhang.novel.pirate.ui.store.adapter.CategoryAdapter
import com.yuhang.novel.pirate.ui.store.adapter.CategoryTitleAdapter
import com.yuhang.novel.pirate.ui.store.viewmodel.BookCategoryViewModel

/**
 * 小说分类
 * 男生/女生/出版
 */
class BookCategoryActivity :
    BaseSwipeBackActivity<ActivityBookCategoryBinding, BookCategoryViewModel>(),
    OnClickCategoryListener {

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, BookCategoryActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_book_category
    }

    override fun initView() {
        super.initView()
        onClick()
        initRecyclerView()
    }

    private fun onClick() {
        mBinding.layoutToolbar.btnBack.setOnClickListener { onBackPressedSupport() }
        mBinding.layoutToolbar.titleTv.text = "选择分类"
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        val layoutManager = VirtualLayoutManager(this)
        mBinding.recyclerview.layoutManager = layoutManager
        mViewModel.adapter = DelegateAdapter(layoutManager, true)
        mBinding.recyclerview.adapter = mViewModel.adapter
    }

    override fun initData() {
        super.initData()
        mViewModel.getCategoryList()
            .compose(bindToLifecycle())
            .subscribe({

                mViewModel.adapter.addAdapter(getTitleAdapter("男生"))
                mViewModel.adapter.addAdapter(getCategoryAdatper(mViewModel.man))

                mViewModel.adapter.addAdapter(getTitleAdapter("女生"))
                mViewModel.adapter.addAdapter(getCategoryAdatper(mViewModel.lady))

                mViewModel.adapter.addAdapter(getTitleAdapter("出版"))
                mViewModel.adapter.addAdapter(getCategoryAdatper(mViewModel.press))

                mBinding.recyclerview.requestLayout()
                mViewModel.adapter
            },{})

    }

    /**
     * 获取标题
     */
    private fun getTitleAdapter(obj:String): DelegateAdapter.Adapter<RecyclerView.ViewHolder> {
        val adapter = CategoryTitleAdapter()
            .setListener(this)
            .initData(obj)

        return adapter.toAdapter()
    }

    /**
     * 列表item
     */
    private fun getCategoryAdatper(obj: List<CategoryKDEntity>): DelegateAdapter.Adapter<RecyclerView.ViewHolder> {
        val adapter = CategoryAdapter()
            .setListener(this)
            .initData(obj)
        return adapter.toAdapter()
    }

    /**
     * 分类点击
     */
    override fun onClickCategoryListener(obj: CategoryKDEntity, position: Int) {
        CategoryDetailActivity.start(this, obj.gender, obj.majorCate)
    }
}