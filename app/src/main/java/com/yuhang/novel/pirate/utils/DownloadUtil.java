/*
 * Copyright (c) 2017 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yuhang.novel.pirate.utils;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.yuhang.novel.pirate.widget.progressLayout.ProgressLayout;

import java.io.File;


public class DownloadUtil {

    public static final String PATH_CHALLENGE_VIDEO = Environment.getExternalStorageDirectory() + "/DownloadFile";

    public static final String URL =
            "https://cdn.llscdn.com/yy/files/tkzpx40x-lls-LLS-5.7-785-20171108-111118.apk";

    public static int calcProgressToView(ProgressLayout progressBar, long offset, long total) {
        final float percent = (float) offset / total;
        Logger.t("progress").i("Progress = "+((int) (percent * 100))+"%");
        progressBar.setCurrentProgress((int) (percent * 100));
        return (int) (percent * 100);
    }


    public static File getParentFile(@NonNull Context context) {
        final File externalSaveDir = context.getExternalCacheDir();
        if (externalSaveDir == null) {
            return context.getCacheDir();
        } else {
            return externalSaveDir;
        }
    }
}
