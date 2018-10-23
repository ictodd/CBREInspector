package com.cbre.tsandford.cbreinspector.misc;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.cbre.tsandford.cbreinspector.AppState;
import com.cbre.tsandford.cbreinspector.R;

import java.io.File;
import java.io.IOException;

public class PlayButton extends AppCompatImageButton {

    private boolean is_playing;
    private MediaPlayer player;
    // uses tag of imagebutton to set data source

    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(is_playing){
                setImageResource(R.drawable.play_button_disabled);
                stopPlaying();
            } else {
                setImageResource(R.drawable.play_button_enabled);
                startPlaying();
            }
            is_playing = !is_playing;
        }
    };

    // region Constructors

    public PlayButton(Context context){
        super(context);
        setUp();
    }

    public PlayButton(Context context, AttributeSet attrs){
        super(context, attrs);
        setUp();
    }

    public PlayButton(Context context, AttributeSet attrs, int styleAttr){
        super(context, attrs, styleAttr);
        setUp();
    }

    // endregion

    private void setUp(){
        is_playing = false;
        setOnClickListener(clickListener);
    }

    private void startPlaying(){
        player = new MediaPlayer();
        try{
            player.setDataSource(getTag().toString());
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

    private boolean audio_source_is_valid(){
        File f = new File(getTag().toString());
        if(!Utils.FileHasContents(f.getPath()))
            return false;

        if(Utils.getFileExtension(f) != AppState.ActiveInspection.audio_clips.extension)
            return false;

        return true;

    }

}
