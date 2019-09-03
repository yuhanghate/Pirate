package com.yuhang.novel.pirate.ui.search.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivitySearchBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.search.viewmodel.SearchViewModel

/**
 * 搜索书籍
 */
class SearchActivity : BaseSwipeBackActivity<ActivitySearchBinding, SearchViewModel>(), FloatingSearchView.OnQueryChangeListener,
        FloatingSearchView.OnSearchListener, FloatingSearchView.OnFocusChangeListener, OnClickItemListener {

    /**
     * 键盘输入间隔
     */
    private var inputKeywordTime: Long = 0

    companion object {
        const val DURATION: Long = 190
        fun start(context: Activity) {
            val intent = Intent(context, SearchActivity::class.java)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_search
    }

    override fun initView() {
        super.initView()
        initFloatingSearch()
        initRecyclerView()
    }

    /**
     * 初始化列表
     */
    override fun initRecyclerView() {
        super.initRecyclerView()

        mViewModel.adapter.setListener(this)
        mViewModel.adapter.setDecorationSize(niceDp2px(15f))
                .setDecorationColor(android.R.color.transparent)
                .setRecyclerView(mBinding.recyclerview)
    }

    /**
     * 初始化搜索控件
     */
    private fun initFloatingSearch() {

        mBinding.floatingSearchView.setMenuItemIconColor(android.R.color.white)
        mBinding.floatingSearchView.setOnQueryChangeListener(this)
        mBinding.floatingSearchView.setOnSearchListener(this)
        mBinding.floatingSearchView.setOnFocusChangeListener(this)
        mBinding.floatingSearchView.setOnHomeActionClickListener { onBackPressed() }
    }

    /**
     * 根据关键字模糊匹配本地
     */
    private fun queryKeywordSearch(keyword: String?) {
        keyword ?: return
        mViewModel.queryListHisotry(keyword)
                .compose(bindToLifecycle())
                .subscribe({
                    mBinding.floatingSearchView.swapSuggestions(it)
                    mBinding.floatingSearchView.setSearchText(mViewModel.lastKeyword)
                }, {
                    mBinding.floatingSearchView.swapSuggestions(arrayListOf())
                })
    }

    /**
     * 关键字搜索
     */
    @SuppressLint("CheckResult")
    private fun netServiceSearch(keyword: String?) {
        keyword ?: return

        mViewModel.lastKeyword = keyword

        mViewModel.searchBook(keyword)
                .filter { it.status == 1 }
                .subscribe({
                    mViewModel.adapter.setRefersh(it.data)
                    mBinding.floatingSearchView.hideProgress()
                }, {
                    mBinding.floatingSearchView.hideProgress()
                })
    }

    /**
     * 输入栏内容变化回调
     */
    override fun onSearchTextChanged(oldQuery: String?, newQuery: String?) {
        if (oldQuery != "" && newQuery == "") {
            mBinding.floatingSearchView.clearSuggestions()
        } else {

            //搜索间隔大于700毫秒 并且 不是删除动作
            if (System.currentTimeMillis() - inputKeywordTime > 700 && !oldQuery?.startsWith(newQuery!!)!!) {
                inputKeywordTime = System.currentTimeMillis()
                mBinding.floatingSearchView.showProgress()
                netServiceSearch(newQuery)
                queryKeywordSearch(newQuery)
            }

        }
    }

    /**
     * 点击历史搜索
     */
    override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
        mBinding.floatingSearchView.showProgress()
        if (searchSuggestion != null) {
            netServiceSearch(searchSuggestion.body)

            mBinding.floatingSearchView.clearSearchFocus()
        }
    }

    /**
     * 键盘点击搜索回调
     */
    override fun onSearchAction(currentQuery: String?) {
        currentQuery?.let {
            mViewModel.insertSearchHistory(currentQuery)
            netServiceSearch(currentQuery)
        }

    }

    /**
     * 获取焦点
     * 显示历史查询记录
     */
    @SuppressLint("CheckResult")
    override fun onFocus() {
        mViewModel.getSearchHistory()
                .compose(bindToLifecycle())
                .subscribe({
                    mBinding.floatingSearchView.swapSuggestions(it)
                    mBinding.floatingSearchView.setSearchText(mViewModel.lastKeyword)
                }, {
                    mBinding.floatingSearchView.swapSuggestions(arrayListOf())
                })

        mBinding.bgShadow.visibility = View.VISIBLE
        mBinding.bgShadow.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_transparent))


    }

    /**
     * 失去焦点
     * 显示最一次搜索内容
     */
    override fun onFocusCleared() {
        mBinding.floatingSearchView.setSearchText(mViewModel.lastKeyword)
        mBinding.bgShadow.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_transparent))
        Handler().postDelayed({
            mBinding.bgShadow.visibility = View.INVISIBLE

        }, DURATION)
    }

    /**
     * 搜索结果点击
     */
    override fun onClickItemListener(view: View, position: Int) {
        val obj = mViewModel.adapter.getObj(position)
        mViewModel.insertSearchHistory(obj.Name)
        BookDetailsActivity.start(this, obj.Id)
    }
}