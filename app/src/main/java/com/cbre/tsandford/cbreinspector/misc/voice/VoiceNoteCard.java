package com.cbre.tsandford.cbreinspector.misc.voice;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cbre.tsandford.cbreinspector.R;
import com.cbre.tsandford.cbreinspector.misc.Utils;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class VoiceNoteCard extends GridLayout {

    private Context parentContext;
    private Activity parentActivity;

    private TextView nameTxtView;
    private TextView dateTxtView;

    private PlayButton playButton;

    private VoiceNoteMetadata metadata;

    public VoiceNoteCard(Context context, Activity parentActivity, String recFilePath){
        super(context);
        init(context, parentActivity, recFilePath);
    }

    // region Base Constructors

    public VoiceNoteCard(Context context) {
        super(context);
    }

    public VoiceNoteCard(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public VoiceNoteCard(Context context, AttributeSet attrs, int styleAttr){
        super(context, attrs, styleAttr);
    }

    public VoiceNoteCard(Context context, AttributeSet attrs, int styleAttr, int styleRes){
        super(context, attrs, styleAttr, styleRes);
    }

    // endregion

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
        <com.cbre.tsandford.cbreinspector.misc.voice.PlayButton
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

    private void init(Context context, Activity parentActivity, String recFilePath){
        this.parentContext = context;
        this.parentActivity = parentActivity;
        this.setTag(recFilePath);
        setStaticHeadings();
        this.playButton = getNewPlayButton();
        this.addView(this.playButton);
        this.setBackground(getResources().getDrawable(R.drawable.full_border_thin));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 20;
        this.setLayoutParams(params);
        this.setMetadata();
    }

    // region Public Metadata Methods

    public void updateName(String newName){
        if(this.metadata == null)
            return;

        this.metadata.setName(newName);
        this.nameTxtView.setText(newName);
    }

    public void updateTags(List<String> newTags){
        if(this.metadata == null)
            return;

        this.metadata.setTags(newTags);
        this.nameTxtView.setText(flattenList(newTags));
    }

    public void updateTranscription(String transcription){
        if(this.metadata == null)
            return;

        this.metadata.setTranscription(transcription);
    }

    public void saveAllMetadataToFile(){
        this.metadata.saveData();
    }

    // endregion

    private void setStaticHeadings(){
        // label text views
        TextView nameHeading = getNewTextView("Name:", true, 0, 0, false);
        TextView dateHeading = getNewTextView("Date:", true, 1, 0, false);

        // blank context text views
        nameTxtView = getNewTextView("", false, 0,1, true);
        dateTxtView = getNewTextView("", false, 1,1, true);

        this.addView(nameHeading);
        this.addView(dateHeading);

        this.addView(nameTxtView);
        this.addView(dateTxtView);
    }

    private void setMetadata(){

        // this should never be called before the tag is set
        // tag has the file path to 3gp file
        if(getTag() == null)
            return;

        File recFile = new File(getTag().toString());
        String audioLoc = recFile.getPath();
        String extension = Utils.GetFileExtension(recFile);
        String metadataPath = audioLoc.replace(extension, ".json");
        File metaFile = new File(metadataPath);

        if(Files.exists(metaFile.toPath())){
            this.metadata = new VoiceNoteMetadata(recFile, metaFile);
            this.metadata.restoreData();
        } else {
            this.metadata = new VoiceNoteMetadata(recFile);
            this.metadata.setMetadataPath(metaFile.getPath());
        }
        setHeadingsFromMetaData();
    }

    public VoiceNoteMetadata getMetadata() {
        return metadata;
    }

    // public to reset headings
    public void setHeadingsFromMetaData(){
        if(metadata == null)
            return;

        this.nameTxtView.setText(metadata.getName());
        this.dateTxtView.setText(metadata.getDate());
    }

    private String flattenList(List<String> tags) {
        StringBuilder sb = new StringBuilder();
        for(String tag: tags){
            sb.append(tag.trim()).append(", ");
        }
        return sb.toString().substring(0, sb.toString().lastIndexOf(",")).trim();
    }

    public void setAudioResource(Uri audioResource){
        this.playButton.setAudio_resource(audioResource.getPath());
        this.setTag(audioResource.getPath());
    }

    private TextView getNewTextView(String txt, boolean boldText, int row, int col, boolean fixedWidth){

        TextView textView = new TextView(parentActivity);
        int padding = 5;
        int colSpan = 1;
        int rowSpan = 1;

        GridLayout.Spec rowSpec = GridLayout.spec(row, rowSpan);
        GridLayout.Spec colSpec = GridLayout.spec(col, colSpan);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.setGravity(Gravity.CENTER_VERTICAL);

        if(fixedWidth)
            params.width = 440;

        textView.setText(txt);
        textView.setPadding(padding,padding,padding,padding);
        textView.setLayoutParams(params);

        Typeface font = boldText ? ResourcesCompat.getFont(parentContext, R.font.futura_heavy) :
                ResourcesCompat.getFont(parentContext, R.font.futura_book);

        textView.setTypeface(font);

        return textView;
    }

    private PlayButton getNewPlayButton(){
        PlayButton playButton = new PlayButton(parentContext);
        int padding = 5;
        int row = 0;
        int col = 2;
        int rowSpan = 3;
        int colSpan = 1;

        GridLayout.Spec rowSpec = GridLayout.spec(row, rowSpan);
        GridLayout.Spec colSpec = GridLayout.spec(col, colSpan);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.setGravity(Gravity.CENTER);
        params.height = 100;
        params.width = 100;

        playButton.setPadding(padding,padding,padding,padding);
        playButton.setLayoutParams(params);

        return playButton;
    }
}
