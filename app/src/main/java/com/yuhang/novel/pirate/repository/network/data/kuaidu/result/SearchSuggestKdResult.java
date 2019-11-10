package com.yuhang.novel.pirate.repository.network.data.kuaidu.result;

import com.yuhang.novel.pirate.repository.network.data.pirate.result.SearchSuggestResult;

import java.util.List;

public class SearchSuggestKdResult {

    /**
     * ok : true
     * keywords : [{"text":"凡人修仙传","tag":"bookname","id":"508662b8d7a545903b000027","author":"忘语"},{"text":"凡人修仙传之轮回","tag":"bookname","id":"574ff4201cd40b580e4652b0","author":"年枫"},{"text":"凡人修仙传之仙路慢慢","tag":"bookname","id":"5c763e3a9acc2320649fc375","author":"香菜拌金针菇"},{"text":"凡人修仙传漫画版","tag":"bookname","id":"5c765d339acc2320649ff1ed","author":"默闻勋勋"},{"text":"凡人修仙传","tag":"bookname","id":"5809dae739b033e3528d47e0","author":"童话雨邪"},{"text":"凡人修仙传","tag":"bookname","id":"5b56cc6bce210c6203bcbd62","author":"过往如烟"},{"text":"凡人修仙传之配角传奇","tag":"bookname","id":"55a4eadd6e47d7837958c2da","author":"轻语江湖"},{"text":"凡人修仙传","tag":"bookname","id":"5af5096b584bc655c8bb140d","author":"忧郁的麻花"},{"text":"凡人修仙传奇","tag":"bookname","id":"54abe9e92a6e84ad2a8edc8f","author":"雨落尘世"},{"text":"凡人修仙传之再续凡缘","tag":"bookname","id":"58bec6e7c80305fc5823add9","author":"再续凡缘"}]
     */

    private boolean ok;
    private List<SearchSuggestResult> keywords;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public List<SearchSuggestResult> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<SearchSuggestResult> keywords) {
        this.keywords = keywords;
    }

}
