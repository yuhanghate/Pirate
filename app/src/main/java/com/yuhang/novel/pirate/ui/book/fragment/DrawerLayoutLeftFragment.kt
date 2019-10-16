package com.yuhang.novel.pirate.ui.book.fragment

import android.annotation.SuppressLint
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.databinding.FragmentDrawerlayoutLeftBinding
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.listener.OnClickChapterItemListener
import com.yuhang.novel.pirate.listener.OnClickItemListener
import com.yuhang.novel.pirate.ui.book.viewmodel.DrawerlayoutLeftViewModel
import com.yuhang.novel.pirate.utils.DateUtils
import java.util.*

/**
 * 阅读界面左滑出来的章节目录
 */
class DrawerLayoutLeftFragment : BaseFragment<FragmentDrawerlayoutLeftBinding, DrawerlayoutLeftViewModel>(),
        OnClickItemListener {


    var bookid: String? = null

    var chapterid: Int? = null

    /**
     * 排序状态
     * true: 顶部  false: 底部
     */
    private var sortStatus = true

    var mOnClickChapterItemListener: OnClickChapterItemListener? = null

    override fun onLayoutId(): Int {
        return R.layout.fragment_drawerlayout_left
    }

    override fun initView() {
        super.initView()
        initHeaderView()
        initRecyclerView()
        onClick()
        initBackground()
        initLastChapterItem()
    }

    /**
     * 初始化背景颜色
     */
    private fun initBackground() {
        mBinding.root.setBackgroundColor(BookConstant.getPageBackground())
    }


    /**
     * 第一次打开,item移动到内容对应的章节
     */
    @SuppressLint("CheckResult")
    fun initLastChapterItem() {
        bookid ?: return
        mViewModel.queryLastChapter(bookid= bookid!!)
                .subscribe({
                    it?.chapterId?.let { id ->
                        setCurrentReadItem(id)
                    }
                }, {})
    }

    /**
     * 改变背景颜色
     */
    fun resetBackground() {
        mBinding.root.setBackgroundColor(BookConstant.getPageBackground())
        mViewModel.adapter.notifyDataSetChanged()
        mBinding.recyclerView.scrollToPosition(mViewModel.adapter.chapterid)
        mBinding.itemDrawerHeader.athorTv.setTextColor(BookConstant.getPageTextColor())
        mBinding.itemDrawerHeader.titleTv.setTextColor(BookConstant.getPageTextColor())
        mBinding.itemDrawerHeader.updateTimeTv.setTextColor(BookConstant.getPageTextColor())
        mBinding.itemDrawerHeader.chapterListTv.setTextColor(BookConstant.getPageTextColor())
    }

    private fun onClick() {
        mBinding.itemDrawerHeader.sortIv.setOnClickListener {
            if (mViewModel.adapter.getList().size == 0) {
                return@setOnClickListener
            }

            if (sortStatus) {
                sortStatus = false
                mBinding.recyclerView.scrollToPosition(mViewModel.adapter.itemCount - 15)
                mBinding.recyclerView.smoothScrollToPosition(mViewModel.adapter.getList().size - 1)
                val animation = AnimationUtils.loadAnimation(activity!!, R.anim.rotate_sort_top)
                animation.interpolator = AccelerateInterpolator()
                animation.fillAfter = true
                mBinding.itemDrawerHeader.sortIv.startAnimation(animation)
//                mBinding.itemDrawerHeader.sortIv.setImageResource(R.drawable.btn_chapter_top)

            } else {
                sortStatus = true
                if (mViewModel.adapter.getList().size > 15) {
                    mBinding.recyclerView.scrollToPosition(15)
                    mBinding.recyclerView.smoothScrollToPosition(0)
                } else {
                    mBinding.recyclerView.smoothScrollToPosition(0)
                }

                val animation = AnimationUtils.loadAnimation(activity, R.anim.rotate_sort_button)
                animation.interpolator = AccelerateInterpolator()
                animation.fillAfter = true
                mBinding.itemDrawerHeader.sortIv.startAnimation(animation)
//                mBinding.itemDrawerHeader.sortIv.setImageResource(R.drawable.btn_chapter_button)
            }
        }


    }

    /**
     * 获取目录头部
     */
    @SuppressLint("CheckResult")
    private fun initHeaderView() {
        bookid ?: return
        mViewModel.queryBookInfo(bookid!!)
                .compose(bindToLifecycle())
                .subscribe({ bookInfo ->
                    mBinding.itemDrawerHeader.athorTv.text = bookInfo?.author
                    mBinding.itemDrawerHeader.titleTv.text = bookInfo?.bookName
                    mBinding.itemDrawerHeader.updateTimeTv.text = DateUtils.getTimeZhanxin(Date(bookInfo?.lastTime!!))

                    mBinding.itemDrawerHeader.converIv.let {

                        val drawable = ContextCompat.getDrawable(mActivity!!, R.drawable.ic_default_img)
                        val placeholder =
                                RequestOptions().transforms(CenterCrop(), RoundedCorners(niceDp2px(3f)))
                                        .placeholder(drawable)
                                        .error(drawable)
                        Glide.with(this).load(niceCoverPic(bookInfo?.cover))
                                .apply(placeholder)
                                .into(mBinding.itemDrawerHeader.converIv)
                    }
                }, {})

//        mBinding.root.setPadding(0, 0, 0, StatusBarUtil.getStatusBarHeight(activity))
    }

    override fun initRecyclerView() {
        super.initRecyclerView()

        mViewModel.adapter.setListener(this)
        mViewModel.adapter
            .setlayoutManager(LinearLayoutManager(mActivity))
                .setRecyclerView(mBinding.recyclerView, false)
        mBinding.fastscroll.setRecyclerView(mBinding.recyclerView)

        bookid?.let {
            mViewModel.queryChapterList(it)
                    .compose(bindToLifecycle())
                    .subscribe({ list ->
                        mViewModel.adapter.setRefersh(list)
                    }, {})
        }
    }

    /**
     * Item点击事件
     */
    override fun onClickItemListener(view: View, position: Int) {
        //章节目录点击事件
        mOnClickChapterItemListener?.onClickChapterItemListener(view, mViewModel.adapter.getObj(position).chapterId)
        setCurrentReadItem(mViewModel.adapter.getObj(position).chapterId)
    }

    /**
     * 设置当前读取的目录章节
     */
    fun setCurrentReadItem(chapterid: Int) {
        if (mViewModel.adapter.chapterid != -1 && chapterid == mViewModel.adapter.chapterid) return

        var position = -1
        mViewModel.adapter.getList().forEachIndexed { index, bookChapterKSEntity ->
            if (bookChapterKSEntity.chapterId == chapterid) {
                position = index
            }
        }
        mViewModel.adapter.chapterid = chapterid
        mViewModel.adapter.notifyDataSetChanged()
        mBinding.recyclerView.scrollToPosition(position)
    }
}