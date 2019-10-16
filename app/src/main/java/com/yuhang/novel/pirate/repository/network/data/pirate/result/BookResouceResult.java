package com.yuhang.novel.pirate.repository.network.data.pirate.result;

import java.util.List;

public class BookResouceResult {

    /**
     * code : 200
     * msg : 成功
     * data : {"total":762,"pageSize":20,"pageNum":2}
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
         * total : 762
         * pageSize : 20
         * pageNum : 2
         */

        private int total;
        private int pageSize;
        private int pageNum;
        private List<BookResouceListResult> list;

        public List<BookResouceListResult> getList() {
            return list;
        }

        public void setList(List<BookResouceListResult> list) {
            this.list = list;
        }

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
    }
}
