package com.yuhang.novel.pirate.listener;

/**
 * 下载回调
 */
public interface OnDownloadListener {

    void onStart();

    void onProgress(String currentLength);

    void onFinish(String localPath);

    void onFailure();

}
