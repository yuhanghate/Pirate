package com.yuhang.novel.pirate.repository.network.data.pirate.result;

import java.util.List;

public class ReadHistoryBookResult {

    /**
     * code : 200
     * msg : 成功
     * data : {"total":2,"pageSize":20,"pageNum":1,"list":[{"bookName":"凡人修仙传仙界篇","bookid":"30594","author":"忘语","conver":"https://bookcover.yuewen.com/qdbimg/349573/1010734492/180","resouceType":"KS"},{"bookName":"凡人修仙传","bookid":"23434334","author":"忘语","conver":"https://bookcover.yuewen.com/qdbimg/349573/1010734492/180","resouceType":"KS"}]}
     */

    private int code;
    private String msg;
    private ReadHistoryDataResult data;

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

    public ReadHistoryDataResult getData() {
        return data;
    }

    public void setData(ReadHistoryDataResult data) {
        this.data = data;
    }

}
