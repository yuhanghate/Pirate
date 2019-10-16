//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.yuhang.novel.pirate.repository.network.resouce;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

/**
 * 书本信息
 */
public class BookInfoBean implements Cloneable {

    private String name; //小说名
    private String tag;
    private String noteUrl;  //如果是来源网站,则小说根地址,如果是本地则是小说本地MD5
    private String chapterUrl;  //章节目录地址,本地目录正则
    private long finalRefreshData;  //章节最后更新时间
    private String coverUrl; //小说封面
    private String author;//作者
    private String introduce; //简介
    private String origin; //来源
    private String charset;//编码
    private String bookSourceType;
    private String bookInfoHtml;
    private String chapterListHtml;

    public BookInfoBean() {
    }

    public BookInfoBean(String name, String tag, String noteUrl, String chapterUrl, long finalRefreshData, String coverUrl, String author, String introduce,
                        String origin, String charset, String bookSourceType) {
        this.name = name;
        this.tag = tag;
        this.noteUrl = noteUrl;
        this.chapterUrl = chapterUrl;
        this.finalRefreshData = finalRefreshData;
        this.coverUrl = coverUrl;
        this.author = author;
        this.introduce = introduce;
        this.origin = origin;
        this.charset = charset;
        this.bookSourceType = bookSourceType;
    }

    @Override
    protected Object clone() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return gson.fromJson(json, BookInfoBean.class);
        } catch (Exception ignored) {
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNoteUrl() {
        return noteUrl;
    }

    public void setNoteUrl(String noteUrl) {
        this.noteUrl = noteUrl;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public long getFinalRefreshData() {
        return finalRefreshData;
    }

    public void setFinalRefreshData(long finalRefreshData) {
        this.finalRefreshData = finalRefreshData;
    }


    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getAuthor() {
        return author;
    }


    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }


    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }



    public String getBookSourceType() {
        return this.bookSourceType;
    }

    public void setBookSourceType(String bookSourceType) {
        this.bookSourceType = bookSourceType;
    }

    public String getBookInfoHtml() {
        return bookInfoHtml;
    }

    public void setBookInfoHtml(String bookInfoHtml) {
        this.bookInfoHtml = bookInfoHtml;
    }

    public String getChapterListHtml() {
        return chapterListHtml;
    }

    public void setChapterListHtml(String chapterListHtml) {
        this.chapterListHtml = chapterListHtml;
    }
}