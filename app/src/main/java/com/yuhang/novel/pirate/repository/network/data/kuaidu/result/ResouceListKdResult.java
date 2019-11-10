package com.yuhang.novel.pirate.repository.network.data.kuaidu.result;

public class ResouceListKdResult {

    /**
     * _id : 577b47b4bd86a4bd3f8bf202
     * chaptersCount : 2629
     * lastChapter : 忘语新书《玄界之门》
     * link : https://www.qu.la/book/401/
     * source : 笔趣阁
     * name : 笔趣阁
     * book : 508662b8d7a545903b000027
     * updated : 2018-06-29T17:31:03.045Z
     * host : 笔趣阁
     */

    private String _id;
    private int chaptersCount;
    private String lastChapter;
    private String link;
    private String source;
    private String name;
    private String book;
    private String updated;
    private String host;

    private String bookid;
    private String bookName;

    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getChaptersCount() {
        return chaptersCount;
    }

    public void setChaptersCount(int chaptersCount) {
        this.chaptersCount = chaptersCount;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

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

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
