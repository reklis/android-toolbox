package com.cibotechnology.visualization;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = DrawingSurface.class.toString();

    public class CanvasThread extends Thread {
        private final SurfaceHolder _surfaceHolder;
        private final DrawingSurface _panel;
        private boolean _run = false;

        public CanvasThread(SurfaceHolder surfaceHolder, DrawingSurface panel) {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
        }

        public void setRunning(boolean run) {
            _run = run;
        }

        @Override
        public void run() {
            Canvas c;
            while (_run) {
                c = null;
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        _panel.onDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

    protected CanvasThread mCanvasThread;

    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        mCanvasThread = new DrawingSurface.CanvasThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed");
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw");
        doCustomDrawing(canvas);
    }

    public abstract void doCustomDrawing(Canvas canvas);
}
