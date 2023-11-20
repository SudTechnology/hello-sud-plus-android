package tech.sud.mgp.hello.ui.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ForbidDragSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {
    public ForbidDragSeekBar(Context context) {
        super(context);
    }

    public ForbidDragSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForbidDragSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        return false;
    }

}
