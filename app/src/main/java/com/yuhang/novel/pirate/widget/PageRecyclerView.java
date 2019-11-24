package com.yuhang.novel.pirate.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class PageRecyclerView extends RecyclerView {
    public PageRecyclerView(@NonNull Context context) {
        super(context);
    }

    public PageRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        return super.fling(velocityX / 1000, velocityY / 1000);
//        return false;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
