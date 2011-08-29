package com.cibotechnology.visualization;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class AudioVisualizer extends View {

    public AudioVisualizer(Context context) {
        super(context);
        init();
    }

    public AudioVisualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioVisualizer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        // TODO: setup
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachToAudio();
    }

    @Override
    protected void onDetachedFromWindow() {
        detachFromAudio();
        super.onDetachedFromWindow();
    }

    protected void attachToAudio() {
        // TODO: start listening
    }

    protected void detachFromAudio() {
        // TODO: stop listening
    }

    private short mAudioData[] = null;

    public short[] getAudioData() {
        return mAudioData;
    }

    public void setAudioData(short[] mAudioData) {
        this.mAudioData = mAudioData;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setARGB(255, 255, 0, 0);

        if (null != mAudioData) {
            paint.setARGB(255, 0, 0, 255);
            int audioLevel = 0;
            for (int i = 0; i != 1024; ++i) {
                int waveform = mAudioData[i];
                if (waveform > audioLevel) {
                    audioLevel = waveform;
                }
            }
            audioLevel /= 256;
            float w = this.getWidth();
            float h = this.getHeight();
            canvas.drawRect(0, h - audioLevel, w, h, paint);
        }
    }

}