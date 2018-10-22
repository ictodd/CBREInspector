package com.cbre.tsandford.cbreinspector.fragments;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.cbre.tsandford.cbreinspector.AppState;
import com.cbre.tsandford.cbreinspector.R;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDictation extends Fragment {

    private ImageButton record_btn;
    boolean recording = false;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private String active_clip = null;

    public FragmentDictation() {
        // Required empty public constructor
    }

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
    }

    private void toggle_button() {
        if(!recording){
            record_btn.setImageResource(R.drawable.microphone_disabled);
            stopRecording();
        }else{
            record_btn.setImageResource(R.drawable.microphone_recording);
            startRecording();
        }
    }

    private void onPlay(boolean start){
        if(start){
            startPlaying();
        }else{
            stopPlaying();
        }
    }

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

    // todo make sure this recorder actually works...

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
        recorder.stop();
        recorder.release();
        recorder = null;
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
}
