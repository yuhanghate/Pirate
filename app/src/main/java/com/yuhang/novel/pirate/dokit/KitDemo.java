package com.yuhang.novel.pirate.dokit;

import android.content.Context;

import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.yuhang.novel.pirate.R;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-15:48
 * 描    述：kit demo
 * 修订历史：
 * ================================================
 */
public class KitDemo implements IKit {
    @Override
    public int getCategory() {
        return Category.UI;
    }

    @Override
    public int getName() {
        return R.string.app_name;
    }


    @Override
    public int getIcon() {
        return R.mipmap.ic_launcher;
    }

    @Override
    public void onClick(Context context) {
        DokitIntent dokitIntent = new DokitIntent(DemoDokitView.class);
        dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(dokitIntent);
    }

    @Override
    public void onAppInit(Context context) {

    }
}
