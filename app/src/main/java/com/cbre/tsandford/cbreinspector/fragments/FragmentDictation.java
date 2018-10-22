package com.cbre.tsandford.cbreinspector.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cbre.tsandford.cbreinspector.AppState;
import com.cbre.tsandford.cbreinspector.R;

import java.io.IOException;
import java.util.List;

public class FragmentDictation extends Fragment {

    // region Private Members

    private ImageButton record_btn;
    private boolean recording = false;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private String active_clip = null;
    private LinearLayout gallery_view_parent = null;

    // endregion

    private ImageButton test_btn;

    public FragmentDictation() {
        // Required empty public constructor
    }

    // region Fragment Events

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dictation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        record_btn = getActivity().findViewById(R.id.dictation_record_btn);
        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle_button();
            }
        });
        gallery_view_parent = getActivity().findViewById(R.id.voice_note_gallery);
        test_btn = getActivity().findViewById(R.id.btn_test_voice_playback);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(recorder != null){
            recorder.release();
            recorder = null;
        }

        if(player != null){
            player.release();
            player = null;
        }
    }

    //endregion

    // region Recording Methods

    private void startRecording(){
        active_clip = AppState.ActiveInspection.audio_clips.get_new_resource().getPath();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(active_clip);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try{
            recorder.prepare();
        }catch(IOException ex){
            Log.d("TODD", "prepare() on starting recording failed");
        }

        recorder.start();
    }

    private void stopRecording(){
        if(recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    // endregion

    // region Playing Methods

    private void startPlaying(){
        player = new MediaPlayer();
        try{
            player.setDataSource(active_clip);
            player.prepare();
            player.start();
        }catch(IOException ex){
            Log.d("TODD", "prepare() failed");
        }
    }

    private void stopPlaying(){
        player.release();
        player = null;
    }

    // endregion

    private void onPlay(boolean start){
        if(start){
            startPlaying();
        }else{
            stopPlaying();
        }
    }

    private void toggle_button() {
        if(recording){
            record_btn.setImageResource(R.drawable.microphone_disabled);
            stopRecording();
        }else{
            record_btn.setImageResource(R.drawable.microphone_recording);
            startRecording();
        }
        recording = !recording;
    }

    // region Gallery Methods

    private void clear_gallery(){
        this.gallery_view_parent.removeAllViews();
    }

    private void reload_voice_notes(){
        clear_gallery();
        List<Uri> voice_notes = AppState.ActiveInspection.audio_clips.get_all_items(10);
        for(Uri voice_note : voice_notes){
            if(!gallery_contains_voice_note(voice_note.getPath()))
                add_voice_note_to_gallery(voice_note);
        }
    }

    private boolean gallery_contains_voice_note(String path){
        int number_of_children = gallery_view_parent.getChildCount();
        for(int i = 0; i < number_of_children; i ++){
            if(gallery_view_parent.getChildAt(i).getTag().toString() == path)
                return true;
        }
        return false;
    }

    private void add_voice_note_to_gallery(Uri voice_note_uri){
//
//        LinearLayout.LayoutParams view_params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        view_params.bottomMargin = 5;
//        view_params.leftMargin = 5;
//        view_params.rightMargin = 5;
//        view_params.topMargin = 5;
//
//        ImageView new_img_view = new ImageView(this.getActivity());
//        new_img_view.setImageURI(voice_note_uri);
//        new_img_view.setAdjustViewBounds(true);
//        new_img_view.setTag(voice_note_uri.getPath());
//
//        this.registerForContextMenu(new_img_view);
//
//        Drawable background = getActivity().getDrawable(R.drawable.full_border_thin);
//        new_img_view.setBackground(background);
//
//        this.gallery_view.addView(new_img_view, view_params);
    }

    // endregion

}

