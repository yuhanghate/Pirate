package com.yuhang.novel.pirate.repository.network.data.pirate.result;

import com.yuhang.novel.pirate.repository.network.data.kanshu.result.BookSearchDataResult;

public class BookSearchKdResult {

    /**
     * code : 200
     * msg : 成功
     * data : {"bookName":"逍遥在电影世界","author":"申宫若愚","cover":"https://appbdimg.cdn.bcebos.com/BookFiles/BookImages/973.jpg","description":"平凡的人得到了不平凡的本领在各个电影世界的到了越来越强大的力量没有争霸天下的的野心只是想逍遥的生活下去有一点装B有一点猥琐力量。。。永生。。。无所谓正邪无所谓对错一切只凭本心的逍遥下去，不争霸，不虐......","lastChapterName":"第二百六十章 大结局","resouceType":"连载","bookKsId":973,"kind":"玄幻奇幻","typeKs":1,"typeKd":1,"bookKdId":"5c767dc09acc232064a04852"}
     */

    private int code;
    private String msg;
    private BooksResult data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BooksResult getData() {
        return data;
    }

    public void setData(BooksResult data) {
        this.data = data;
    }

}
