//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.yuhang.novel.pirate.repository.network.resouce;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;


public class SearchBookBean   {
    private String noteUrl;
    private String coverUrl;//封面URL
    private String name;
    private String author;
    private String tag;
    private String kind;//分类
    private String origin;//来源
    private String lastChapter;
    private String introduce; //简介
    private String chapterUrl;//目录URL
    private Long addTime = 0L;
    private Long upTime = 0L;
    private String variable;

    private Boolean isCurrentSource = false;
    private int originNum = 1;
    private int lastChapterNum = -2;
    private int searchTime = Integer.MAX_VALUE;
    private LinkedHashSet<String> originUrls;
    private Map<String, String> variableMap;
    private String bookInfoHtml;

    public SearchBookBean() {

    }

    public SearchBookBean(String tag, String origin) {
        this.tag = tag;
        this.origin = origin;
    }

    public SearchBookBean(String noteUrl, String coverUrl, String name, String author, String tag, String kind,
                          String origin, String lastChapter, String introduce, String chapterUrl, Long addTime, Long upTime,
                          String variable) {
        this.noteUrl = noteUrl;
        this.coverUrl = coverUrl;
        this.name = name;
        this.author = author;
        this.tag = tag;
        this.kind = kind;
        this.origin = origin;
        this.lastChapter = lastChapter;
        this.introduce = introduce;
        this.chapterUrl = chapterUrl;
        this.addTime = addTime;
        this.upTime = upTime;
        this.variable = variable;
    }


    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim().replaceAll("　", "") : null;
    }

    public String getAuthor() {
        return author;
    }


    public String getLastChapter() {
        return lastChapter == null ? "" : lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;

    }


    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }


    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Boolean getIsCurrentSource() {
        return this.isCurrentSource;
    }

    public void setIsCurrentSource(Boolean isCurrentSource) {
        this.isCurrentSource = isCurrentSource;
        if (isCurrentSource)
            this.addTime = System.currentTimeMillis();
    }

    public int getOriginNum() {
        return originNum;
    }

    public void addOriginUrl(String origin) {
        if (this.originUrls == null) {
            this.originUrls = new LinkedHashSet<>();
        }
        this.originUrls.add(origin);
        originNum = this.originUrls.size();
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getChapterUrl() {
        return this.chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public long getAddTime() {
        return this.addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }


    public int getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(int searchTime) {
        this.searchTime = searchTime;
    }

    public Long getUpTime() {
        return this.upTime;
    }

    public void setUpTime(Long upTime) {
        this.upTime = upTime;
    }

    public String getBookInfoHtml() {
        return bookInfoHtml;
    }

    public void setBookInfoHtml(String bookInfoHtml) {
        this.bookInfoHtml = bookInfoHtml;
    }

    // 一次性存入搜索书籍节点信息
    public void setSearchInfo(String name, String author, String kind, String lastChapter,
                              String introduce, String coverUrl, String noteUrl) {
        this.name = name;
        this.author = author;
        this.kind = kind;
        this.lastChapter = lastChapter;
        this.introduce = introduce;
        this.coverUrl = coverUrl;
        this.noteUrl = noteUrl;
    }
}
