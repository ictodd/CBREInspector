package com.cbre.tsandford.cbreinspector.fragments;

import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cbre.tsandford.cbreinspector.AppState;
import com.cbre.tsandford.cbreinspector.R;
import com.cbre.tsandford.cbreinspector.misc.CameraPreview;
import com.cbre.tsandford.cbreinspector.misc.CameraSettings;
import com.cbre.tsandford.cbreinspector.misc.ImageCompressor;
import com.cbre.tsandford.cbreinspector.misc.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FragmentCamera extends Fragment {

    private static final String TAG = "FragmentCamera";

    private Button btnCamera;
    private ImageCompressor compressor;

    private LinearLayout gallery_view;

    private ImageView activeGalleryImage;

    private Camera camera;
    private CameraPreview cameraPreview;
    private FrameLayout previewWindow;
    private Camera.PictureCallback photo_callback;

    private static final int JPEG_QUALITY = 50;
    private static final int PIC_WIDTH = 4032; // this stays static, height calc'd based on current settings
    private CameraSettings cameraSettings; // for height and width


    public FragmentCamera() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btnCamera = getActivity().findViewById(R.id.btn_camera);
        this.gallery_view = getActivity().findViewById(R.id.camera_gallery);

        btnCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, photo_callback);
            }
        });

        reload_pics();
    }

    @Override
    public void onPause() {
        super.onPause();
        release_camera();
    }

    @Override
    public void onResume() {
        super.onResume();
        set_up_camera();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // make sure i haven't accidentally registered another view for this menu...
        if(!(v instanceof android.widget.ImageView)) return;
        activeGalleryImage = (ImageView)v;
        menu.add(Menu.NONE, 0,0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // only using a delete option right now...
        String image_file_path = activeGalleryImage.getTag().toString();
        new File(image_file_path).delete();
        reload_pics();
        return true;
    }

    // region Image and Gallery Functions

    private void clear_gallery(){
        this.gallery_view.removeAllViews();
    }

    private void reload_pics(){
        clear_gallery();
        List<Uri> pics = AppState.ActiveInspection.pictures.get_all_items(5);
        for(Uri pic : pics){
            if(!gallery_contains_pic(pic.getPath()))
                add_image_to_gallery(pic);
        }
    }

    private boolean gallery_contains_pic(String path){
        int number_of_children = this.gallery_view.getChildCount();
        for(int i = 0; i < number_of_children; i ++){
            if(gallery_view.getChildAt(i).getTag().toString() == path)
                return true;
        }
        return false;
    }

    private void add_image_to_gallery(Uri image_uri){

        LinearLayout.LayoutParams view_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        view_params.bottomMargin = 5;
        view_params.leftMargin = 5;
        view_params.rightMargin = 5;
        view_params.topMargin = 5;

        ImageView new_img_view = new ImageView(this.getActivity());
        new_img_view.setImageURI(image_uri);
        new_img_view.setAdjustViewBounds(true);
        new_img_view.setTag(image_uri.getPath());

        this.registerForContextMenu(new_img_view);

        Drawable background = getActivity().getDrawable(R.drawable.full_border_thin);
        new_img_view.setBackground(background);

        this.gallery_view.addView(new_img_view, view_params);
    }

    // endregion

    // region Camera Controls

    private void restart_camera_preview(){
        camera.stopPreview();
        camera.startPreview();
    }

    private Camera get_camera_instance(){
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

    private void set_up_camera(){
        if(camera == null) {

            camera = get_camera_instance();
            cameraPreview = new CameraPreview(getActivity(),
                                                camera,
                                                new CameraSettings(JPEG_QUALITY, PIC_WIDTH));

            previewWindow = getActivity().findViewById(R.id.camera_preview_window);

            previewWindow.addView(cameraPreview);

            photo_callback = new Camera.PictureCallback() {
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

                    reload_pics();
                    restart_camera_preview();
                }
            };
        }
    }

    private void release_camera(){
        if (this.camera != null){
            this.camera.release();
            this.camera = null;
        }
    }

    //endregion
}
