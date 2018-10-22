package com.cbre.tsandford.cbreinspector.misc;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

public class CustomListView extends ListView {

    DoubleTouchListener double_touch_listener;

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        double_touch_listener = new DoubleTouchListener();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return double_touch_listener.onDoubleTap(ev);
    }

    @Override
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {

    }

    private class DoubleTouchListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d("TODD", "Double tap event fired");
            return true;
        }
    }
}

