package com.yuhang.novel.pirate.widget.pageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.orhanobut.logger.Logger;
import com.yuhang.novel.pirate.app.PirateApp;
import com.yuhang.novel.pirate.constant.ConfigConstant;
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil;
import com.yuhang.novel.pirate.utils.ContextUtil;
import com.yuhang.novel.pirate.utils.IOUtils;
import com.yuhang.novel.pirate.utils.PageUtils;
import com.yuhang.novel.pirate.utils.ScreenUtils;
import com.yuhang.novel.pirate.utils.StringUtils;
import com.yuhang.novel.pirate.widget.ReadBookTextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 绘制页面显示内容的类
 */
public class TextPagerView extends ReadBookTextView {

    //View的宽高
    private int mViewWidth = 0; // 当前View的宽
    private int mViewHeight = 0; // 当前View的高


    //书籍绘制区域的宽高
    private int mVisibleWidth;
    private int mVisibleHeight;
    //应用的宽高
    private int mDisplayWidth;
    private int mDisplayHeight;
    //间距
    private int mMarginWidthEnd;
    private int mMarginWidthStart;
    private int mMarginHeightTop;
    private int mMarginHeightButton;

    private int mMarginWidth;

    private int mPaddingStart;
    private int mPaddingEnd;
    private int mPaddingTop;
    private int mPaddingButton;
    //字体的颜色
    private int mTextColor = Color.parseColor("#212121");
    //标题的大小
    private float mTitleSize;
    //字体的大小
    private float mTextSize;
    //行间距
    private float mTextInterval;
    //标题的行间距
    private float mTitleInterval;
    //段落距离(基于行间距的额外距离)
    private float mTextPara;
    private float mTitlePara;
    //小标题段落距离
    private float mTipPare;
    //小标题行间距
    private float mTipInterval;

    // 当前显示的页
    private TxtPage mCurPage;
    //当前显示页
    private TextPageBean mTextPageBean;
    // 上一章的页面列表缓存
//    private List<TxtPage> mPrePageList;
    // 当前章节的页面列表
//    private List<TxtPage> mCurPageList;
    // 下一章的页面列表缓存
//    private List<TxtPage> mNextPageList;

    // 绘制电池的画笔
    private Paint mBatteryPaint;
    // 绘制提示的画笔
    private Paint mTipPaint;
    // 绘制标题的画笔
    private Paint mTitlePaint;
    // 绘制背景颜色的画笔(用来擦除需要重绘的部分)
    private Paint mBgPaint;
    // 绘制小说内容的画笔
    private TextPaint mTextPaint;
    //画面背景
    private Bitmap mBitmap;
    //横向或者竖向滑动
    private int mOrientation = ORIENTATION_HORIZONTAL;
    //标题内容
    private String mTitleText = "";
    //电池的百分比
    private int mBatteryLevel = 100;
    //章节内容,流方式
    private BufferedReader mContent;
    //上下翻页/左右翻页
    private int mPageType;
    //章节标题
    private String mTitle;

    private int left;
    private int top;
    private int right;
    private int bottom;

    // 默认的显示参数配置
    private static final int DEFAULT_MARGIN_HEIGHT = 15;
    private static final int DEFAULT_MARGIN_WIDTH = 15;
    private static final int DEFAULT_TIP_SIZE = 12;
    private static final int EXTRA_TITLE_SIZE = 4;

    //竖向方向
    public static final int ORIENTATION_VERTICAL = 1;
    //横向方向
    public static final int ORIENTATION_HORIZONTAL = 2;

    public TextPagerView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public TextPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextPagerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {


        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        prepareDisplay(w, h);
        if (mBitmap == null) {
            mBitmap = createBitmap();
        }

    }

    /**
     * 预加载尺寸宽高
     *
     * @param w 宽度
     * @param h 高度
     */
    void prepareDisplay(int w, int h) {
        // 获取PageView的宽高
        mDisplayWidth = w;
        mDisplayHeight = h;

        // 获取内容显示位置的大小

        resetVisibleWidthAndHeight();

    }

