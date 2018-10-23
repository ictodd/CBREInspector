package com.cbre.tsandford.cbreinspector.misc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cbre.tsandford.cbreinspector.R;

public class VoiceNoteCard extends GridLayout {

    private Context parentContext;
    private Activity parentActivity;

    private TextView nameHeading;
    private TextView dateHeading;
    private TextView tagsHeading;

    private TextView nameTxtView;
    private TextView dateTxtView;
    private TextView tagsTxtView;

    private PlayButton playButton;

    public VoiceNoteCard(Context context, Activity parentActivity){
        super(context);
        init(context, parentActivity);
    }

    private void init(Context context, Activity parentActivity){
        this.parentContext = context;
        this.parentActivity = parentActivity;
        setStaticHeadings();
        this.playButton = getNewPlayButton();
        this.addView(this.playButton);
        this.setBackground(getResources().getDrawable(R.drawable.full_border_thin));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 20;
        this.setLayoutParams(params);
    }

    private void setStaticHeadings(){
        // label text views
        nameHeading = getNewTextView("Name:", true, 0, 0, false);
        dateHeading = getNewTextView("Date:", true, 1, 0, false);
        tagsHeading = getNewTextView("Tags:", true, 2, 0, false);

        // blank context text views
        nameTxtView = getNewTextView("", false, 0,1, true);
        dateTxtView = getNewTextView("", false, 1,1, true);
        tagsTxtView = getNewTextView("", false, 2,1, true);

        this.addView(nameHeading);
        this.addView(dateHeading);
        this.addView(tagsHeading);

        this.addView(nameTxtView);
        this.addView(dateTxtView);
        this.addView(tagsTxtView);
    }

    public void setHeadings(String name, String date, String tags){
        this.nameTxtView.setText(name);
        this.dateTxtView.setText(date);
        this.tagsTxtView.setText(tags);
    }

    public void setAudioResource(Uri audioResource){
        this.playButton.setTag(audioResource.getPath());
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

        playButton.setBackground(parentContext.getDrawable(R.drawable.play_button_disabled));
        playButton.setPadding(padding,padding,padding,padding);
        playButton.setLayoutParams(params);
        playButton.setScaleType(ImageView.ScaleType.FIT_XY);
        playButton.setHapticFeedbackEnabled(true);

        return playButton;
    }
}
