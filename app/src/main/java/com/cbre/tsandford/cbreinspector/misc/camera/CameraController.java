package com.cbre.tsandford.cbreinspector.misc.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.widget.FrameLayout;

public class CameraController {

    private static String TAG = "CameraController";

    private Activity mActivity;
    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout mFrame;
    private CameraSettings mSettings;
    private Camera.PictureCallback mPictureCallback;

    public CameraController(Activity activity, CameraSettings cameraSettings, FrameLayout previewWindow){
        this.mActivity = activity;
        this.mSettings = cameraSettings;
        this.mFrame = previewWindow;
    }

    public void setPhotoCallback(Camera.PictureCallback callback){
        this.mPictureCallback = callback;
    }

    public void takePhoto(){
        if(this.mCamera != null &&
                this.mPictureCallback != null){
            this.mCamera.takePicture(null, null, this.mPictureCallback);
        }
    }

    public void releaseCamera(){
        if (this.mCamera != null){
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    private Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
            Log.d(TAG, "Failed to open Camera");
            e.printStackTrace();
        }
        return c;
    }

    public void restartCameraPreview(){
        if(this.mCamera != null){
            this.mCamera.stopPreview();
            this.mCamera.startPreview();
        }
    }

    public void setUpCamera(){
        if(mCamera == null){
            mCamera = getCameraInstance();
            mPreview = new CameraPreview(mActivity, mCamera, mSettings);
            mFrame.addView(mPreview);
        }
    }


}
