package com.yuhang.novel.pirate.utils;

import android.content.Context;
import android.content.Intent;
import com.yuhang.novel.pirate.ui.launch.activity.LaunchActivity;

/**
 * 此工具类用来重启APP，只是单纯的重启，不做任何处理。
 * Created by 13itch on 2016/8/5.
 */
public class RestartAPPTool {

    /**
     * 重启整个APP
     * @param context
     * @param Delayed 延迟多少毫秒
     */
    public static void restartAPP(Context context, long Delayed){
        Intent intent = new Intent(context, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        // 杀掉进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);


    }
    /***重启整个APP*/
    public static void restartAPP(Context context){
        restartAPP(context,200);
    }
}