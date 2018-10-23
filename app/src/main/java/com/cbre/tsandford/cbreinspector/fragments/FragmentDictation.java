package com.cbre.tsandford.cbreinspector.fragments;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
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
import com.cbre.tsandford.cbreinspector.misc.PlayButton;
import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.cbre.tsandford.cbreinspector.misc.VoiceNoteCard;

import java.io.File;
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

    // endregion

    private PlayButton test_btn;

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
    }


    // todo implement menu options
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getTitle().toString()){
            case "Edit":
                //do stuff
                break;
            case "Delete":
                // do stuff
                break;
        }
        return true;
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

    @Override
    public void onResume() {
        super.onResume();
        reload_voice_notes();
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
            reload_voice_notes();
        }
    }

    // endregion

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
                add_voice_note(voice_note);
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

    // region Example Voice Note Card

    /*
     <GridLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:rowCount="3"
        android:columnCount="3"
        android:background="@drawable/full_border_thin">
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Name:"
            android:layout_row="0"
            android:layout_column="0"
            android:padding="5dp"
            android:fontFamily="@font/futura_heavy"
            android:layout_gravity="center_vertical"
            android:layout_rowWeight="1"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="abc_street_audio_clips_1"
            android:padding="5dp"
            android:layout_row="0"
            android:layout_column="1"
            android:layout_gravity="center_vertical"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Date:"
            android:layout_row="1"
            android:layout_column="0"
            android:padding="5dp"
            android:fontFamily="@font/futura_heavy"
            android:layout_gravity="center_vertical"
            android:layout_rowWeight="1"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="23 Oct 2018 10:14 am"
            android:layout_row="1"
            android:layout_column="1"
            android:padding="5dp"
            android:layout_gravity="center_vertical"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Tags:"
            android:layout_row="2"
            android:layout_column="0"
            android:padding="5dp"
            android:fontFamily="@font/futura_heavy"
            android:layout_gravity="center_vertical"
            android:layout_rowWeight="1"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="None"
            android:layout_row="2"
            android:layout_column="1"
            android:padding="5dp"
            android:layout_gravity="center_vertical"/>
        <com.cbre.tsandford.cbreinspector.misc.PlayButton
            android:id="@+id/btn_test_voice_playback"
            android:padding="5dp"
            android:layout_column="2"
            android:layout_row="0"
            android:layout_rowSpan="3"
            android:hapticFeedbackEnabled="true"
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:scaleType="fitXY"
            android:background="#00000000"
            android:src="@drawable/play_button_disabled" />
    </GridLayout>

</LinearLayout>
     */

    // endregion

    private void add_voice_note(Uri voiceNote){
        VoiceNoteCard card = new VoiceNoteCard(this.getContext(), this.getActivity());
        card.setAudioResource(voiceNote);
        String name = getName(voiceNote);
        String date = getDate(voiceNote);
        String tags = getTags(voiceNote);
        card.setHeadings(name, date, tags);
        registerForContextMenu(card);
        gallery_view_parent.addView(card);
    }

    private String getTags(Uri voice_note_uri) {
        return "Tag 1, Tag 2";
    }

    private String getDate(Uri voice_note_uri) {
        BasicFileAttributes attrs = null;
        try{
            Path path = Paths.get(voice_note_uri.getPath());
            attrs = Files.readAttributes(path, BasicFileAttributes.class);
        }catch(Exception e){
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat("d MMM yy - h:mm a ", Locale.UK);
        String result = "";
        if(attrs != null){
            result = df.format(attrs.creationTime().toMillis());
        }
        return result;

    }

    private String getName(Uri voice_note_uri) {
        return Utils.getFieNameNoExtension(new File(voice_note_uri.getPath()));
    }

    // endregion

}

