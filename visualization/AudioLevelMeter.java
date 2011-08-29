package com.cibotechnology.visualization;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class AudioLevelMeter extends DrawingSurface {

    public static class MeterMath {
        static double getDbToAmp(double inDb) {
            return Math.pow(10.0, 0.05 * inDb);
        }

        static float clamp(float min, float x, float max) {
            return (x < min ? min : (x > max ? max : x));
        }
    }

    public class MeterTable {
        private final int TABLE_SIZE = 400;
        private final float ROOT = 2.0f;
        private final float MIN_DECIBELS = -80.0f; // MeterTable Min Decibels
                                                   // must be negative
        private final float mScaleFactor;
        private final ArrayList<Float> mTable;
        private float mMinDecibels;
        private final float mDecibelResolution;

        public MeterTable() {
            mTable = new ArrayList<Float>();
            mDecibelResolution = (mMinDecibels / (TABLE_SIZE - 1));
            mScaleFactor = 1.0f / mDecibelResolution;

            double minAmp = MeterMath.getDbToAmp(MIN_DECIBELS);
            double ampRange = 1. - minAmp;
            double invAmpRange = 1. / ampRange;

            double rroot = 1. / ROOT;
            for (int i = 0; i < TABLE_SIZE; ++i) {
                double decibels = i * mDecibelResolution;
                double amp = MeterMath.getDbToAmp(decibels);
                double adjAmp = (amp - minAmp) * invAmpRange;
                mTable.add((float) Math.pow(adjAmp, rroot));
            }
        }

        public Float ValueAt(float inDecibels) {
            if (inDecibels < MIN_DECIBELS)
                return 0.0f;
            if (inDecibels >= 0.0f)
                return 1.0f;
            int index = (int) (inDecibels * mScaleFactor);
            return mTable.get(index);
        }
    }

    public class LevelMeterColorThreshold {
        public float maxValue;
        public Paint color;
    }

    private float _peakLevel;

    public float getPeakLevel() {
        return _peakLevel;
    }

    public void setPeakLevel(float _peakLevel) {
        this._peakLevel = _peakLevel;
    }

    private float _level = 0.0f;

    public float getLevel() {
        return _level;
    }

    public void setLevel(float _level) {
        this._level = _level;
    }

    private int _numLights = 0;

    public int getNumLights() {
        return _numLights;
    }

    public void setNumLights(int _numLights) {
        this._numLights = _numLights;
    }

    private Paint _bgPaint = new Paint();

    public Paint getBgPaint() {
        return _bgPaint;
    }

    public void setBgPaint(Paint _bgPaint) {
        this._bgPaint = _bgPaint;
    }

    private Paint _borderPaint = new Paint();

    public Paint getBorderPaint() {
        return _borderPaint;
    }

    public void setBorderPaint(Paint _borderPaint) {
        this._borderPaint = _borderPaint;
    }

    private boolean _variableLightIntensity = true;

    public boolean hasVariableLightIntensity() {
        return _variableLightIntensity;
    }

    public void setVariableLightIntensity(boolean _variableLightIntensity) {
        this._variableLightIntensity = _variableLightIntensity;
    }

    private short[] _audioData = null;

    public short[] getAudioData() {
        return _audioData;
    }

    public void setAudioData(short[] audioData) {
        this._audioData = audioData;
    }

    private final int _numColorThresholds = 100;
    private final ArrayList<LevelMeterColorThreshold> _colorThresholds = new ArrayList<LevelMeterColorThreshold>();
    private boolean isVertical = true;

    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public AudioLevelMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        _bgPaint.setARGB(153, 0, 0, 0);
        _borderPaint.setARGB(255, 0, 0, 0);

        for (int i = 0; i != _numColorThresholds; ++i) {
            LevelMeterColorThreshold lmct = new LevelMeterColorThreshold();
            float max = i * (1.0f / _numColorThresholds);
            lmct.maxValue = max;
            float stepping = 0xff * max;
            Paint levelPaint = new Paint();
            int r = (int) (255.0f * (stepping * .001f));
            int g = (int) (255.0f * (stepping * .002f));
            int b = (int) (255.0f * (stepping * .003f));
            levelPaint.setARGB(255, r, g, b);
            lmct.color = levelPaint;
            _colorThresholds.add(lmct);
        }
    }

    @Override
    public void doCustomDrawing(Canvas canvas) {
        if (getNumLights() == 0) {
            drawNoLights(canvas);
        } else {
            drawLights(canvas);
        }
    }

    private void drawNoLights(Canvas canvas) {
        float currentTop = 0.0f;
        int w = this.getWidth();
        int h = this.getHeight();

        for (int i = 0; i < _numColorThresholds; i++) {
            LevelMeterColorThreshold thisThresh = _colorThresholds.get(i);

            float val = Math.min(thisThresh.maxValue, _level);

            RectF rect = new RectF();
            rect.set(0, h * currentTop, w, h * (val - currentTop));
            canvas.drawRect(rect, thisThresh.color);

            if (_level < thisThresh.maxValue)
                break;

            currentTop = val;
        }
    }

    private void drawLights(Canvas canvas) {
        float viewHeight = this.getHeight();
        float viewWidth = this.getWidth();
        int light_i;
        float lightMinVal = 0.0f;
        float insetAmount;
        float lightVSpace = viewHeight / _numLights;

        if (lightVSpace < 4.) {
            insetAmount = 0.0f;
        } else if (lightVSpace < 8.) {
            insetAmount = 0.5f;
        } else {
            insetAmount = 1.0f;
        }

        int peakLight = -1;
        if (_peakLevel > 0.) {
            peakLight = (int) (getPeakLevel() * getNumLights());
            if (peakLight >= _numLights)
                peakLight = _numLights - 1;
        }

        for (light_i = 0; light_i < _numLights; light_i++) {
            float lightMaxVal = (float) (light_i + 1) / (float) _numLights;
            float lightIntensity;

            if (light_i == peakLight) {
                lightIntensity = 1.0f;
            } else {
                lightIntensity = (_level - lightMinVal) / (lightMaxVal - lightMinVal);
                lightIntensity = MeterMath.clamp(0.0f, lightIntensity, 1.0f);
                if ((!_variableLightIntensity) && (lightIntensity > 0.))
                    lightIntensity = 1.0f;
            }

            Paint lightColor = _colorThresholds.get(0).color;
            int color_i;
            for (color_i = 0; color_i < (_numColorThresholds - 1); color_i++) {
                LevelMeterColorThreshold thisThresh = _colorThresholds.get(color_i);
                LevelMeterColorThreshold nextThresh = _colorThresholds.get(color_i + 1);
                if (thisThresh.maxValue <= lightMaxVal) {
                    lightColor = nextThresh.color;
                }
            }

            RectF lightRect = new RectF();
            float left = 0.0f;
            float top = viewHeight * ((float) (light_i) / (float) _numLights);
            float right = viewWidth;
            float bottom = viewHeight * (1.0f / _numLights);
            lightRect.set(left + insetAmount, top + insetAmount, right - insetAmount, bottom - insetAmount);

            float lightAlpha = ((lightColor.getAlpha()) / 255.0f) * lightIntensity;
            if ((lightIntensity < 1.) && (lightIntensity > 0.) && (lightAlpha > .8)) {
                lightAlpha = 0.8f;
            }
            lightColor.setAlpha((int) (lightAlpha * 255.0f));
            canvas.drawRect(lightRect, lightColor);

            lightMinVal = lightMaxVal;
        }

    }
}
