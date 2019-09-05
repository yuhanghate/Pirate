package com.yuhang.novel.pirate.ui.book.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.github.aakira.expandablelayout.Utils
import com.google.android.material.appbar.AppBarLayout
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseSwipeBackActivity
import com.yuhang.novel.pirate.constant.UMConstant
import com.yuhang.novel.pirate.databinding.ActivityBookDetailsBinding
import com.yuhang.novel.pirate.databinding.LayoutBookDetailsAuthorAllBookItemBinding
import com.yuhang.novel.pirate.databinding.LayoutBookDetailsAuthorAllBookLineBinding
import com.yuhang.novel.pirate.eventbus.UpdateChapterEvent
import com.yuhang.novel.pirate.extension.niceCoverPic
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceToast
import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookDetailsDataResult
import com.yuhang.novel.pirate.ui.book.viewmodel.BookDetailsViewModel
import com.yuhang.novel.pirate.utils.StatusBarUtil
import com.yuhang.novel.pirate.utils.SystemUtil
import jp.wasabeef.glide.transformations.BlurTransformation
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.abs


/**
 * 小说详情页
 * 简介/目录/加入书架/立即阅读
 */
class BookDetailsActivity : BaseSwipeBackActivity<ActivityBookDetailsBinding, BookDetailsViewModel>(),
    AppBarLayout.OnOffsetChangedListener {

    /**
     * 标题行为
     */
    enum class TitleEvent {

        ENTER_START, //进入动画开始
        ENTER_END,  //进入动画结束
        EXIT_START, //退出动画开始
        EXIT_END, //退出动画结束
        DEFAULT //默认
    }


    /**
     * 是否显示标题动画
     */
    private var animationEvent = TitleEvent.DEFAULT

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

        initToolbarHeight()

    }

    private fun initToolbarHeight() {
//        mBinding.root.setPadding(0, 0, 0, StatusBarUtil.getNavigationBarSize(this).y - StatusBarUtil.getStatusBarHeight(this))
//
//        mBinding.tabNavigationBar.setPadding(0, 0, 0, StatusBarUtil.getNavigationBarSize(this).y * 3 - StatusBarUtil.getStatusBarHeight(this))
//        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//        layoutParams.height = StatusBarUtil.getNavigationBarSize(this).y
//        layoutParams.height = StatusBarUtil.getRealHeight(this) - mBinding.appBar.height
//        mBinding.tabNavigationBar.layoutParams = layoutParams

        mBinding.statusBarV.layoutParams.height = StatusBarUtil.getStatusBarHeight(this)
    }

    override fun initStatusTool() {
        StatusBarUtil.setTranslucentForCoordinatorLayout(this, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            var height = 0
            val resourceId = applicationContext.resources.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                height = applicationContext.resources.getDimensionPixelSize(resourceId);
            }
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (SystemUtil.getDeviceBrand() == "Meizu") {
                layoutParams.height = StatusBarUtil.getNavigationBarSize(this).y
            } else {
                layoutParams.height =
                    StatusBarUtil.getNavigationBarSize(this).y - StatusBarUtil.getStatusBarHeight(this)
            }

            mBinding.tabNavigationBar.layoutParams = layoutParams

//            mBinding.statusBarV.layoutParams.height = height
        }
    }

    override fun onPause() {
        super.onPause()
        mViewModel.onPageEnd("我的页面")
    }

    override fun onResume() {
        super.onResume()
        initCollectionStatus()
        mViewModel.onPageStart("书箱详情页")
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
            mViewModel.onUMEvent(this, UMConstant.TYPE_DETAILS_CLICK_READ, "立即阅读")
            ReadBookActivity.start(this, getBookid())
        }
        mBinding.addBookrackTv.setOnClickListener {

            //            showDialogCollection(mViewModel.isCollection)
            if (mViewModel.isCollection) {
                mViewModel.onUMEvent(this, UMConstant.TYPE_DETAILS_CLICK_REMOVE_BOOKCASE, "移出书架")
                removeCollection()
            } else {
                mViewModel.onUMEvent(this, UMConstant.TYPE_DETAILS_CLICK_REMOVE_BOOKCASE, "加入书架")
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
            val bookName = mViewModel.obj?.Name ?: ""
            mViewModel.onUMEvent(
                this,
                UMConstant.TYPE_DETAILS_CLICK_DIR_CHANPTER,
                hashMapOf(
                    "action" to "书箱详情 -> 点击单独章节目录",
                    "bookName" to bookName,
                    "bookId" to getBookid().toString()
                )
            )
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
                        mViewModel.onUMEvent(
                            this,
                            UMConstant.TYPE_DETAILS_CLICK_AUTHOR_OTHER_BOOK,
                            hashMapOf(
                                "action" to "书箱详情 -> 点击作者其他作品",
                                "bookId" to book.Id.toString(),
                                "bookName" to book.Name,
                                "author" to book.Author
                            )
                        )
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

        if (scrollRange - offset == 0f) {

            //滑入标题动画
            animationEvent = TitleEvent.ENTER_START
            mBinding.includeToolbarOpen.root.visibility = View.INVISIBLE
            mBinding.includeToolbarClose.root.visibility = View.VISIBLE

            mBinding.includeToolbarClose.titleCloseTv.visibility = View.VISIBLE
            mBinding.includeToolbarClose.backCloseIv.visibility = View.VISIBLE

            val animationIn = AnimationUtils.loadAnimation(this, R.anim.slide_book_details_in)
            animationIn.interpolator = Utils.createInterpolator(Utils.LINEAR_OUT_SLOW_IN_INTERPOLATOR) as Interpolator
            mBinding.includeToolbarClose.titleCloseTv.startAnimation(animationIn)
            Handler().postDelayed({
                mBinding.includeToolbarClose.titleCloseTv.visibility = View.VISIBLE
                animationEvent = TitleEvent.ENTER_END
            }, 500)

        } else if (animationEvent == TitleEvent.ENTER_END) {

            //标题滑出动画
            animationEvent = TitleEvent.EXIT_START
            mBinding.includeToolbarClose.root.visibility = View.VISIBLE
            val animationOut = AnimationUtils.loadAnimation(this, R.anim.slide_book_details_out)
            animationOut.interpolator = Utils.createInterpolator(Utils.LINEAR_OUT_SLOW_IN_INTERPOLATOR) as Interpolator
            animationOut.fillAfter = true
            mBinding.includeToolbarClose.titleCloseTv.startAnimation(animationOut)
            Handler().postDelayed({
                //                mBinding.includeToolbarClose.titleCloseTv.visibility = View.INVISIBLE
                animationEvent = TitleEvent.EXIT_END
            }, 600)
        } else if (animationEvent == TitleEvent.DEFAULT || animationEvent == TitleEvent.EXIT_END) {
            animationEvent = TitleEvent.DEFAULT
            mBinding.includeToolbarClose.root.visibility = View.VISIBLE
        }

        Logger.t("offset").i("alpha=$alpha4")

        mBinding.includeToolbarClose.backCloseIv.alpha = 1 - alpha4
        mBinding.includeToolbarOpen.root.alpha = alpha4
        mBinding.statusBarV.alpha = 1 - alpha4
        mBinding.includeToolbarClose.root.alpha = 1 - alpha4

        if (offset == 0f) {
            //打开
            mBinding.includeToolbarClose.root.visibility = View.INVISIBLE
            mBinding.includeToolbarOpen.root.visibility = View.VISIBLE
            mBinding.includeToolbarOpen.root.alpha = 1f
            mBinding.statusBarV.visibility = View.VISIBLE
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
                //点击立即阅读,返回就刷新一次
                initCollectionStatus()
                //插入或更新书箱信息
                mViewModel.insertBookInfoEntity()
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