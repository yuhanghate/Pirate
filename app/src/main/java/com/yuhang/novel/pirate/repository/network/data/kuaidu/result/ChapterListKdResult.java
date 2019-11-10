package com.yuhang.novel.pirate.repository.network.data.kuaidu.result;

import java.util.List;

public class ChapterListKdResult {

    private String book;
    private String chaptersUpdated;
    private String updated;
    private String link;
    private String source;
    private String name;
    private String _id;
    private List<ChapterListDataKdResult> chapters;


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getChaptersUpdated() {
        return chaptersUpdated;
    }

    public void setChaptersUpdated(String chaptersUpdated) {
        this.chaptersUpdated = chaptersUpdated;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public List<ChapterListDataKdResult> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterListDataKdResult> chapters) {
        this.chapters = chapters;
    }

}
