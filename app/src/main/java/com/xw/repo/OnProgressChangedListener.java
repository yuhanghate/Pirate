package com.xw.repo;

public interface OnProgressChangedListener {

    void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser);

    void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat);

    void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser);
}
