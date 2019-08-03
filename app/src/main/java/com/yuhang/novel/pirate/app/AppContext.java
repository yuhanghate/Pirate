package com.yuhang.novel.pirate.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.yuhang.novel.pirate.BuildConfig;

//参数设置
public class AppContext extends BlockCanaryContext {
    private static final String TAG = "AppContext";

    @Override
    public String provideQualifier() {
        String qualifier = "";
        try {
            PackageInfo info = PirateApp.Companion.getInstance().getPackageManager()
                    .getPackageInfo(PirateApp.Companion.getInstance().getPackageName(), 0);
            qualifier += info.versionCode + "_" + info.versionName + "_YYB";
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "provideQualifier exception", e);
        }
        return qualifier;
    }

    @Override
    public int provideBlockThreshold() {
        return 500;
    }

    @Override
    public boolean displayNotification() {
        return BuildConfig.DEBUG;
    }

    @Override
    public boolean stopWhenDebugging() {
        return false;
    }
}