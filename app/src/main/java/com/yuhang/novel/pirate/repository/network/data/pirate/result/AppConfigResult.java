package com.yuhang.novel.pirate.repository.network.data.pirate.result;

public class AppConfigResult {

    /**
     * code : 200
     * msg : 成功
     * data : {"showGameRecommended":true,"showSexBook":false}
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
         * showGameRecommended : true
         * showSexBook : false
         */

        private boolean showGameRecommended;
        private boolean showSexBook;
        private boolean isOpenVip;

        public boolean isShowGameRecommended() {
            return showGameRecommended;
        }

        public void setShowGameRecommended(boolean showGameRecommended) {
            this.showGameRecommended = showGameRecommended;
        }

        public boolean isShowSexBook() {
            return showSexBook;
        }

        public void setShowSexBook(boolean showSexBook) {
            this.showSexBook = showSexBook;
        }

        public boolean isOpenVip() {
            return isOpenVip;
        }

        public void setOpenVip(boolean openVip) {
            isOpenVip = openVip;
        }
    }
}
