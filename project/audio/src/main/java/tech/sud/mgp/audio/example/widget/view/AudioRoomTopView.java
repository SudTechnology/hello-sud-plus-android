package tech.sud.mgp.audio.example.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.audio.R;

public class AudioRoomTopView extends ConstraintLayout {

    private TextView mTvName;
    private TextView mTvId;
    private TextView mTvNumber;
    private View mContainerMode;
    private TextView mTvMode;
    private ImageView mIvClose;

    public AudioRoomTopView(@NonNull Context context) {
        this(context, null);
    }

    public AudioRoomTopView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRoomTopView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.audio_view_room_top, this);
        mTvName = findViewById(R.id.top_tv_name);
        mTvId = findViewById(R.id.top_tv_room_id);
        mTvNumber = findViewById(R.id.top_tv_room_number);
        mContainerMode = findViewById(R.id.top_ll_select_mode);
        mTvMode = findViewById(R.id.top_tv_mode_name);
        mIvClose = findViewById(R.id.top_iv_close);
    }

    public void setName(String value) {
        mTvName.setText(value);
    }

    public void setId(String value) {
        mTvId.setText(value);
    }

    public void setNumber(String value) {
        mTvNumber.setText(value);
    }

    public void setMode(String value) {
        mTvMode.setText(value);
    }

    public void setSelectModeClickListener(OnClickListener listener) {
        mContainerMode.setOnClickListener(listener);
    }

    public void setCloseClickListener(OnClickListener listener) {
        mIvClose.setOnClickListener(listener);
    }

}
