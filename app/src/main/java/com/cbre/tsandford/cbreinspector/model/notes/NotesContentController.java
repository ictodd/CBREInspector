package com.cbre.tsandford.cbreinspector.model.notes;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cbre.tsandford.cbreinspector.R;

public class NotesContentController extends LinearLayout {

    public enum Type{
        EditText,
        ComboBox,
        Table,
        RadioButton,
        Checkbox
    }

    private String mHeading;
    private View mContentView;

    // region Constructors

    public NotesContentController(Context context) {
        super(context);
    }

    public NotesContentController(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NotesContentController(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NotesContentController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // endregion

    public void init(String heading, Type contentType){
        mHeading = heading;
        setUpBaseLayout();
        setUpHeadingView();
        setUpContentView(contentType);
    }

    private void setUpBaseLayout() {
        LinearLayout.LayoutParams params = getDefaultParams();
        int padding = 5;
        this.setOrientation(LinearLayout.VERTICAL);
        this.setLayoutParams(params);
        this.setPadding(padding,padding,padding,padding);
    }

    private void setUpHeadingView() {
        TextView txtView = new TextView(getContext());
        LinearLayout.LayoutParams params = getDefaultParams();
        txtView.setTypeface(getFont(true));
        txtView.setLayoutParams(params);
        txtView.setTextSize(15);
        txtView.setText(mHeading);
        this.addView(txtView);
    }

    private void setUpContentView(Type contentType) {
        switch (contentType){
            case EditText:
                mContentView = setUpEditText();
                break;
            case Table:
                // Todo write table content view
                break;
            case Checkbox:
                // Todo write checkbox content view
                break;
            case ComboBox:
                // Todo write checkbox content view
                break;
            case RadioButton:
                break;
        }

        if(mContentView != null)
            this.addView(mContentView);

    }

    private EditText setUpEditText() {
        EditText result = new EditText(getContext());
        LinearLayout.LayoutParams params = getDefaultParams();
        result.setTypeface(getFont(false));
        result.setLayoutParams(params);
        result.setPadding(5,5,5,5);
        result.setTextSize(15);
        return result;
    }

    private LayoutParams getDefaultParams(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,10);
        return params;
    }

    private Typeface getFont(boolean boldText){
        return boldText ? ResourcesCompat.getFont(getContext(), R.font.futura_heavy) :
                            ResourcesCompat.getFont(getContext(), R.font.futura_book);
    }
}
