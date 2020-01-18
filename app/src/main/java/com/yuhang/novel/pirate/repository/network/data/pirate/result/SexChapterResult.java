package com.yuhang.novel.pirate.repository.network.data.pirate.result;

import java.util.List;

public class SexChapterResult {

    /**
     * code : 200
     * msg : 成功
     * data : {"total":72,"pageSize":10,"pageNum":1,"list":[{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十章","chapterUrl":"https://m.xlusir.com/165_165444/66.html","chapterIndex":66,"chapterId":46917},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第八十八章","chapterUrl":"https://m.xlusir.com/165_165444/65.html","chapterIndex":65,"chapterId":46901},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第八十六章","chapterUrl":"https://m.xlusir.com/165_165444/63.html","chapterIndex":63,"chapterId":46872},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第八十四章","chapterUrl":"https://m.xlusir.com/165_165444/62.html","chapterIndex":62,"chapterId":46857},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第八十七章","chapterUrl":"https://m.xlusir.com/165_165444/64.html","chapterIndex":64,"chapterId":46886},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十六章","chapterUrl":"https://m.xlusir.com/165_165444/71.html","chapterIndex":71,"chapterId":47004},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十五章","chapterUrl":"https://m.xlusir.com/165_165444/70.html","chapterIndex":70,"chapterId":46986},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十二章","chapterUrl":"https://m.xlusir.com/165_165444/68.html","chapterIndex":68,"chapterId":46952},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十一章","chapterUrl":"https://m.xlusir.com/165_165444/67.html","chapterIndex":67,"chapterId":46934},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十四章","chapterUrl":"https://m.xlusir.com/165_165444/69.html","chapterIndex":69,"chapterId":46969}]}
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
         * total : 72
         * pageSize : 10
         * pageNum : 1
         * list : [{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十章","chapterUrl":"https://m.xlusir.com/165_165444/66.html","chapterIndex":66,"chapterId":46917},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第八十八章","chapterUrl":"https://m.xlusir.com/165_165444/65.html","chapterIndex":65,"chapterId":46901},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第八十六章","chapterUrl":"https://m.xlusir.com/165_165444/63.html","chapterIndex":63,"chapterId":46872},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第八十四章","chapterUrl":"https://m.xlusir.com/165_165444/62.html","chapterIndex":62,"chapterId":46857},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第八十七章","chapterUrl":"https://m.xlusir.com/165_165444/64.html","chapterIndex":64,"chapterId":46886},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十六章","chapterUrl":"https://m.xlusir.com/165_165444/71.html","chapterIndex":71,"chapterId":47004},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十五章","chapterUrl":"https://m.xlusir.com/165_165444/70.html","chapterIndex":70,"chapterId":46986},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十二章","chapterUrl":"https://m.xlusir.com/165_165444/68.html","chapterIndex":68,"chapterId":46952},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十一章","chapterUrl":"https://m.xlusir.com/165_165444/67.html","chapterIndex":67,"chapterId":46934},{"bookId":20,"bookName":"《当同人主角穿回原著》","chapterName":"第九十四章","chapterUrl":"https://m.xlusir.com/165_165444/69.html","chapterIndex":69,"chapterId":46969}]
         */

        private int total;
        private int pageSize;
        private int pageNum;
        private List<ListBean> list;

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

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * bookId : 20
             * bookName : 《当同人主角穿回原著》
             * chapterName : 第九十章
             * chapterUrl : https://m.xlusir.com/165_165444/66.html
             * chapterIndex : 66
             * chapterId : 46917
             */

            private int bookId;
            private String bookName;
            private String chapterName;
            private String chapterUrl;
            private int chapterIndex;
            private int chapterId;

            public int getBookId() {
                return bookId;
            }

            public void setBookId(int bookId) {
                this.bookId = bookId;
            }

            public String getBookName() {
                return bookName;
            }

            public void setBookName(String bookName) {
                this.bookName = bookName;
            }

            public String getChapterName() {
                return chapterName;
            }

            public void setChapterName(String chapterName) {
                this.chapterName = chapterName;
            }

            public String getChapterUrl() {
                return chapterUrl;
            }

            public void setChapterUrl(String chapterUrl) {
                this.chapterUrl = chapterUrl;
            }

            public int getChapterIndex() {
                return chapterIndex;
            }

            public void setChapterIndex(int chapterIndex) {
                this.chapterIndex = chapterIndex;
            }

            public int getChapterId() {
                return chapterId;
            }

            public void setChapterId(int chapterId) {
                this.chapterId = chapterId;
            }
        }
    }
}
