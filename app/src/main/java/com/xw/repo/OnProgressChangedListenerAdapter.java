package com.xw.repo;

import android.animation.AnimatorListenerAdapter;

/**
 * Listener adapter
 * <br/>
 * usage like {@link AnimatorListenerAdapter}
 */
public abstract class OnProgressChangedListenerAdapter implements OnProgressChangedListener {

    @Override
    public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
    }

    @Override
    public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
    }

    @Override
    public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
    }
}