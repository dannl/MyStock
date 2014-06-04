package com.dolphin.browser.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorController implements SensorEventListener {
    public interface OnSensorEventListener {
        public void onShake();
    }

    private static final float DEFAULT_SENSIBILITY = 75;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private OnSensorEventListener mOnSensorEventListener;
    private boolean mIsAlive = false;
    private float mSensibility;
    private boolean isSupportSensor;

    public SensorController(final Context context) {
        //fix bug 35497:some time SENSOR_SERVICE not support
        try {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensibility = DEFAULT_SENSIBILITY;
            isSupportSensor=true;
        } catch (Exception e) {
            mSensorManager=null;
            mSensor=null;
            isSupportSensor=false;
        }
    }

    public void setSensibility(final float sensibility) {
        if(!isSupportSensor){
            return;
        }
        if(sensibility < 0 || sensibility > 100) {
            return;
        }
        mSensibility = sensibility;
    }

    public void stop() {
        if(!isSupportSensor){
            return;
        }
        if(mIsAlive) {
            mIsAlive = false;
            mSensorManager.unregisterListener(this, mSensor);
            mOnSensorEventListener = null;
        }
    }

    public void startDetect(final OnSensorEventListener onSensorEventListener) {
        if(!isSupportSensor){
            return;
        }
        if(!mIsAlive) {
            mOnSensorEventListener = onSensorEventListener;
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
            mIsAlive = true;
        }
    }

    private long mLastGestureTime;
    private final float[] mPrev = new float[3];
    private final float[] mPrevDiff = new float[3];
    private final float[] mDiff = new float[3];
    private final float[] mRevertDiff = new float[3];

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        float[] diff = new float[3];
        for (int i = 0; i < 3; i++) {
            diff[i] = event.values[i] - mPrev[i];
            if ((diff[i] > 1.0 && mDiff[i] < 0.2)
                    || (diff[i] < -1.0 && mDiff[i] > -0.2)) {
                // start track when there is a big move, or revert
                mRevertDiff[i] = mDiff[i];
                mDiff[i] = 0;
            } else if (diff[i] > -0.2 && diff[i] < 0.2) {
                // reset when it is flat
                mDiff[i] = mRevertDiff[i] = 0;
            }
            mDiff[i] += diff[i];
            mPrevDiff[i] = diff[i];
            mPrev[i] = event.values[i];
        }

        long now = android.os.SystemClock.uptimeMillis();
        if (now - mLastGestureTime > 1000) {
            mLastGestureTime = 0;
            float z = mDiff[2];
            float az = Math.abs(z);
            float rz = mRevertDiff[2];
            float arz = Math.abs(rz);

            float y = mDiff[1];
            float ay = Math.abs(y);
            float ry = mRevertDiff[1];
            float ary = Math.abs(ry);

            float x = mDiff[0];
            float ax = Math.abs(x);
            float rx = mRevertDiff[0];
            float arx = Math.abs(rx);

            float axThreshold = (100 - mSensibility) + 2.5f;
            float arxThreshold = (100 - mSensibility) + 1.0f;
            boolean gestX = ax > axThreshold && arx > 1.0f && ax > arx;
            boolean gestY = ay > arxThreshold && ary > 1.0f && ay > ary;
            boolean gestZ = az > arxThreshold && arz > 1.0f && az > arz;


            if (gestX || gestY || gestZ) {
                if(mOnSensorEventListener != null) {
                    mOnSensorEventListener.onShake();
                }
                mLastGestureTime = now;
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
