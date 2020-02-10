package com.yuhang.novel.pirate.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.widget.AppCompatTextView;

import com.yuhang.novel.pirate.constant.BookConstant;
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil;

public class ReadBookTextView extends AppCompatTextView {

    // 用于记录点击事件
    private int mDownMotionX, mDownMotionY;
    private long mDownMotionTime;

    //下一页
    private OnClickNextListener mOnClickNextListener;
    //上一页
    private OnClickPreviousListener mOnClickPreviousListener;
    //显示设置
    private OnClickCenterListener mOnClickCenterListener;

    private int mPosition;

    /**
     * 是否全屏翻下页
     */
    private boolean isNextPage = PreferenceUtil.getBoolean(BookConstant.CLICK_NEXT_PAGE, false);

    public ReadBookTextView(Context context) {
        super(context);
    }

    public ReadBookTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReadBookTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownMotionX = (int) event.getX();
                mDownMotionY = (int) event.getY();
                mDownMotionTime = System.currentTimeMillis();
                return true;

            case MotionEvent.ACTION_UP:
                return computeTapMotion(event);
        }

        return super.onTouchEvent(event);
    }

    private boolean computeTapMotion(MotionEvent event) {

        int xDiff = Math.abs((int) event.getX() - mDownMotionX);
        int yDiff = Math.abs((int) event.getY() - mDownMotionY);
        long timeDiff = System.currentTimeMillis() - mDownMotionTime;

        if (xDiff < 5 && yDiff < 5 && timeDiff < 200) {

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeigh = getResources().getDisplayMetrics().heightPixels;
            int x = (int) event.getX();
            float y = (int) event.getY();

            if (x > screenWidth / 4 && x < screenWidth - screenWidth / 6 * 2 && y > screenHeigh / 6 * 2 && y < screenHeigh - screenHeigh / 6 * 2) {
                //点击中心点
                onClickCenter();
                return true;
            } else {
                if (x > screenWidth / 2) {
                    //点击下一页
                    onClickNext();
                    return true;
                } else if (x <= screenWidth / 2) {

                    //设置全屏翻下页
                    if (isNextPage) {
                        //点击下一页
                        onClickNext();
                        return true;
                    }
                    //点击上一页
                    onClickPrevious();
                    return true;
                }
            }
        }
        return false;
    }

    private void onClickNext() {
        if (getOnClickNextListener() != null) {
            getOnClickNextListener().onClickNextListener(this, getPosition());
        }
    }

    private void onClickPrevious() {
        if (getOnClickPreviousListener() != null) {
            getOnClickPreviousListener().onClickPreviousListener(this, getPosition());
        }
    }

    private void onClickCenter() {
        if (getOnClickCenterListener() != null) {
            getOnClickCenterListener().onClickCenterListener(this, getPosition());
        }
    }

    public OnClickNextListener getOnClickNextListener() {
        return mOnClickNextListener;
    }

    public void setOnClickNextListener(OnClickNextListener mOnClickNextListener) {
        this.mOnClickNextListener = mOnClickNextListener;
    }

    public OnClickPreviousListener getOnClickPreviousListener() {
        return mOnClickPreviousListener;
    }

    public void setOnClickPreviousListener(OnClickPreviousListener mOnClickPreviousListener) {
        this.mOnClickPreviousListener = mOnClickPreviousListener;
    }

    public OnClickCenterListener getOnClickCenterListener() {
        return mOnClickCenterListener;
    }

    public void setOnClickCenterListener(OnClickCenterListener mOnClickCenterListener) {
        this.mOnClickCenterListener = mOnClickCenterListener;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    /**
     * 点击界面右边部分
     * 下一页
     */
    public interface OnClickNextListener {
        void onClickNextListener(View view, int position);
    }

    /**
     * 点击界面左边部分
     * 上一页
     */
    public interface OnClickPreviousListener {
        void onClickPreviousListener(View view, int position);
    }

    /**
     * 点击中心点
     * 打开设置
     */
    public interface OnClickCenterListener {
        void onClickCenterListener(View view, int position);
    }
}
