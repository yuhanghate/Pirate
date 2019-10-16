package com.yuhang.novel.pirate.repository.network.rule;

public class ResouceCore {

    public static void getSearchBook(String url, String body) {
        AnalyzeRule analyzer = new AnalyzeRule(null);
        analyzer.setContent(body, url);
    }
}
