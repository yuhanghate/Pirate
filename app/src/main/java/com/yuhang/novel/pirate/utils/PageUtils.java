package com.yuhang.novel.pirate.utils;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.yuhang.novel.pirate.app.PirateApp;
import com.yuhang.novel.pirate.constant.BookConstant;
import com.yuhang.novel.pirate.constant.ConfigConstant;
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil;
import com.yuhang.novel.pirate.widget.pageview.TextPageBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PageUtils {

    /**
     * 画标题+内容
     *
     * @param canvas
     */
    public static Canvas drawContent(Canvas canvas, TextPageBean textPageBean, PageBean bean) {

        //画标题
        LinesBean linesBean = textPageBean.getLines().get(0);

        float top = 0;

        LinesBean contentLines;
        if (linesBean.type == LinesType.TITLE) {
            top = drawTitle(canvas, linesBean.lines, bean);
        } else if (linesBean.type == LinesType.SUB_TITLE) {
            //左右滑动增加小标题.上下滑动去掉小标题
            if (PirateApp.Companion.getInstance().getPageType() == 1) {
                top = drawSubTitle(canvas, linesBean.lines, bean);
            } else {
                //上下滑动,除了大标题页面我.都不留边
                top = bean.getContentHeight();
            }
        }else {
            if (PirateApp.Companion.getInstance().getPageType() == 1) {
                //左右滑动,增加头部距离
                top = bean.marginTop + bean.paddingTop;
            } else {
                //上下滑动,除了大标题页面我.都不留边
                top = bean.getContentHeight();
            }
        }

        contentLines = textPageBean.getLines().get(1);
        drawContent(canvas, contentLines.lines, bean, top);
        return canvas;
    }


    /**
     * 画内容
     *
     * @param canvas 画布
     * @param list   内容
     * @param bean   页面工具
     * @return
     */
    private static float drawContent(Canvas canvas, List<String> list, PageBean bean, float top) {
        float start = bean.marginStart + bean.padddingStart;


        float[] measuredWidth = {0};
        bean.textPaint.breakText(BookConstant.WIDTH_MEASURE,
                true, bean.getShowWidth(), measuredWidth);
        float x = (bean.getShowWidth() - measuredWidth[0]) / 2 + start;
        for (String content : list) {
            //进行绘制
            canvas.drawText(content, x, top, bean.textPaint);
            //移动上边距离. 文字高度+行间距
            top += bean.textInterval + bean.getContentHeight();
        }

        return top;
    }

    /**
     * 画小标题
     *
     * @param canvas 画布
     * @param list   所有小标题
     * @param bean   页面工具
     * @return
     */
    private static float drawSubTitle(Canvas canvas, List<String> list, PageBean bean) {
        float top = bean.marginTop + bean.paddingTop + bean.getTipHeight() + bean.tipPare;
        for (String title : list) {

            //上边距.大标题居中显示
            float start = bean.marginStart + bean.padddingStart;
            //进行绘制
            canvas.drawText(title, start, top, bean.tipPaint);
            //移动上边距离. 文字高度+行间距
            top += bean.tipInterval + bean.getTipHeight();
        }

        //最后一行不用增加行间距, 增加段落间距
        top -= bean.tipInterval;
        top += bean.tipPare;

        return top;
    }

    /**
     * 画大标题
     *
     * @param canvas 画面
     * @param list   页面数据
     * @param bean   页面工具
     * @return 标题结束的上边距
     */
    private static float drawTitle(Canvas canvas, List<String> list, PageBean bean) {

        float top = bean.marginTop + bean.paddingTop + bean.getTitleHeight() + bean.titlePare;
        //上边距.大标题居中显示
        float start = bean.marginStart + bean.padddingStart;
        float[] measuredWidth = {0};
        bean.textPaint.breakText(BookConstant.WIDTH_MEASURE,
                true, bean.getShowWidth(), measuredWidth);
        float x = (bean.getShowWidth() - measuredWidth[0]) / 2 + start;

        for (String title : list) {

            //进行绘制
            canvas.drawText(title, x, top, bean.titlePaint);
            //移动上边距离. 文字高度+行间距
            top += bean.titleInterval + bean.getTitleHeight();
        }

        //最后一行不用增加行间距, 增加段落间距
        top -= bean.titleInterval;
        top += bean.titlePare;

        return top;
    }

    /**
     * 章节进行分页.根据宽度和高度计算一页最大显示字数
     *
     * @param br
     * @param bean
     * @return
     * @throws IOException
     */
    public static List<TextPageBean> loadPages(BufferedReader br, PageBean bean) throws IOException {
        //把内容根据手机宽度切割
        List<String> contentLines = new ArrayList<>();
        //章节所有页面
        List<TextPageBean> pages = new ArrayList<>();

        //把流解析成一行行集合
        List<String> originalLines = getOriginalLines(br);
        String paragraph;
        for (String content : originalLines) {
            paragraph = content.replaceAll("\\s", "");
            paragraph = paragraph.trim();
            // 如果只有换行符，那么就不执行
            if (paragraph.equals("")) continue;
            paragraph = StringUtils.halfToFull("  " + paragraph + "\n");
            List<String> lines = getContentLines(paragraph, bean);
            //根据手机宽度进行切割行
            contentLines.addAll(lines);
        }

        //获取大标题所有行对象
        List<String> titleLines = getTitleLines(bean.title, bean);
        //获取小标题所有行对象,横屏才计算
        List<String> tipTitleLines = getTipTitleLines(bean.title, bean);


        //获取第一页对象
        if (bean.hasHorizontal()) {
            TextPageBean titlePage = getTitlePage(titleLines, contentLines, bean);
            pages.add(titlePage);
        } else {
            TextPageBean titlePage = getTitlePageVertical(titleLines, contentLines, bean);
            pages.add(titlePage);
        }
//        Logger.t("空白").i("第一页 contentLines="+contentLines.size());

        //循环获取除一第页以外的页数. 第一页标题大小跟其他页面不一样,另外计算
        while (contentLines.size() > 0) {
            if (bean.hasHorizontal()) {
                //计算左右滑动
                pages.add(getNormalPage(tipTitleLines, contentLines, bean));
            } else {
                //计算上下滑动
                pages.add(getNormalPagVertical(tipTitleLines, contentLines, bean));
            }

        }


        //计算章节所需要的所有页面.每页需要需求第几页
        int maxPage = pages.size();
        for (TextPageBean pageBean : pages) {
            pageBean.setMaxPage(maxPage);
            pageBean.setCurrentPage(pages.indexOf(pageBean));
        }

        return pages;
    }


    /**
     * 返回正常页: 小标题+内容
     * 左右滑动
     *
     * @param subTitleLines
     * @param contentLines
     * @param bean
     * @return
     */
    private static TextPageBean getNormalPage(List<String> subTitleLines, List<String> contentLines, PageBean bean) {
        List<LinesBean> list = new ArrayList<>();

        //获取小标题
        LinesBean subTitleLinesBean = getSubTitleLinesBean(subTitleLines, bean);

        //上下内边距+上下外边距+标题高度+电池显示高度
        float height = subTitleLinesBean.showHeight + bean.getBatteryHeight();

        //获取内容, 去除小标题高度
        LinesBean contentLinesBean = getContentLinesBean(contentLines, bean, bean.getShowHeight() - height);
        list.add(subTitleLinesBean);
        list.add(contentLinesBean);

        TextPageBean textPageBean = new TextPageBean();
        textPageBean.setChapterName(bean.title);
        textPageBean.setCurrentPage(0);
        textPageBean.setLines(list);

        //去掉计算过的内容
        removeContentLines(contentLinesBean, contentLines);

        return textPageBean;
    }

    /**
     * 返回正常页: 内容
     *
     * @param contentLines
     * @param bean
     * @return
     */
    private static TextPageBean getNormalPagVertical(List<String> subTitleLines, List<String> contentLines, PageBean bean) {
        List<LinesBean> list = new ArrayList<>();
        //获取小标题
        LinesBean subTitleLinesBean = getSubTitleLinesBeanVertical(subTitleLines, bean);

        float showHeight = bean.getShowSubHeightVertical();

        //获取内容, 去除小标题高度
        LinesBean contentLinesBean = getContentLinesBeanVertical(contentLines, bean, showHeight);
        list.add(subTitleLinesBean);
        list.add(contentLinesBean);

        TextPageBean textPageBean = new TextPageBean();
        textPageBean.setChapterName(bean.title);
        textPageBean.setCurrentPage(0);
        textPageBean.setLines(list);

        //去掉计算过的内容
        removeContentLines(contentLinesBean, contentLines);

        return textPageBean;
    }

    /**
     * 返回第一页: 大标题+内容
     *
     * @param titleLines
     * @param contentLines
     * @param bean
     * @return
     */
    private static TextPageBean getTitlePage(List<String> titleLines, List<String> contentLines, PageBean bean) {
        List<LinesBean> list = new ArrayList<>();
        //第一页的标题
        LinesBean titleLinesBean = getTitleLinesBean(titleLines, bean);
        //上下内边距+上下外边距+标题高度+电池显示高度
        float height = titleLinesBean.showHeight + bean.getBatteryHeight();

        //第一页的内容
        LinesBean contentLinesBean = getContentLinesBean(contentLines, bean, bean.getShowHeight() - height);
        list.add(titleLinesBean);
        list.add(contentLinesBean);

        TextPageBean textPageBean = new TextPageBean();
        textPageBean.setChapterName(bean.title);
        textPageBean.setCurrentPage(0);
        textPageBean.setLines(list);

        //去掉计算过的内容
        removeContentLines(contentLinesBean, contentLines);

        return textPageBean;
    }


    /**
     * 第一页大标题
     * 上下滑动
     * @param titleLines
     * @param contentLines
     * @param bean
     * @return
     */
    private static TextPageBean getTitlePageVertical(List<String> titleLines, List<String> contentLines, PageBean bean) {
        List<LinesBean> list = new ArrayList<>();
        //第一页的标题
        LinesBean titleLinesBean = getTitleLinesBeanVertical(titleLines, bean);
        //上内边距+上外边距+标题高度
        float height = titleLinesBean.showHeight;

        //第一页的内容
        LinesBean contentLinesBean = getContentLinesBeanVertical(contentLines, bean, bean.getShowTitleHeightVertical()-height);
        list.add(titleLinesBean);
        list.add(contentLinesBean);

        TextPageBean textPageBean = new TextPageBean();
        textPageBean.setChapterName(bean.title);
        textPageBean.setCurrentPage(0);
        textPageBean.setLines(list);

        //去掉计算过的内容
        removeContentLines(contentLinesBean, contentLines);

        return textPageBean;
    }


    /**
     * 把计算好的内容从章节内容中移除
     *
     * @param linesBean
     * @param list
     */
    private static void removeContentLines(LinesBean linesBean, List<String> list) {
        List<String> lines = linesBean.lines;
        for (String content : lines) {
            list.remove(content);
        }
    }


    /**
     * 计算一页可以显示多少条内容
     *
     * @param list      章节内容
     * @param bean      配置
     * @param maxHeight 最大显示高度
     * @return
     */
    private static LinesBean getContentLinesBean(List<String> list, PageBean bean, float maxHeight) {
        LinesBean linesBean = new LinesBean();
        linesBean.height = maxHeight;
        linesBean.type = LinesType.CONTENT;

        //增加开始段落间距
        linesBean.showHeight = bean.textPare;

        for (String content : list) {
            //每行标题需要的高度: 标题高度+行间距
            float lineHeight = bean.getContentHeight() + bean.textInterval;

            //当前显示
            if (linesBean.showHeight + lineHeight <= linesBean.height) {
                linesBean.showHeight += lineHeight;
                linesBean.lines.add(content);
            } else {
                break;
            }
        }

        //增加结束段落间距
        linesBean.showHeight += bean.textPare;

        //for循环都会在每一行下面加行间距,把最后一行多余的行间距去掉
        linesBean.showHeight -= bean.textInterval;

        return linesBean;
    }


    /**
     * 计算一页可以显示多少条内容
     *
     * @param list      章节内容
     * @param bean      配置
     * @param maxHeight 最大显示高度
     * @return
     */
    private static LinesBean getContentLinesBeanVertical(List<String> list, PageBean bean, float maxHeight) {
        LinesBean linesBean = new LinesBean();
        linesBean.height = maxHeight;
        linesBean.type = LinesType.CONTENT;

        //增加开始段落间距
//        linesBean.showHeight = bean.textPare;

        for (String content : list) {
            //每行标题需要的高度: 标题高度+行间距
            float lineHeight = bean.getContentHeight() + bean.textInterval;

            //当前显示
            if (linesBean.showHeight + lineHeight <= linesBean.height) {
                linesBean.showHeight += lineHeight;
                linesBean.lines.add(content);
            } else {
                break;
            }
        }

        //增加结束段落间距
//        linesBean.showHeight += bean.textPare;

        //for循环都会在每一行下面加行间距,把最后一行多余的行间距去掉
        linesBean.showHeight -= bean.textInterval;

        return linesBean;
    }
    /**
     * 小标题进行分行对象
     *
     * @param list
     * @param bean
     * @return
     */
    private static LinesBean getSubTitleLinesBean(List<String> list, PageBean bean) {
        LinesBean linesBean = new LinesBean();
        linesBean.height = bean.getShowHeight();
        linesBean.type = LinesType.SUB_TITLE;

        //增加开始段落间距
        linesBean.showHeight = bean.tipPare;
        for (String subTitle : list) {
            //每行标题需要的高度: 标题高度+行间距
            float lineHeight = bean.getTipHeight() + bean.tipInterval;

            //可显示高度不能超过指定的高度
            if (linesBean.showHeight + lineHeight <= bean.height) {
                linesBean.showHeight = lineHeight;
                linesBean.lines.add(subTitle);
            } else {
                break;
            }
        }

        //增加结束段落间距
        linesBean.showHeight += bean.tipPare;

        //for循环都会在每一行下面加行间距,把最后一行多余的行间距去掉
        linesBean.showHeight -= bean.tipInterval;

        return linesBean;
    }

    /**
     * 小标题进行分行对象
     *
     * @param list
     * @param bean
     * @return
     */
    private static LinesBean getSubTitleLinesBeanVertical(List<String> list, PageBean bean) {
        LinesBean linesBean = new LinesBean();
        linesBean.height = bean.getShowSubHeightVertical();
        linesBean.type = LinesType.SUB_TITLE;

        //增加开始段落间距
        linesBean.showHeight = bean.tipPare;
        for (String subTitle : list) {
            //每行标题需要的高度: 标题高度+行间距
            float lineHeight = bean.getTipHeight() + bean.tipInterval;

            //可显示高度不能超过指定的高度
            if (linesBean.showHeight + lineHeight <= bean.height) {
                linesBean.showHeight = lineHeight;
                linesBean.lines.add(subTitle);
            } else {
                break;
            }
        }

        //增加结束段落间距
        linesBean.showHeight += bean.tipPare;

        //for循环都会在每一行下面加行间距,把最后一行多余的行间距去掉
        linesBean.showHeight -= bean.tipInterval;

        return linesBean;
    }

    /**
     * 大标题进行对象
     *
     * @param list
     * @param bean
     * @return
     */
    private static LinesBean getTitleLinesBean(List<String> list, PageBean bean) {

        LinesBean linesBean = new LinesBean();
        linesBean.height = bean.getShowHeight();
        linesBean.type = LinesType.TITLE;

        //增加开始段落间距
        linesBean.showHeight = bean.titlePare;
        for (String title : list) {
            //每行标题需要的高度: 标题高度+行间距
            float lineHeight = bean.getTitleHeight() + bean.titleInterval;

            //可显示高度不能超过指定的高度
            if (linesBean.showHeight + lineHeight <= bean.height) {
                linesBean.showHeight += lineHeight;
                linesBean.lines.add(title);
            } else {
                break;
            }
        }

        //增加结束段落间距
        linesBean.showHeight += bean.titlePare;

        //for循环都会在每一行下面加行间距,把最后一行多余的行间距去掉
        linesBean.showHeight -= bean.titleInterval;

        return linesBean;
    }

    /**
     * 小标题进行对象
     * @param list
     * @param bean
     * @return
     */
    private static LinesBean getTitleLinesBeanVertical(List<String> list, PageBean bean) {

        LinesBean linesBean = new LinesBean();
        linesBean.height = bean.getShowTitleHeightVertical();
        linesBean.type = LinesType.TITLE;

        //增加开始段落间距
        linesBean.showHeight = bean.titlePare;
        for (String title : list) {
            //每行标题需要的高度: 标题高度+行间距
            float lineHeight = bean.getTitleHeight() + bean.titleInterval;

            //可显示高度不能超过指定的高度
            if (linesBean.showHeight + lineHeight <= bean.height) {
                linesBean.showHeight += lineHeight;
                linesBean.lines.add(title);
            } else {
                break;
            }
        }

        //增加结束段落间距
        linesBean.showHeight += bean.titlePare;

        //for循环都会在每一行下面加行间距,把最后一行多余的行间距去掉
        linesBean.showHeight -= bean.titleInterval;

        return linesBean;
    }

    /**
     * 读取每行数据放到集合
     *
     * @param br
     * @return
     */
    private static List<String> getOriginalLines(BufferedReader br) throws IOException {
        //每一行原始数据
        List<String> lines = new ArrayList<>();
        //读取一行
        String paragraph = "";
        while ((paragraph = br.readLine()) != null) {

            //转换字体(简体,繁体等)
            paragraph = StringUtils.convertCC(paragraph, PirateApp.Companion.getInstance());

            paragraph = StringUtils.halfToFull(paragraph);
            lines.add(paragraph);
        }
        return lines;
    }

    /**
     * 每条内容进行分行
     *
     * @param content
     * @param bean
     * @return
     */
    private static List<String> getContentLines(String content, PageBean bean) {
        List<String> lines = new ArrayList<>();
        float[] measuredWidth = {0};

//        Logger.t("空白").i(" content="+content);
        //读取一行
        while (content.length() > 0) {

            //一行显示的字数
            int count = bean.textPaint.breakText(content,
                    true, bean.getShowWidth(), measuredWidth);
            String subTitle = content.substring(0, count);
            //过滤无效字符
            if (subTitle.equals("\n") || subTitle.equals("\n\r") || subTitle.equals("\r\n") || subTitle.trim().equals("")) {
                return lines;
            }

            lines.add(subTitle);

            content = content.substring(subTitle.length());
        }

        return lines;
    }

    /**
     * 大标题分行
     *
     * @param title
     * @param bean
     * @return
     */
    private static List<String> getTitleLines(String title, PageBean bean) {
        List<String> lines = new ArrayList<>();
        float[] measuredWidth = {0};


        while (title.length() > 0) {
            //一行显示的字数
            int count = bean.titlePaint.breakText(title,
                    true, bean.getShowWidth(), measuredWidth);
            String subTitle = title.substring(0, count);
            //过滤无效字符
            if (subTitle.equals("\n") || subTitle.equals("\n\r") || subTitle.equals("\r\n") || subTitle.trim().equals("")) {
                return lines;
            }

            lines.add(subTitle);

            title = title.replace(subTitle, "");
        }


        return lines;
    }

    /**
     * 小标题进行分行
     *
     * @param title
     * @param bean
     * @return
     */
    private static List<String> getTipTitleLines(String title, PageBean bean) {
        List<String> lines = new ArrayList<>();
        float[] measuredWidth = {0};


        while (title.length() > 0) {
            //一行显示的字数
            int count = bean.tipPaint.breakText(title,
                    true, bean.getShowWidth(), measuredWidth);
            String subTitle = title.substring(0, count);
            //过滤无效字符
            if (subTitle.equals("\n") || subTitle.equals("\n\r") || subTitle.equals("\r\n") || subTitle.trim().equals("")) {
                return lines;
            }

            lines.add(subTitle);

            title = title.replace(subTitle, "");
        }


        return lines;
    }

    /**
     * 对象
     */
    public static class PageBean {

        public Paint titlePaint;//大标题画笔
        public Paint textPaint;//内容画笔
        public Paint tipPaint;//电池和小标题画笔

        public float tipPare;//小标题段落间距
        public float tipInterval;//小标题行间距

        public float titlePare;//大标题段落间距
        public float titleInterval;//大标题行间距

        public float textPare;//内容段落间距
        public float textInterval;//内容行间距

        public float paddingTop;
        public float paddingButtom;
        public float padddingStart;
        public float padddingEnd;

        public float marginTop;
        public float marginButtom;
        public float marginStart;
        public float marginEnd;

        public float width;//显示宽度
        public float height;//显示高度

        public String title;//标题
        public int pageType;//上下翻页/左右翻页

        /**
         * 获取大标题高度
         *
         * @return
         */
        public float getTitleHeight() {
            return titlePaint.getFontSpacing();
        }

        /**
         * 获取小标题高度
         *
         * @return
         */
        public float getTipHeight() {
            return tipPaint.getFontSpacing();
        }

        /**
         * 获取内容高度
         *
         * @return
         */
        public float getContentHeight() {
            return textPaint.getFontSpacing();
        }

        /**
         * 电池高度
         *
         * @return
         */
        public float getBatteryHeight() {
            return tipPaint.getFontSpacing();
        }

        /**
         * 获取显示宽度
         *
         * @return
         */
        public float getShowWidth() {
            return width - marginStart - marginEnd - padddingStart - padddingEnd;
        }

        /**
         * 获取显示的高度
         *
         * @return
         */
        public float getShowHeight() {
            return height - marginTop - marginButtom - paddingTop - paddingButtom;
        }

        /**
         * 大标题显示高度
         * 上下滑动
         */

        public float getShowTitleHeightVertical() {
            return height - marginTop -paddingTop;
        }

        /**
         * 小标题显示高度
         * 上下滑动
         * @return
         */
        public float getShowSubHeightVertical() {
            return height;
        }

        /**
         * 是否左右滑动
         * @return
         */
        public boolean hasHorizontal() {
            return PirateApp.Companion.getInstance().getPageType() == ConfigConstant.INSTANCE.getPAGE_TYPE_HORIZONTAL();
        }
    }

    /**
     * 标题计算对象
     */
    public static class LinesBean {
        //当前页可以容纳的标题的行数
        List<String> lines = new ArrayList<>();

        /**
         * 可用的显示高度
         */
        public float height;

        /**
         * 已经显示的高度
         */
        public float showHeight;

        /**
         * 页面的3个元素类型
         */
        public LinesType type;
    }

    enum LinesType {
        /**
         * 大标题
         */
        TITLE,

        /**
         * 小标题
         */
        SUB_TITLE,

        /**
         * 内容
         */
        CONTENT
    }
}
