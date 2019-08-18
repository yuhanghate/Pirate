package com.yuhang.novel.pirate.repository.network.data.pirate.result;

import java.util.List;

public class ReadHistoryResult {

    /**
     * code : 200
     * msg : 成功
     * data : {"total":2,"pageSize":20,"pageNum":1,"list":[{"bookName":"凡人修仙传仙界篇","bookid":"30594","author":"忘语","conver":"https://bookcover.yuewen.com/qdbimg/349573/1010734492/180","resouceType":"KS"},{"bookName":"凡人修仙传","bookid":"23434334","author":"忘语","conver":"https://bookcover.yuewen.com/qdbimg/349573/1010734492/180","resouceType":"KS"}]}
     */

    private int code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * total : 2
         * pageSize : 20
         * pageNum : 1
         * list : [{"bookName":"凡人修仙传仙界篇","bookid":"30594","author":"忘语","conver":"https://bookcover.yuewen.com/qdbimg/349573/1010734492/180","resouceType":"KS"},{"bookName":"凡人修仙传","bookid":"23434334","author":"忘语","conver":"https://bookcover.yuewen.com/qdbimg/349573/1010734492/180","resouceType":"KS"}]
         */

        private int total;
        private int pageSize;
        private int pageNum;
        private List<ReadHistoryDataResult> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public List<ReadHistoryDataResult> getList() {
            return list;
        }

        public void setList(List<ReadHistoryDataResult> list) {
            this.list = list;
        }

    }
}
