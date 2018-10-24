package com.cbre.tsandford.cbreinspector.misc.voice;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cbre.tsandford.cbreinspector.R;
import java.io.IOException;

public class PlayButton extends AppCompatImageButton {

    private boolean is_playing;
    private MediaPlayer player;
    private String audio_resource;

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


    public void setAudio_resource(String audio_resource) {
        this.audio_resource = audio_resource;
    }

    private void setUp(){
        is_playing = false;
        setOnClickListener(clickListener);
        setImageResource(R.drawable.play_button_disabled);
        setScaleType(ImageView.ScaleType.FIT_XY);
        setHapticFeedbackEnabled(true);
        setBackgroundColor(0);
    }

    private void startPlaying(){
        player = new MediaPlayer();
        try{
            player.setDataSource(audio_resource);
            player.prepare();
            player.start();
        }catch(IOException ex){
            Log.d("TODD", "prepare() failed inside startPlaying()");
        }
    }

    private void stopPlaying(){
        player.release();
        player = null;
    }

}
