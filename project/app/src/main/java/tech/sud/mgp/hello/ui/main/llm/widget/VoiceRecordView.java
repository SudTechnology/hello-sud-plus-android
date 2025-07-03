package tech.sud.mgp.hello.ui.main.llm.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.sud.mgp.hello.R;

public class VoiceRecordView extends androidx.appcompat.widget.AppCompatTextView {

    private OnRecordListener mOnRecordListener;
    private boolean isRecording;

    public VoiceRecordView(@NonNull Context context) {
        this(context, null);
    }

    public VoiceRecordView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceRecordView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setTextSize(14);
        setText(R.string.hold_to_record);
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        setTouchStatus(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isRecording = true;
                setTouchStatus(true);
                getParent().requestDisallowInterceptTouchEvent(true);
                if (mOnRecordListener != null) {
                    mOnRecordListener.onStart();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isRecording = false;
                setTouchStatus(false);
                if (mOnRecordListener != null) {
                    mOnRecordListener.onStop();
                }
                break;
        }
        return true;
    }

    private void setTouchStatus(boolean isTouch) {
        if (isTouch) {
            setBackgroundColor(Color.parseColor("#666666"));
        } else {
            setBackgroundColor(Color.BLACK);
        }
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        mOnRecordListener = onRecordListener;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public interface OnRecordListener {
        void onStart();

        void onStop();
    }

}
