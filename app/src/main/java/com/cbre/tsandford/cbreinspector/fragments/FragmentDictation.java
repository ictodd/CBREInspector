package com.cbre.tsandford.cbreinspector.fragments;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.cbre.tsandford.cbreinspector.AppState;
import com.cbre.tsandford.cbreinspector.R;
import com.cbre.tsandford.cbreinspector.misc.PromptRunnable;
import com.cbre.tsandford.cbreinspector.misc.voice.AudioConverter;
import com.cbre.tsandford.cbreinspector.misc.voice.PlayButton;
import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.cbre.tsandford.cbreinspector.misc.voice.SpeechToTextController;
import com.cbre.tsandford.cbreinspector.misc.voice.VoiceNoteCard;
import com.cbre.tsandford.cbreinspector.misc.voice.VoiceNoteMetadata;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FragmentDictation extends Fragment {

    // region Private Members

    private ImageButton record_btn;
    private boolean recording = false;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private String active_clip = null;
    private LinearLayout gallery_view_parent = null;

    static String TAG = "FragDictation";

    // endregion

    private VoiceNoteCard activeNote;

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
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, 0,0, "Edit");
        menu.add(Menu.NONE, 1,1, "Delete");
        activeNote = (VoiceNoteCard)v;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(activeNote == null)
            return false;

        switch(item.getTitle().toString()){
            case "Edit":
                loadEditMenu();
                break;
            case "Delete":
                deleteActiveCard();
                break;
        }
        return true;
    }

    private void deleteActiveCard() {
        Utils.showYesNoDialog(getActivity(),
                "Confirm Delete",
                "Are you sure you want to delete this voice note?",
                new PromptRunnable(){
                    @Override
                    public void run() {
                        File voiceFile = new File(activeNote.getTag().toString());
                        File metaFile = new File(activeNote.getMetadata().getMetadataPath());
                        try{
                            Files.delete(voiceFile.toPath());
                            Files.delete(metaFile.toPath());
                        }catch(Exception ex){
                            Log.d("TODD", "failed to delete voice and meta files");
                        }
                        reload_voice_notes();
                    }
                },
                new PromptRunnable(){
                    @Override
                    public void run() {
                        // blank for no
                    }
                });
    }

    private void loadEditMenu() {
        FragmentManager fm = getActivity().getSupportFragmentManager();

        FragmentEditVoiceNote frag = new FragmentEditVoiceNote();
        VoiceNoteMetadata metadata = activeNote.getMetadata();
        frag.init(metadata.getName());
        frag.setDialogCloseHandler(new FragmentEditVoiceNote.CustomDialogListener() {
            @Override
            public void onDialogClose(String name) {
                activeNote.updateName(name);
                activeNote.saveAllMetadataToFile();
            }
        });
        frag.show(fm, "fragment_edit_voice_note");
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
        save_all_voice_note_metadata();
    }

    @Override
    public void onResume() {
        super.onResume();
        reload_voice_notes();
    }

    //endregion

    // region Recording Methods
    private void startRecording(){
        AppState.ActiveInspection.audio_clips.extension = "aac";

        active_clip = AppState.ActiveInspection.audio_clips.get_new_resource().getPath();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setOutputFile(active_clip);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try{
            recorder.prepare();
        }catch(IOException ex){
            Log.d(TAG, "prepare() on starting recording failed");
        }

        try{
            recorder.start();
        }catch(Exception ex){
            Log.d(TAG, "start() on recording failed");
            ex.printStackTrace();
        }


    }

    private void stopRecording(){
        if(recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
            reload_voice_notes();
        }
    }

    // endregion

    private void toggle_button() {
        if(recording){
            record_btn.setImageResource(R.drawable.microphone_disabled);
            stopRecording();

            final AudioConverter converter = new AudioConverter(getContext());
            converter.setCallbackHandler(new AudioConverter.OnFinishCallBackHandler() {
                @Override
                public void Success() {
                    File newFile = converter.getOutputFile();
                    transcribe(newFile);
                }

                @Override
                public void Failure() {

                }
            });

            converter.convert(new File(active_clip), AudioConverter.AudioFormat.FLAC);

        }else{
            record_btn.setImageResource(R.drawable.microphone_recording);
            startRecording();
        }
        recording = !recording;
    }

    private void transcribe(File f){
        SpeechToTextController speechToTextController = new SpeechToTextController();
        speechToTextController.setServiceCallback(new ServiceCallback() {
            @Override
            public void onResponse(Object response) {
                Log.d(TAG, "transcribe got a response");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "transcribe failed: " + e.getMessage());
                e.printStackTrace();
            }
        });

        speechToTextController.setBaseRecogCallback(new BaseRecognizeCallback(){

            @Override
            public void onConnected() {
                Log.d(TAG,"Web socket connected.");
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG,"Web socket disconnected.");
            }

            @Override
            public void onTranscription(SpeechRecognitionResults speechResults) {
                Log.d(TAG,"Got results.");
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG,"Web socket error. Error: " + e.getMessage());
            }

        });

        try {
            Log.d(TAG,"Trying to get transcription of " + f.getPath());
            speechToTextController.getTranscription(f);
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    // region Gallery Methods

    private void clear_gallery(){
        this.gallery_view_parent.removeAllViews();
    }

    private void reload_voice_notes(){
        clear_gallery();
        List<Uri> voice_notes = AppState.ActiveInspection.audio_clips.get_all_items(30);
        for(Uri voice_note : voice_notes){
            if(!gallery_contains_voice_note(voice_note.getPath()))
                add_voice_note(voice_note);
        }
    }

    private void save_all_voice_note_metadata(){
        int number_of_children = gallery_view_parent.getChildCount();
        VoiceNoteCard card;
        for(int i = 0; i < number_of_children; i ++){
            card = (VoiceNoteCard)gallery_view_parent.getChildAt(i);
            card.saveAllMetadataToFile();
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

    private void add_voice_note(Uri voiceNote){
        VoiceNoteCard card = new VoiceNoteCard(this.getContext(), this.getActivity(), voiceNote.getPath());
        card.setAudioResource(voiceNote);
        registerForContextMenu(card);
        gallery_view_parent.addView(card);
    }
    // endregion

}