    /**
     * 获取内容显示位置的大小
     */
    private void resetVisibleWidthAndHeight() {
        mVisibleWidth = mDisplayWidth - mMarginWidthStart - mMarginWidthEnd - getPaddingStart() - getPaddingEnd();
        mVisibleHeight = mDisplayHeight - mMarginHeightTop - mMarginHeightButton - getPaddingTop() - getPaddingBottom();
    }


    /**
     * 作用：设置与文字相关的参数
     *
     * @param textSize
     */
    private void setUpTextParams(float textSize) {
        // 文字大小
        mTextSize = textSize;
        mTitleSize = mTextSize + ScreenUtils.spToPx(EXTRA_TITLE_SIZE);
        // 行间距(大小为字体的一半)
        mTextInterval = mTextPaint.getFontSpacing() / 2;
        mTitleInterval = mTitlePaint.getFontSpacing() / 2;
        // 段落间距(大小为字体的高度)
        mTextPara = mTextPaint.getFontSpacing() + mTextInterval;
        mTitlePara = mTitlePaint.getFontSpacing() + mTitleInterval;

        //小标题段落间距
        mTipInterval = mTipPaint.getFontSpacing() / 2;
        mTipPare = mTipPaint.getFontSpacing() + mTipInterval;
    }

    private void initPaint() {
        // 绘制提示的画笔
        mTipPaint = new Paint();
        mTipPaint.setColor(mTextColor);
        mTipPaint.setTextAlign(Paint.Align.LEFT); // 绘制的起始点
        mTipPaint.setTextSize(ScreenUtils.spToPx(DEFAULT_TIP_SIZE)); // Tip默认的字体大小
        mTipPaint.setAntiAlias(true);
        mTipPaint.setSubpixelText(true);


        // 绘制页面内容的画笔
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(getTextSize());
        mTextPaint.setAntiAlias(true);

        // 绘制标题的画笔
        mTitlePaint = new TextPaint();
        mTitlePaint.setColor(mTextColor);
        mTitlePaint.setTextSize(getTextSize() + ScreenUtils.spToPx(EXTRA_TITLE_SIZE));
        mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTitlePaint.setAntiAlias(true);

        // 绘制背景的画笔
//        mBgPaint = new Paint();

        // 绘制电池的画笔
        mBatteryPaint = new Paint();
        mBatteryPaint.setColor(mTextColor);
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setDither(true);

    }


    @Override
    protected void onDraw(Canvas canvas) {

        //绘制背景
        if (getBackground() instanceof ColorDrawable) {
            int color = ((ColorDrawable) getBackground()).getColor();
            canvas.drawColor(color);
        } else if (getBackground() instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) getBackground()).getBitmap();
            canvas.drawBitmap(bitmap, 0, 0, mBgPaint);
        }


