package tech.sud.mgp.hello.ui.room.audio.example.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;

/**
 * 语聊房顶部的View
 */
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
        inflate(context, R.layout.view_audio_room_top, this);
        mTvName = findViewById(R.id.top_tv_name);
        mTvId = findViewById(R.id.top_tv_room_id);
        mTvNumber = findViewById(R.id.top_tv_room_number);
        mContainerMode = findViewById(R.id.top_ll_select_mode);
        mTvMode = findViewById(R.id.top_tv_mode_name);
        mIvClose = findViewById(R.id.top_iv_close);
    }

    /**
     * 设置房间名称
     */
    public void setName(String value) {
        mTvName.setText(value);
    }

    /**
     * 设置房号
     *
     * @param value
     */
    public void setId(String value) {
        mTvId.setText(value);
    }

    /**
     * 设置房间人数
     *
     * @param value
     */
    public void setNumber(String value) {
        mTvNumber.setText(value);
    }

    /**
     * 设置选择模式中的文字内容
     *
     * @param value
     */
    public void setMode(String value) {
        mTvMode.setText(value);
    }

    /**
     * 设置选择模式点击监听
     *
     * @param listener
     */
    public void setSelectModeClickListener(OnClickListener listener) {
        mContainerMode.setOnClickListener(listener);
    }

    /**
     * 设置关闭房间的点击监听
     *
     * @param listener
     */
    public void setCloseClickListener(OnClickListener listener) {
        mIvClose.setOnClickListener(listener);
    }

    /**
     * 设置选择模式的可见性
     *
     * @param visibility
     */
    public void setSelectModeVisibility(int visibility) {
        mContainerMode.setVisibility(visibility);
    }

}