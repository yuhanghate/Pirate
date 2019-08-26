package com.yuhang.novel.pirate.ui.book.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.google.android.material.appbar.AppBarLayout
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.databinding.ActivityBookDetailsBinding
import com.yuhang.novel.pirate.databinding.LayoutBookDetailsAuthorAllBookItemBinding
import com.yuhang.novel.pirate.databinding.LayoutBookDetailsAuthorAllBookLineBinding
import com.yuhang.novel.pirate.eventbus.UpdateChapterEvent
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookDetailsDataResult
import com.yuhang.novel.pirate.ui.book.viewmodel.BookDetailsViewModel
import jp.wasabeef.glide.transformations.BlurTransformation
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.abs
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat


/**
 * 小说详情页
 * 简介/目录/加入书架/立即阅读
 */
class BookDetailsActivity : BaseSwipeBackActivity<ActivityBookDetailsBinding, BookDetailsViewModel>(),
    AppBarLayout.OnOffsetChangedListener {


    companion object {
        const val BOOK_ID = "book_id"
        fun start(context: Activity, bookid: Long) {
            val intent = Intent(context, BookDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(BOOK_ID, bookid)
            startIntent(context, intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_book_details
    }

    private fun getBookid() = intent.getLongExtra(BOOK_ID, -1)

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        netServiceData()
        onClick()
        //每次打开详情页加载一下章节目录 .防止进入阅读时本地获取不到章节
        mViewModel.updateChapterToDB(getBookid()).compose(bindToLifecycle()).subscribe({}, {})

    }

    override fun onStart() {
        super.onStart()
        //点击立即阅读,返回就刷新一次
        initCollectionStatus()
    }

    /**
     * 书箱是否在书架中
     */
    private fun initCollectionStatus() {
        thread {
            val bookid = mViewModel.obj?.Id ?: return@thread
            val collection = mViewModel.queryCollection(bookid)
            runOnUiThread {
                if (collection != null) {
                    mViewModel.isCollection = true
                    mBinding.addBookrackTv.text = "移出书架"
                } else {
                    mViewModel.isCollection = false
                    mBinding.addBookrackTv.text = "加入书架"
                }


            }

        }
    }

    /**
     *
     */
    private fun onClick() {
        mBinding.appBar.addOnOffsetChangedListener(this)
        mBinding.includeToolbarClose.backCloseIv.setOnClickListener { onBackPressed() }
        mBinding.includeToolbarOpen.backOpenIv.setOnClickListener { onBackPressed() }
        mBinding.openReadBookTv.setOnClickListener {

            ReadBookActivity.start(this, getBookid())
        }
        mBinding.addBookrackTv.setOnClickListener {

            //            showDialogCollection(mViewModel.isCollection)
            if (mViewModel.isCollection) {
                removeCollection()
            } else {
                addCollection()
            }
        }
    }

    /**
     * 重新加载数据
     */
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun resetView(obj: BookDetailsDataResult) {
        Glide.with(this).load(niceCoverPic(obj.Img)).into(mBinding.includeToobarHeadOpen.coverIv)
        Glide.with(this).load(niceCoverPic(obj.Img))
            .apply(bitmapTransform(BlurTransformation(20, 5) as Transformation<Bitmap>))
            .into(mBinding.includeToobarHeadOpen.bgCoverIv)

        val details = mBinding.layoutBookDetails
        details.statusTv.setText("状态   ${obj.BookStatus}", null)
        details.authorTv.text = obj.Author
        details.titleTv.text = obj.Name
        details.chapterNameTv.text = obj.LastChapter
        details.bookTypeTv.text = obj.CName
        details.descTv.text = obj.Desc
        details.authorAllBookTv.text = "${obj.Author} 的全部作品"
        details.timeTv.text = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date(obj.LastTime))

        mBinding.includeToolbarClose.titleCloseTv.text = obj.Name

        //目录点击
        mBinding.layoutBookDetails.chapterListLl.setOnClickListener {
            ChapterListActivity.start(
                this,
                getBookid(),
                obj.LastChapterId
            )
        }

        //动态加载作者全部作品
        details.authorAllBookLl.removeAllViews()

        if (obj.SameUserBooks.isNotEmpty()) {
            obj.SameUserBooks.forEachIndexed { index, recommendBooksResult ->
                recommendBooksResult?.let { book ->

                    //作品Item
                    val itemBinding = LayoutBookDetailsAuthorAllBookItemBinding.inflate(layoutInflater)
                    itemBinding.titleTv.text = book.Name
                    itemBinding.chapterNameTv.text = book.LastChapter

                    val drawable = getDrawable(R.drawable.ic_default_cover)
                    val placeholder =
                        RequestOptions().transforms(CenterCrop(), RoundedCorners(niceDp2px(3f)))
                            .placeholder(drawable)
                            .error(drawable)
                    Glide.with(this).load(niceCoverPic(book.Img)).apply(placeholder).into(itemBinding.coverIv)

                    //分隔线
                    val lineBinding = LayoutBookDetailsAuthorAllBookLineBinding.inflate(layoutInflater)

                    //点击跳转作者相关详情
                    itemBinding.itemLl.setOnClickListener {
                        BookDetailsActivity.start(this, book.Id)
                    }

                    details.authorAllBookLl.addView(itemBinding.root)
                    details.authorAllBookLl.addView(lineBinding.root)

                }
            }

            details.authorAllBookLl.removeViewAt(details.authorAllBookLl.childCount - 1)
        }
    }

    /**
     * 头部滑动事件
     */
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout ?: return
        //垂直方向偏移量
        val offset = abs(verticalOffset).toFloat();
        //最大偏移距离
        val scrollRange = appBarLayout.totalScrollRange.toFloat()


        val alpha4 = (scrollRange - offset) / scrollRange
        Logger.i("offset = $offset scrollRange = $scrollRange  alpha4 = $alpha4")
        if (scrollRange - offset < 5) {
            mBinding.includeToolbarOpen.root.visibility = View.GONE
            mBinding.includeToolbarClose.root.visibility = View.VISIBLE
            mBinding.includeToolbarClose.root.alpha = 1f
        } else {
            mBinding.includeToolbarOpen.root.visibility = View.VISIBLE
            mBinding.includeToolbarClose.root.visibility = View.GONE
            mBinding.includeToolbarOpen.bgToolbarOpen.setBackgroundColor(Color.parseColor("#ffffff"))
            mBinding.includeToolbarOpen.bgToolbarOpen.alpha = 1 - alpha4
        }
    }

    /**
     * 获取详情数据
     */
    @SuppressLint("CheckResult")
    private fun netServiceData() {
        mBinding.loading.showLoading()
        mViewModel.getBookDetails(getBookid())
            .compose(bindToLifecycle())
            .subscribe({
                mViewModel.obj = it
                mBinding.loading.showContent()
                resetView(it)
            }, {
                Logger.e(it.message!!)
                mBinding.loading.showError()
            })
    }


    /**
     * 增加收藏
     */
    @SuppressLint("CheckResult")
    private fun addCollection() {
        val bookid = mViewModel.obj?.Id ?: return
        mViewModel.postCollection(bookid)
        mViewModel.insertCollection(bookid)
            .compose(bindToLifecycle())
            .subscribe({
                mViewModel.isCollection = true
                mBinding.addBookrackTv.text = "移出书架"
                //插入或更新书箱信息
                mViewModel.insertBookInfoEntity()
                EventBus.getDefault().post(UpdateChapterEvent())
                niceToast("加入成功")
            }, { niceToast("加入失败") })
    }

    /**
     * 移除收藏
     */
    @SuppressLint("CheckResult")
    private fun removeCollection() {
        val bookid = mViewModel.obj?.Id ?: return
        mViewModel.deleteCollection(bookid)
            .compose(bindToLifecycle())
            .subscribe({
                mViewModel.isCollection = false
                mBinding.addBookrackTv.text = "加入书架"
                niceToast("移除成功")
            }, { niceToast("加入失败") })
    }
}