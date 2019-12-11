package com.yuhang.novel.pirate.repository.network.data.pirate.result;

import java.util.List;

public class GameRecommentResult {

    /**
     * code : 200
     * msg : 成功
     * data : {"total":1,"pageSize":20,"pageNum":1,"list":[{"name":"战国幻武","image":"https://img.tapimg.com/market/lcs/03f1fd1285ac27142b6322cc0cb7ef15_360.png","gameType":"策略类游戏","size":509000179,"description":"体验不一样的战场","packageName":"com.Otaku.dazhanguowuyu","downloadUrl":"http://gncn.comicocn.com/otaku_2019_1202_1837_V7.24.apk"}]}
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
         * total : 1
         * pageSize : 20
         * pageNum : 1
         * list : [{"name":"战国幻武","image":"https://img.tapimg.com/market/lcs/03f1fd1285ac27142b6322cc0cb7ef15_360.png","gameType":"策略类游戏","size":509000179,"description":"体验不一样的战场","packageName":"com.Otaku.dazhanguowuyu","downloadUrl":"http://gncn.comicocn.com/otaku_2019_1202_1837_V7.24.apk"}]
         */

        private int total;
        private int pageSize;
        private int pageNum;
        private List<GameDataResult> list;

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

        public List<GameDataResult> getList() {
            return list;
        }

        public void setList(List<GameDataResult> list) {
            this.list = list;
        }

    }
}
