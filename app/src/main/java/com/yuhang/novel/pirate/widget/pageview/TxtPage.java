package com.yuhang.novel.pirate.widget.pageview;

import java.util.List;

/**
 * Created by newbiechen on 17-7-1.
 */

public class TxtPage {
    int position;
    String title;
    int titleLines; //当前 lines 中为 title 的行数。
    List<String> lines;

    int pageSize;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTitleLines() {
        return titleLines;
    }

    public void setTitleLines(int titleLines) {
        this.titleLines = titleLines;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }
}
