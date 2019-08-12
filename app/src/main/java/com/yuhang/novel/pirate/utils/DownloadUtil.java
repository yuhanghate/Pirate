package com.yuhang.novel.pirate.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.vondear.rxtool.RxFileTool;
import com.yuhang.novel.pirate.app.PirateApp;
import com.yuhang.novel.pirate.listener.OnDownloadListener;
import com.yuhang.novel.pirate.repository.network.NetApi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Description：下载文件工具类
 * Created by kang on 2018/3/9.
 */

public class DownloadUtil {
    private static final String TAG = "DownloadUtil";
    public static final String PATH_CHALLENGE_VIDEO = Environment.getExternalStorageDirectory() + "/DownloadFile";
    //视频下载相关
    protected NetApi mApi;
    private Call<ResponseBody> mCall;
    private File mFile;
    private Thread mThread;
    private String mVideoPath; //下载到本地的视频路径

    public DownloadUtil() {
        if (mApi == null) {
            //初始化网络请求接口
            mApi = PirateApp.Companion.getInstance().getDataRepository().getNetApi();
        }
    }

    public void downloadFile(String url, final OnDownloadListener downloadListener) {
        String name = url;
        //通过Url得到文件并创建本地文件
        if (RxFileTool.createOrExistsDir(PATH_CHALLENGE_VIDEO)) {
            int i = name.lastIndexOf('/');//一定是找最后一个'/'出现的位置
            if (i != -1) {
                name = name.substring(i);
                mVideoPath = PATH_CHALLENGE_VIDEO +
                        name;
            }
        }
        if (TextUtils.isEmpty(mVideoPath)) {
            Log.e(TAG, "downloadVideo: 存储路径为空了");
            return;
        }
        //建立一个文件
        mFile = new File(mVideoPath);
        if (RxFileTool.isFile(mFile) && RxFileTool.createOrExistsFile(mFile)) {
            if (mApi == null) {
                Log.e(TAG, "downloadVideo: 下载接口为空了");
                return;
            }
            mCall = mApi.downloadFile(url);
            mCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                    //下载文件放在子线程
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            //保存到本地
                            writeFile2Disk(response, mFile, downloadListener);
                        }
                    };
                    mThread.start();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        downloadListener.onFailure(); //下载失败
                    });

                }
            });
        } else {
            downloadListener.onFinish(mVideoPath); //下载完成
        }
    }

    //将下载的文件写入本地存储
    private void writeFile2Disk(Response<ResponseBody> response, File file, OnDownloadListener downloadListener) {
        downloadListener.onStart();
        long currentLength = 0;
        OutputStream os = null;

        InputStream is = response.body().byteStream(); //获取下载输入流
        long totalLength = response.body().contentLength();

        try {
            os = new FileOutputStream(file); //输出流
            int len;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                currentLength += len;
                Log.e(TAG, "当前进度: " + currentLength);
                //计算当前下载百分比，并经由回调传出
                long finalCurrentLength = currentLength;
                String percent = myPercent(finalCurrentLength, totalLength);
                new Handler(Looper.getMainLooper()).post(() -> {
                    Logger.t("progress").i("currentLength=" + finalCurrentLength + " totalLength=" + totalLength + " progress=" + percent);
                    downloadListener.onProgress(percent);
                });

                //当百分比为100时下载结束，调用结束回调，并传出下载后的本地路径
                if ((int) (100 * currentLength / totalLength) == 100) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        downloadListener.onFinish(mVideoPath); //下载完成
                    });

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close(); //关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close(); //关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String myPercent(long y, long z) {
        String baifenbi = "";// 接受百分比的值
        double baiy = y * 1.0;
        double baiz = z * 1.0;
        double fen = baiy / baiz;
        // NumberFormat nf = NumberFormat.getPercentInstance(); 注释掉的也是一种方法
        // nf.setMinimumFractionDigits( 2 ); 保留到小数点后几位
        DecimalFormat df1 = new DecimalFormat("##%"); // ##.00%
        // 百分比格式，后面不足2位的用0补齐
        // baifenbi=nf.format(fen);
        baifenbi = df1.format(fen);
        System.out.println(baifenbi);
        return baifenbi;
    }


}
