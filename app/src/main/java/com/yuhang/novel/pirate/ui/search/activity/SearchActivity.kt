package com.yuhang.novel.pirate.ui.search.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivitySearchBinding
import com.yuhang.novel.pirate.databinding.LayoutSearchHistoryBinding
import com.yuhang.novel.pirate.extension.clickWithTrigger
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.listener.OnClickSearchSuggestListener
import com.yuhang.novel.pirate.repository.network.data.pirate.result.BooksResult
import com.yuhang.novel.pirate.ui.book.activity.BookDetailsActivity
import com.yuhang.novel.pirate.ui.search.viewmodel.SearchViewModel
import com.yuhang.novel.pirate.ui.settings.activity.SearchFeedbackActivity
import com.yuhang.novel.pirate.widget.TextWatcherAdapter


/**
 * 搜索书籍
 */
class SearchActivity : BaseSwipeBackActivity<ActivitySearchBinding, SearchViewModel>(),
    OnClickItemListener,TextView.OnEditorActionListener, OnClickSearchSuggestListener {


    var LONG_SEARCH_SUGGES = System.currentTimeMillis()

    companion object {
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
        initRecyclerView()
        initHistoryView()
        onClick()
    }

    /**
     * 初始化列表
     */
    override fun initRecyclerView() {
        super.initRecyclerView()

        mViewModel.adapter.setDecorationSize(niceDp2px(15f))
            .setListener(this)
            .setlayoutManager(LinearLayoutManager(this))
            .setDecorationColor(android.R.color.transparent)
            .setRecyclerView(mBinding.recyclerview)

        mViewModel.searchAdapter
            .setlayoutManager(LinearLayoutManager(this))
            .setListener(this)
            .setRecyclerView(mBinding.searchSuggestRecyclerview)

    }

    private fun onClick() {
        mBinding.btnResouce.clickWithTrigger { showResouceDialog() }
        mBinding.searchEt.setOnEditorActionListener(this)
        mBinding.btnCancel.clickWithTrigger { onBackPressedSupport() }
        mBinding.btnClear.clickWithTrigger{mBinding.searchEt.setText("", null)}
        mBinding.searchEt.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                if (TextUtils.isEmpty(s?.toString())) {
                    mBinding.btnClear.visibility = View.GONE
                    mBinding.recyclerview.visibility = View.GONE
                    return
                }
                mBinding.btnClear.visibility = View.VISIBLE

                if (System.currentTimeMillis() - LONG_SEARCH_SUGGES > 700 && mViewModel.searchSuggestStr != s?.toString()) {
                    netServiceSearchSuggest(s?.toString())
                    LONG_SEARCH_SUGGES = System.currentTimeMillis()
                }

            }
        })
    }


    /**
     * 关键字搜索
     */
    @SuppressLint("CheckResult")
    private fun netServiceSearch(keyword: String?) {
        if (TextUtils.isEmpty(keyword)) return

        showProgress()
        mViewModel.lastKeyword = keyword?.trim()!!
        mViewModel.resouce = if (mBinding.resouceTv.text.toString() == "笔趣阁") "KS" else "KD"
        mViewModel.insertSearchHistory(keyword.trim())
        mViewModel.searchBookV2(keyword.trim())
            .compose(bindToLifecycle())
            .subscribe({
                val list = arrayListOf<BooksResult>()
                list.add(BooksResult())
                list.addAll(it)
                mViewModel.adapter.setRefersh(list)
                mBinding.recyclerview.visibility = View.VISIBLE
                mBinding.searchSuggestRecyclerview.visibility = View.GONE
                hideProgress()
            }, {
                hideProgress()
                mBinding.recyclerview.visibility = View.VISIBLE
                mBinding.searchSuggestRecyclerview.visibility = View.GONE
            })
    }

    /**
     * 关键字联想
     */
    private fun netServiceSearchSuggest(keyword: String?) {
        if (TextUtils.isEmpty(keyword)) {
            mBinding.searchSuggestRecyclerview.visibility = View.GONE
            return
        }

        mViewModel.searchSuggest(keyword!!)
            .compose(bindToLifecycle())
            .subscribe({
                mBinding.searchSuggestRecyclerview.visibility = View.VISIBLE
                mViewModel.searchAdapter.setRefersh(it)
            },{
                mBinding.searchSuggestRecyclerview.visibility = View.GONE
            })
    }

    /**
     * 显示进度条
     */
    private fun showProgress() {
        mBinding.progressRl.visibility = View.VISIBLE
    }

    /**
     * 不显示进度条
     */
    private fun hideProgress() {
        mBinding.progressRl.visibility = View.GONE
    }


    /**
     * 键盘搜索
     */
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId === EditorInfo.IME_ACTION_SEARCH) {
            // 当按了搜索之后关闭软键盘
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(
                    this.currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )

            netServiceSearch(v?.text.toString().trim())
            return true
        }
        return false
    }


    /**
     * 初始化历史记录
     */
    @SuppressLint("CheckResult")
    private fun initHistoryView() {
        mViewModel.getSearchHistory()
            .compose(bindToLifecycle())
            .subscribe({
                it.filter { !TextUtils.isEmpty(it?.keyword) }.forEach {result ->
                    val tag = createHistoryTag(result?.keyword!!)
                    mBinding.flexboxLayout.addView(tag)
                }
            }, {})
    }

    /**
     * 创建历史记录View
     */
    private fun createHistoryTag(keyword: String) :View{
        val binding = LayoutSearchHistoryBinding.inflate(LayoutInflater.from(this))
        binding.contentTv.text = keyword
        binding.contentTv.clickWithTrigger {
            mViewModel.searchSuggestStr = keyword
            mBinding.searchEt.setText(keyword, null)
            mBinding.searchEt.setSelection(keyword.length)
            netServiceSearch(keyword)
        }
        return binding.root
    }


    /**
     * 搜索结果点击
     */
    override fun onClickItemListener(view: View, position: Int) {

        //精确求书
        if (position == 0) {
            SearchFeedbackActivity.start(this)
            return
        }

        //书籍详情页
        val obj = mViewModel.adapter.getObj(position)
        mViewModel.insertSearchHistory(obj.bookName)
        mViewModel.onUMEvent(
            this,
            UMConstant.TYPE_SEARCH_ITEM_CLICK,
            hashMapOf(
                "action" to "搜索 -> 点击搜索结果页",
                "bookName" to obj.bookName,
                "author" to obj.author,
                "bookType" to obj.kind
            )
        )
        BookDetailsActivity.start(this, obj)
    }


    /**
     * 搜索源选择
     */
    private fun showResouceDialog() {
        MaterialDialog(this).show {
            listItems(items = arrayListOf("笔趣阁")) { dialog, index, text ->
                mBinding.resouceTv.text = text
                netServiceSearch(mBinding.searchEt.text.toString())
            }
        }
    }

    override fun onResume() {

        super.onResume()
        mViewModel.onResume(this)
    }

    override fun onPause() {

        super.onPause()
        mViewModel.onPause(this)
    }

    /**
     * 联想搜索点击
     */
    override fun onClickSearchSuggestListener(position: Int) {
        val text = mViewModel.searchAdapter.getObj(position).text.trim()
        mViewModel.searchSuggestStr = text
        mBinding.searchEt.setText(text)
        mBinding.searchSuggestRecyclerview.visibility = View.GONE
        netServiceSearch(text)
    }
}