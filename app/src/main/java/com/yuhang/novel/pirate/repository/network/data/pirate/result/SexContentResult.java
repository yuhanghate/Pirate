package com.yuhang.novel.pirate.repository.network.data.pirate.result;

public class SexContentResult {

    /**
     * code : 200
     * msg : 成功
     * data : {"contentId":46017,"bookId":768,"chapterId":188542,"bookName":null,"chapterName":"【武林沉沦】（第二十五章）","content":""}
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
         * contentId : 46017
         * bookId : 768
         * chapterId : 188542
         * bookName : null
         * chapterName : 【武林沉沦】（第二十五章）
         * content :
         */

        private int contentId;
        private int bookId;
        private int chapterId;
        private Object bookName;
        private String chapterName;
        private String content;

        public int getContentId() {
            return contentId;
        }

        public void setContentId(int contentId) {
            this.contentId = contentId;
        }

        public int getBookId() {
            return bookId;
        }

        public void setBookId(int bookId) {
            this.bookId = bookId;
        }

        public int getChapterId() {
            return chapterId;
        }

        public void setChapterId(int chapterId) {
            this.chapterId = chapterId;
        }

        public Object getBookName() {
            return bookName;
        }

        public void setBookName(Object bookName) {
            this.bookName = bookName;
        }

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
