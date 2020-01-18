package com.yuhang.novel.pirate.repository.network.data.pirate.result;

import com.yuhang.novel.pirate.repository.database.entity.SexBooksEntity;

import java.util.List;

public class ChapterSexResult {

    /**
     * code : 200
     * msg : 成功
     * data : [{"bookId":5101,"bookName":"万能连接器","author":"\u2026","description":"万能连接器是由作者创作的玄幻小说，欢迎您来本站阅读万能连接器","lastChapterName":"最近更新>>","bookUrl":"https://www.xlusir.com/80_80835/"},{"bookId":17806,"bookName":"美少女灵灵放学后的地狱24小时","author":"黑白包","description":"    放学后的校园寂静无人，只有归巢的鸟鸣缕缕传来。阳光把纤细的倩影投 在地面上，秀髮飞扬。女孩闪","lastChapterName":"【美少女灵灵放学后的地狱24小时】完","bookUrl":"https://www.xlusir.com/162_162278/"},{"bookId":3845,"bookName":"快穿表妹不是炮灰","author":"\u2026","description":"快穿表妹不是炮灰是由作者创作的历史小说，欢迎您来本站阅读快穿表妹不是炮灰","lastChapterName":"最近更新>>","bookUrl":"https://www.xlusir.com/38_38730/"},{"bookId":762,"bookName":"帝霸","author":"\u2026","description":"帝霸是由作者创作的玄幻小说，欢迎您来本站阅读帝霸","lastChapterName":"最近更新>>","bookUrl":"https://www.xlusir.com/7_7254/"},{"bookId":2142,"bookName":"科技大帝","author":"\u2026","description":"科技大帝是由作者创作的历史小说，欢迎您来本站阅读科技大帝","lastChapterName":"最近更新>>","bookUrl":"https://www.xlusir.com/87_87237/"}]
     */

    private int code;
    private String msg;
    private List<SexBooksEntity> data;

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

    public List<SexBooksEntity> getData() {
        return data;
    }

    public void setData(List<SexBooksEntity> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * bookId : 5101
         * bookName : 万能连接器
         * author : …
         * description : 万能连接器是由作者创作的玄幻小说，欢迎您来本站阅读万能连接器
         * lastChapterName : 最近更新>>
         * bookUrl : https://www.xlusir.com/80_80835/
         */

        private int bookId;
        private String bookName;
        private String author;
        private String description;
        private String lastChapterName;
        private String bookUrl;

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

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLastChapterName() {
            return lastChapterName;
        }

        public void setLastChapterName(String lastChapterName) {
            this.lastChapterName = lastChapterName;
        }

        public String getBookUrl() {
            return bookUrl;
        }

        public void setBookUrl(String bookUrl) {
            this.bookUrl = bookUrl;
        }
    }
}