//        if (mCurPage != null) {
//
//            drawContent(canvas);
//            drawBackground(canvas);
//        }

        if (mTextPageBean != null) {
            drawContent2(canvas);
            //左右滑动, 上下滑动不显示时间和电池
            if (PirateApp.Companion.getInstance().getPageType() == ConfigConstant.INSTANCE.getPAGE_TYPE_HORIZONTAL()) {
                drawBackground2(canvas);
            }

        }


    }

    private void drawBackground2(Canvas canvas) {

        float y = mDisplayHeight - getPaddingBottom() - mMarginHeightButton - mTipPaint.getFontSpacing();

        // 只有finish的时候采用页码

        String percent = (mTextPageBean.getCurrentPage() + 1) + "/" + mTextPageBean.getMaxPage() + "页";
        canvas.drawText(percent, mDisplayWidth - getPaddingEnd() - mMarginWidthEnd - mTipPaint.measureText(percent) - ContextUtil.getDp(10), y, mTipPaint);


        /******绘制电池********/

        int visibleRight = ContextUtil.getDp(25) + getPaddingStart() + mMarginWidthStart;
        int visibleBottom = (int) y;

        int outFrameWidth = (int) mTipPaint.measureText("xxx");
        int outFrameHeight = (int) mTipPaint.getTextSize();

        int polarHeight = ContextUtil.getDp(6);
        int polarWidth = ContextUtil.getDp(2);
        int border = 1;
        int innerMargin = 1;

        //电极的制作
        int polarLeft = visibleRight - polarWidth;
        int polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2;
        Rect polar = new Rect(polarLeft, polarTop, visibleRight,
                polarTop + polarHeight - ContextUtil.getDp(2));

        mBatteryPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(polar, mBatteryPaint);

        //外框的制作
        int outFrameLeft = polarLeft - outFrameWidth;
        int outFrameTop = visibleBottom - outFrameHeight;
        int outFrameBottom = visibleBottom - ContextUtil.getDp(2);
        Rect outFrame = new Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom);

        mBatteryPaint.setStyle(Paint.Style.STROKE);
        mBatteryPaint.setStrokeWidth(border);
        canvas.drawRect(outFrame, mBatteryPaint);

        //内框的制作
        float innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f);
        RectF innerFrame = new RectF(outFrameLeft + border + innerMargin, outFrameTop + border + innerMargin,
                outFrameLeft + border + innerMargin + innerWidth, outFrameBottom - border - innerMargin);

        mBatteryPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(innerFrame, mBatteryPaint);

        //显示时间
        if (PreferenceUtil.getInt(ConfigConstant.INSTANCE.getPAGE_TIME(), ConfigConstant.INSTANCE.getPAGE_TIME_SHOW()) == ConfigConstant.INSTANCE.getPAGE_TIME_SHOW()) {
            /******绘制当前时间********/
            //底部的字显示的位置Y
            String time = StringUtils.dateConvert(System.currentTimeMillis(), "HH:mm");
            canvas.drawText(time, polarLeft + ContextUtil.getDp(5), outFrameBottom, mTipPaint);
        } else {
            /******绘制当前电量********/
            canvas.drawText((mBatteryLevel) + "%", polarLeft + ContextUtil.getDp(5), outFrameBottom, mTipPaint);

        }

    }


    public Canvas drawContent2(Canvas canvas) {
        PageUtils.PageBean bean = new PageUtils.PageBean();
        bean.titlePaint = mTitlePaint;
        bean.tipPaint = mTipPaint;
        bean.height = mDisplayHeight;
        bean.width = mDisplayWidth;
        bean.marginButtom = mMarginHeightButton;
        bean.marginEnd = mMarginWidthEnd;
        bean.marginStart = mMarginWidthStart;
        bean.marginTop = mMarginHeightTop;
        bean.padddingEnd = getPaddingEnd();
        bean.padddingStart = getPaddingTop();
        bean.paddingButtom = getPaddingBottom();
        bean.paddingTop = getPaddingTop();
        bean.textInterval = mTextInterval;
        bean.tipInterval = mTipInterval;
        bean.textPaint = mTextPaint;
        bean.textPare = mTextPara;
        bean.tipPare = mTipPare;
        bean.titleInterval = mTitleInterval;
        bean.titlePare = mTitlePara;
        bean.title = mTitleText;
        return PageUtils.drawContent(canvas, mTextPageBean, bean);
    }

    public List<TextPageBean> loadPages2(BufferedReader br) {
        PageUtils.PageBean bean = new PageUtils.PageBean();
        bean.titlePaint = mTitlePaint;
        bean.tipPaint = mTipPaint;
        bean.height = mDisplayHeight;
        bean.width = mDisplayWidth;
        bean.marginButtom = mMarginHeightButton;
        bean.marginEnd = mMarginWidthEnd;
        bean.marginStart = mMarginWidthStart;
        bean.marginTop = mMarginHeightTop;
        bean.padddingEnd = getPaddingEnd();
        bean.padddingStart = getPaddingTop();
        bean.paddingButtom = getPaddingBottom();
        bean.paddingTop = getPaddingTop();
        bean.textInterval = mTextInterval;
        bean.tipInterval = mTipInterval;
        bean.textPaint = mTextPaint;
        bean.textPare = mTextPara;
        bean.tipPare = mTipPare;
        bean.titleInterval = mTitleInterval;
        bean.titlePare = mTitlePara;
        bean.title = mTitleText;
        bean.pageType = mPageType;

        try {
            return PageUtils.loadPages(br, bean);
        } catch (IOException e) {
            Logger.t("空白").i("page exception : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将章节数据，解析成页面列表
     *
     * @param br：章节的文本流
     * @return
     */
    public List<TxtPage> loadPages(BufferedReader br) {
        //生成的页面
        List<TxtPage> pages = new ArrayList<>();
        //使用流的方式加载
        List<String> lines = new ArrayList<>();

//        float navige = StatusBarUtil.getNavigationBarSize(getContext()).y;
//        int realHeight = StatusBarUtil.getRealHeight(getContext());
        float rHeight = mVisibleHeight;
        int titleLinesCount = 0;
        boolean showTitle = true; // 是否展示标题
        String paragraph = mTitleText;//默认展示标题

        //设置总距离
        float interval = mTextInterval + (int) mTextPaint.getFontSpacing();
        float para = mTextPara + (int) mTextPaint.getFontSpacing();


        //减去底部电池和页码
        float tipHeightBottom = mTipPaint.getFontSpacing();
        //减去小标题
        float tipHeightTop = mTipPare + mTipPaint.getFontSpacing();

        rHeight -= tipHeightBottom;

        PageUtils.PageBean bean = new PageUtils.PageBean();
        bean.titlePaint = mTitlePaint;
        bean.tipPaint = mTipPaint;
        bean.height = mDisplayHeight;
        bean.width = mDisplayWidth;
        bean.marginButtom = mMarginHeightButton;
        bean.marginEnd = mMarginWidthEnd;
        bean.marginStart = mMarginWidthStart;
        bean.marginTop = mMarginHeightTop;
        bean.padddingEnd = getPaddingEnd();
        bean.padddingStart = getPaddingTop();
        bean.paddingButtom = getPaddingBottom();
        bean.paddingTop = getPaddingTop();
        bean.textInterval = mTextInterval;
        bean.tipInterval = mTipInterval;
        bean.textPaint = mTextPaint;
        bean.textPare = mTextPara;
        bean.tipPare = mTipPare;
        bean.titleInterval = mTitleInterval;
        bean.titlePare = mTitlePara;
        bean.title = mTitleText;

        try {
            while (showTitle || (paragraph = br.readLine()) != null) {
                paragraph = StringUtils.convertCC(paragraph, getContext());
                // 重置段落
                if (!showTitle) {
                    paragraph = paragraph.replaceAll("\\s", "");
//                    paragraph = paragraph.trim();
                    // 如果只有换行符，那么就不执行
                    if (paragraph.equals("")) continue;
                    paragraph = StringUtils.halfToFull("  " + paragraph + "\n");
                } else {
                    //设置 title 的顶部间距
                    rHeight -= mTitlePara;


                }
                int wordCount = 0;
                String subStr = null;
                while (paragraph.length() > 0) {
                    //当前空间，是否容得下一行文字
                    if (showTitle) {
                        rHeight -= mTitlePaint.getFontSpacing();


                    } else {
                        rHeight -= mTextPaint.getFontSpacing();
                    }
                    // 一页已经填充满了，创建 TextPage
                    if (rHeight <= 0) {
                        // 创建Page
                        TxtPage page = new TxtPage();
                        page.position = pages.size();

                        page.lines = new ArrayList<>(lines);
                        page.title = StringUtils.convertCC(mTitleText, getContext());
                        page.titleLines = titleLinesCount;
                        pages.add(page);
                        // 重置Lines
                        lines.clear();

                        rHeight = mVisibleHeight;

                        rHeight -= tipHeightTop;
                        rHeight -= tipHeightBottom;

                        titleLinesCount = 0;

                        continue;
                    }

                    float[] measuredWidth = {0};
                    //测量一行占用的字节数
                    if (showTitle) {
                        wordCount = mTitlePaint.breakText(paragraph,
                                true, mVisibleWidth, measuredWidth);


                    } else {
                        wordCount = mTextPaint.breakText(paragraph,
                                true, mVisibleWidth, measuredWidth);
                    }


                    subStr = paragraph.substring(0, wordCount);
                    //截取的内容处理.分成二行,中间加入行间距
                    if (!subStr.equals("\n") && !subStr.trim().equals("")) {


                        //将一行字节，存储到lines中
                        lines.add(subStr);

                        //设置段落间距
                        if (showTitle) {
                            titleLinesCount += 1;
                            rHeight -= mTitleInterval;


                        } else {
                            rHeight -= mTextInterval;
                        }
                    }
                    //裁剪
                    paragraph = paragraph.substring(wordCount);
                }

                //增加段落的间距
                if (!showTitle && lines.size() != 0) {
                    rHeight = rHeight - mTextPara;
                }

                if (showTitle) {
                    rHeight = rHeight - mTitlePara;
                    showTitle = false;
                }


            }

//            if (lines.size() != 0) {
//                //创建Page
//                TxtPage page = new TxtPage();
//                page.position = pages.size();
//                page.title = StringUtils.convertCC(mTitleText, getContext());
//                page.lines = new ArrayList<>(lines);
//                page.titleLines = titleLinesCount;
//                pages.add(page);
//                //重置Lines
//                lines.clear();
//            }

            /**
             * 记录当前章节所有页数
             */
            for (TxtPage page : pages) {
                page.setPageSize(pages.size());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br);
        }

        return pages;
    }


    private Bitmap createBitmap() {
        return Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);
    }

    /******************  对外公布方法 ***************************/

    /**
     * 更新电量
     *
     * @param level
     */
    public void updateBattery(int level) {
        //电量1%变量进行刷新,防止频繁刷新页面
        if (Math.abs(level - mBatteryLevel) >= 1) {
            mBatteryLevel = level;
            invalidate();
        }

    }

    public TextPagerView setBattery(int level) {
        mBatteryLevel = level;
        return this;
    }

    /**
     * 设置横向或者坚向
     *
     * @param orientation
     */
    public TextPagerView setOrientation(int orientation) {
        mOrientation = orientation;
        return this;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public TextPagerView setTitle(String title) {
        mTitleText = title;
        return this;
    }

    public TextPagerView setPageTextColor(int color) {
        mTextColor = color;
        return this;
    }


    /**
     * 设置内容
     *
     * @param content 章节内容
     * @return
     */
    public TextPagerView setContent(String content) {
        StringReader is = new StringReader(content);
        mContent = new BufferedReader(is);
        return this;
    }

    /**
     * 上下翻页/左右翻页
     *
     * @param pageType
     * @return
     */
    public TextPagerView setPageType(int pageType) {
        this.mPageType = pageType;
        return this;
    }

    /**
     * 设置内容与屏幕的间距
     *
     * @param marginWidthStat :单位为 px
     * @param marginHeightTop :单位为 px
     */
    public TextPagerView setMargin(int marginWidthStat, int marginWidthEnd, int marginHeightTop, int marginHeightButton) {
        mMarginWidthStart = marginWidthStat;
        mMarginWidthEnd = marginWidthEnd;
        mMarginHeightTop = marginHeightTop;
        mMarginHeightButton = marginHeightButton;
        resetVisibleWidthAndHeight();
        return this;
    }

    /**
     * 设置当前的页面
     *
     * @param txtPage
     * @return
     */
    public TextPagerView setCurPage(TxtPage txtPage) {
        mCurPage = txtPage;
        return this;
    }

    public TextPagerView setTextPageBean(TextPageBean textPageBean) {
        this.mTextPageBean = textPageBean;
        return this;
    }



    public List<TextPageBean> build2() {
        initPaint();
        setUpTextParams(getTextSize());
        return loadPages2(mContent);
    }

    /**
     * 初始化参数
     */
    public void init() {
        initPaint();
        setUpTextParams(getTextSize());
    }
}
