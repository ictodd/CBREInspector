package com.cbre.tsandford.cbreinspector.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cbre.tsandford.cbreinspector.AppState;
import com.cbre.tsandford.cbreinspector.PaintView;
import com.cbre.tsandford.cbreinspector.R;
import com.cbre.tsandford.cbreinspector.misc.PromptRunnable;
import com.cbre.tsandford.cbreinspector.misc.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class FragmentDraw extends Fragment {

    private PaintView paint_view;
    private View gallery_view_pane;
    private LinearLayout gallery_view_parent;
    static boolean gallery_is_shown;
    private ImageView activeGalleryImage;

    private RelativeLayout rootView;
    private LinearLayout paintParentView;

    public FragmentDraw() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_draw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        paint_view = getActivity().findViewById(R.id.paint_view);
        DisplayMetrics metrics = new DisplayMetrics();

        gallery_view_pane = getActivity().findViewById(R.id.drawing_gallery_scroll_view);
        gallery_view_parent = getActivity().findViewById(R.id.drawing_gallery_parent);

        rootView = getActivity().findViewById(R.id.drawing_root_relative_layout);

        paintParentView = getActivity().findViewById(R.id.draw_canvas_parent);

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paint_view.init(metrics);
        BindAllButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
        hide_gallery(gallery_view_pane);
        gallery_is_shown = false;
        reload_gallery();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // make sure i haven't accidentally registered another view for this menu...
        if (!(v instanceof android.widget.ImageView)) return;
        activeGalleryImage = (ImageView) v;
        menu.add(Menu.NONE, 0, 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // only using a delete option right now...
        String image_file_path = activeGalleryImage.getTag().toString();
        new File(image_file_path).delete();
        reload_gallery();
        return true;
    }

    private void BindAllButtons() {
        ImageView imgBtn = getActivity().findViewById(R.id.draw_thin_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint_view.setNewBrushSize(PaintView.BrushSize.Small);
            }
        });
        imgBtn = getActivity().findViewById(R.id.draw_medium_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint_view.setNewBrushSize(PaintView.BrushSize.Medium);
            }
        });
        imgBtn = getActivity().findViewById(R.id.draw_thick_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint_view.setNewBrushSize(PaintView.BrushSize.Large);
            }
        });
        imgBtn = getActivity().findViewById(R.id.draw_black_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint_view.setNewBrushColour(PaintView.BrushColour.Black);
            }
        });
        imgBtn = getActivity().findViewById(R.id.draw_blue_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint_view.setNewBrushColour(PaintView.BrushColour.Blue);
            }
        });
        imgBtn = getActivity().findViewById(R.id.draw_red_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint_view.setNewBrushColour(PaintView.BrushColour.Red);
            }
        });
        imgBtn = getActivity().findViewById(R.id.draw_eraser_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint_view.SetEraserMode();
            }
        });

        imgBtn = getActivity().findViewById(R.id.draw_highlighter_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint_view.SetHighlighterMode();
            }
        });

        imgBtn = getActivity().findViewById(R.id.draw_erase_all_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.showYesNoDialog(getActivity(), "Confirm", "Are you sure you want to clear the canvas?",
                        new PromptRunnable() {
                            public void run() {
                                paint_view.clear();
                            }
                        },
                        new PromptRunnable() {
                            public void run() {
                                // pass
                            }
                        });
            }
        });

        imgBtn = getActivity().findViewById(R.id.draw_gallery_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gallery_is_shown) {
                    hide_gallery(gallery_view_pane);
                } else {
                    show_gallery(gallery_view_pane);
                }
                gallery_is_shown = !gallery_is_shown;
            }
        });

        imgBtn = getActivity().findViewById(R.id.draw_save_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showYesNoDialog(getActivity(), "Confirm", "Are you sure you want to save the current drawing?",
                        new PromptRunnable() {
                            public void run() {
                                save_current_drawing();
                                reload_gallery();
                            }
                        },
                        new PromptRunnable() {
                            public void run() {
                                // pass
                            }
                        });
            }
        });
    }

    // region Gallery Controls

    private void show_gallery(View v) {
        TranslateAnimation animate = new TranslateAnimation(
                -v.getWidth(),
                0,
                0,
                0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        v.startAnimation(animate);

        rootView.bringChildToFront(gallery_view_pane);
        rootView.invalidate();
    }

    public void hide_gallery(View v) {
        rootView.bringChildToFront(paintParentView);
        rootView.invalidate();
        TranslateAnimation animate = new TranslateAnimation(
                0,
                -v.getWidth(),
                0,
                0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        v.startAnimation(animate);
    }

    // todo BUG: doesnt always load all files OR doesnt always save...
    private void reload_gallery() {
        clear_gallery();
        List<Uri> pics = AppState.ActiveInspection.drawings.get_all_items(5);
        for (Uri pic : pics) {
            if (!gallery_contains_drawing(pic.getPath()))
                add_image_to_gallery(pic);
        }
    }

    private boolean gallery_contains_drawing(String path) {
        int number_of_children = this.gallery_view_parent.getChildCount();
        for (int i = 0; i < number_of_children; i++) {
            if (this.gallery_view_parent.getChildAt(i).getTag().toString() == path)
                return true;
        }
        return false;
    }

    private void add_image_to_gallery(Uri image_uri) {

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

        // add an image load on click listener
        // when user clicks image in gallery they're given the option to load
        new_img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Utils.showYesNoDialog(getActivity(),
                        "Confirm",
                        "Are you sure you want to load this image? Your current drawing will be overwritten.",
                        new PromptRunnable(){
                            @Override
                            public void run() {
                                load_image(v.getTag().toString());
                            }
                        },
                        new PromptRunnable(){
                            @Override
                            public void run() {
                                super.run();
                            }
                        });
            }
        });

        this.gallery_view_parent.addView(new_img_view, view_params);
    }

    private void load_image(String image_filepath){
        Bitmap bmp = BitmapFactory.decodeFile(image_filepath);
        this.paint_view.loadBitmapToCanvas(bmp);
    }

    private void clear_gallery() {
        this.gallery_view_parent.removeAllViews();
    }

    // endregion

    // todo BUG: doesnt always save...OR saves only without new edits
    private Uri save_current_drawing() {

        Uri result = null;

        Bitmap bitmap = paint_view.getCurrentImage();
        String newDrawingFile = AppState.ActiveInspection.drawings.get_new_resource().getPath();
        FileOutputStream outputStream;

        try {
            File newFile = new File(newDrawingFile);
            outputStream = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            result = Uri.fromFile(newFile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}

