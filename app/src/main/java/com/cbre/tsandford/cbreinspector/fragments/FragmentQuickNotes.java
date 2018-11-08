package com.cbre.tsandford.cbreinspector.fragments;

import android.hardware.Camera;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cbre.tsandford.cbreinspector.AppState;
import com.cbre.tsandford.cbreinspector.R;
import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.cbre.tsandford.cbreinspector.misc.camera.CameraController;
import com.cbre.tsandford.cbreinspector.misc.camera.CameraSettings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FragmentQuickNotes extends DialogFragment {

    public interface CustomDialogListener{
        void OnQuickNotesClose(String quickNotes);
    }

    CustomDialogListener dialogListener;
    CameraController cameraController;
    static String TAG = "FragmentQuickNotes";

    private final double THUMBNAIL_SCALE = 0.2;
    private final int THUMBNAIL_COMPRESSION = 30;

    EditText noteText;
    String preLoadNotesContent;

    public FragmentQuickNotes() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quick_notes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noteText = getView().findViewById(R.id.quick_notes_content);
        noteText.setText(this.preLoadNotesContent);
        FrameLayout cameraFrame = getView().findViewById(R.id.quick_camera_preview);
        cameraController = new CameraController(getActivity(),
                                                new CameraSettings(
                                                        CameraController.DEFAULT_JPEG_QUALITY,
                                                        CameraController.DEFAULT_PIC_WIDTH),
                                                cameraFrame);
        cameraController.setPhotoCallback(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                String raw_photo_filename = AppState.ActiveInspection.pictures.get_new_resource().getPath();

                Utils.MakeFile(raw_photo_filename);
                File pictureFile = new File(raw_photo_filename);

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }

                Utils.Image.ScaleImage(pictureFile, THUMBNAIL_SCALE, THUMBNAIL_COMPRESSION);
                cameraController.restartCameraPreview();
            }
        });

        ImageView imageView = getView().findViewById(R.id.quick_notes_take_photo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraController.takePhoto();
            }
        });
    }

    public void setNotesTextContent(String content){
        this.preLoadNotesContent = content;
    }

    public void setOnCloseDialogListener(CustomDialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraController.setUpCamera();
        cameraController.restartCameraPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraController.releaseCamera();
        if(dialogListener != null){
            dialogListener.OnQuickNotesClose(noteText.getText().toString());
        }
    }


}
