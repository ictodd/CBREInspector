package com.cbre.tsandford.cbreinspector.misc.camera;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cbre.tsandford.cbreinspector.misc.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Size mCurrentSize;
    private float mDist;

    private CameraSettings mCameraSettings;

    public CameraPreview(Context context, Camera camera, CameraSettings cameraSettings) {
        super(context);
        mCamera = camera;

        mCameraSettings = cameraSettings;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void set_camera_settings(CameraSettings new_settings){
        this.mCameraSettings = new_settings;
    }

    public CameraSettings get_camera_settings() {
        return mCameraSettings;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null)  return;

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        apply_settings();

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    // implement auto focus functionality
    public boolean onTouchEvent(MotionEvent event) {
        if(mCamera != null){

            Camera.Parameters params = mCamera.getParameters();
            int action = event.getAction();

            if(event.getPointerCount() == 1){
                // tap to focus
                handleFocus(event, params);
            } else if(event.getPointerCount() == 2){
                // pinch to zoom
                if (action == MotionEvent.ACTION_POINTER_DOWN) {
                    mDist = getFingerSpacing(event);
                } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                    mCamera.cancelAutoFocus();
                    handleZoom(event, params);
                }
            }
            return true;
        }
        return false;
    }

    // Determine the space between the first two fingers
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    private void handleFocus(MotionEvent event, Camera.Parameters params) {
        mCamera.cancelAutoFocus();
        Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f, 100);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        if(params.getMaxNumFocusAreas() > 0){
            List<Camera.Area> areas = new ArrayList<>();
            areas.add(new Camera.Area(focusRect, 1000));
            params.setFocusAreas(areas);
        }
        try{
            mCamera.cancelAutoFocus();
            mCamera.setParameters(params);
            mCamera.startPreview();
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (!camera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        Camera.Parameters parameters = camera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        if (parameters.getMaxNumFocusAreas() > 0) {
                            parameters.setFocusAreas(null);
                        }
                        camera.setParameters(parameters);
                        camera.startPreview();
                    }
                }
            });

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private Rect calculateTapArea(float x, float y, float coefficient, int focusAreaSize) {
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int left = Utils.Math.Clamp((int) x - areaSize / 2, 0, getWidth() - areaSize);
        int top = Utils.Math.Clamp((int) y - areaSize / 2, 0, getHeight() - areaSize);

        return new Rect(Math.round(left), Math.round(top), Math.round(left + focusAreaSize), Math.round(top + focusAreaSize));
    }



    private void apply_settings(){
        Camera.Parameters params = mCamera.getParameters();
        params.setJpegQuality(mCameraSettings.get_jpeg_quality());
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCurrentSize = getOptimalSize(params.getSupportedPictureSizes(),
                mCameraSettings.get_pic_width(),
                mCameraSettings.get_pic_height());
        params.setPictureSize(mCurrentSize.width, mCurrentSize.height);
        //params.setPreviewSize(optimal_size.width, optimal_size.height);
        mCamera.setParameters(params);
    }

    public double currentRatio(){
        return mCurrentSize.width / mCurrentSize.height;
    }

    private Camera.Size getOptimalSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }

}