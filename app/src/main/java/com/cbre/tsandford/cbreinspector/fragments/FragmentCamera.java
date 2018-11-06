package com.cbre.tsandford.cbreinspector.fragments;

import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.cbre.tsandford.cbreinspector.misc.camera.CameraController;
import com.cbre.tsandford.cbreinspector.misc.camera.CameraPreview;
import com.cbre.tsandford.cbreinspector.misc.camera.CameraSettings;
import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.cbre.tsandford.cbreinspector.model.PictureItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FragmentCamera extends Fragment {

    private static final String TAG = "FragmentCamera";

    private Button btnCamera;
    private LinearLayout gallery_view;

    private ImageView activeGalleryImage;

    private Camera camera;
    private CameraPreview cameraPreview;
    private FrameLayout previewWindow;
    private Camera.PictureCallback photo_callback;

    private PictureItem inspectionPictures;

    private final double THUMBNAIL_SCALE = 0.2;
    private final int THUMBNAIL_COMPRESSION = 30;

    private CameraController cameraController;

    // the below is not used any more, but might be useful in the future if users
    // want to be able to take photos in different resolutions and ratios.
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

        previewWindow = getActivity().findViewById(R.id.camera_preview_window);
        cameraController = new CameraController(getActivity(), new CameraSettings(JPEG_QUALITY, PIC_WIDTH), previewWindow);
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

                create_thumbnail(pictureFile);
                reload_pics();
                cameraController.restartCameraPreview();
            }
        });

        btnCamera = getActivity().findViewById(R.id.btn_camera);
        this.gallery_view = getActivity().findViewById(R.id.camera_gallery);

        btnCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cameraController.takePhoto();
            }
        });

        this.inspectionPictures = AppState.ActiveInspection.pictures;

        reload_pics();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraController.releaseCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraController.setUpCamera();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        // make sure i haven't accidentally registered another view for this menu...
        if(!(v instanceof android.widget.ImageView)) return;
        activeGalleryImage = (ImageView)v;
        menu.add(Menu.NONE,0,0,"Preview");
        menu.add(Menu.NONE, 1,1, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        File activeImageFile = new File(activeGalleryImage.getTag().toString());
        switch(item.getTitle().toString()){
            case "Delete":
                inspectionPictures.deletePic(activeImageFile);
                reload_pics();
                break;
            case "Preview":
                loadPreview(activeImageFile);
                break;

        }

        return true;
    }

    private void loadPreview(File activeImageFile) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentPicPreview frag = new FragmentPicPreview();
        frag.init(inspectionPictures.getPic(activeImageFile).getMain());
        frag.show(fm,"Pic_Preview");
    }

    // region Image and Gallery Functions

    private void clear_gallery(){
        this.gallery_view.removeAllViews();
    }

    // todo needs to be in date order with latest pic first
    private void reload_pics(){
        clear_gallery();
        inspectionPictures.RefreshItems();
        List<PictureItem.Pic> pics = inspectionPictures.getPicItems();
        for(PictureItem.Pic pic : pics)
            if(pic.hasThumb() && !gallery_contains_pic(pic.getThumb().getPath()))
                add_image_to_gallery(pic.getThumb());

    }

    private boolean gallery_contains_pic(String path){
        int number_of_children = this.gallery_view.getChildCount();
        for(int i = 0; i < number_of_children; i ++){
            if(gallery_view.getChildAt(i).getTag().toString() == path)
                return true;
        }
        return false;
    }

    private void add_image_to_gallery(File file){

        LinearLayout.LayoutParams view_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        view_params.bottomMargin = 5;
        view_params.leftMargin = 5;
        view_params.rightMargin = 5;
        view_params.topMargin = 5;

        ImageView new_img_view = new ImageView(this.getActivity());
        new_img_view.setImageURI(Uri.fromFile(file));
        new_img_view.setAdjustViewBounds(true);
        new_img_view.setTag(file.getPath());

        this.registerForContextMenu(new_img_view);

        Drawable background = getActivity().getDrawable(R.drawable.full_border_thin);
        new_img_view.setBackground(background);

        this.gallery_view.addView(new_img_view, view_params);
    }

    private void create_thumbnail(File pictureFile) {
        Utils.Image.ScaleImage(pictureFile, THUMBNAIL_SCALE, THUMBNAIL_COMPRESSION);
    }

    // endregion
}
