package com.cbre.tsandford.cbreinspector.misc.camera;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;

public class SensorController {

    public interface SensorCallback{
        void OnSensorChanged(float lightValue);
        void OnAccuracyChanged(int accuracy);
    }

    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private Activity mActivity;

    private SensorCallback mCallback;

    public SensorController(Activity activity, SensorCallback callback) {
        mActivity = activity;
        mCallback = callback;
        setUpManager();
    }

    private void setUpManager() {
        mSensorManager = (SensorManager) mActivity.getSystemService(SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(mCallback != null)
                    mCallback.OnSensorChanged(event.values[0]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                if(mCallback != null)
                    mCallback.OnAccuracyChanged(accuracy);
            }
        };
        mSensorManager.registerListener(
                listener, mLightSensor, SensorManager.SENSOR_DELAY_UI);
    }


}
