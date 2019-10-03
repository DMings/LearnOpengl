package com.dming.testopengl.utils;

/**
 * ms
 */
public class GLInterpolator {

    private int mTimeSpan;
    private float mMaxValue = 1.0f;
    private float mMinValue = 0.0f;
    private long mLastTime = 0;

    public GLInterpolator(int timeSpan) {
        mTimeSpan = timeSpan;
    }

    public GLInterpolator(int timeSpan, float maxValue) {
        mTimeSpan = timeSpan;
        mMaxValue = maxValue;
    }

    public GLInterpolator(int timeSpan, float maxValue, float minValue) {
        mTimeSpan = timeSpan;
        mMaxValue = maxValue;
        mMinValue = minValue;
    }

    public float getValue() {
        long time = System.currentTimeMillis();
        if (mLastTime == 0) {
            mLastTime = time;
        }
        long diff = time - mLastTime;
        if (diff >= mTimeSpan * 10) {
            mLastTime = time;
        }
        int t = (int) (diff % mTimeSpan);
        return mMinValue + mMaxValue * t / mTimeSpan;
    }

}
