package com.genymobile.scrcpy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

public class ScrcpyView extends SurfaceView implements View.OnTouchListener {

    public ScrcpyView(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public ScrcpyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrcpyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ScrcpyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        event.getPointerId()
        return false;
    }
}
