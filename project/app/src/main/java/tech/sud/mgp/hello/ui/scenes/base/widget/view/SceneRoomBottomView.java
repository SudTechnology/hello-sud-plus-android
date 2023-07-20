package tech.sud.mgp.hello.ui.scenes.base.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;

public class SceneRoomBottomView extends ConstraintLayout {

    private LinearLayout mViewGotMic;
    private ImageView mIvMicState;
    private TextView mTvInput;
    private View mViewGift;
    private LinearLayout leftContainer;
    private LinearLayout rightContainer;

    private boolean micOpened = false;

    public SceneRoomBottomView(@NonNull Context context) {
        this(context, null);
    }

    public SceneRoomBottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SceneRoomBottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_scene_room_bottom, this);
        mViewGotMic = findViewById(R.id.ll_got_mic);
        mIvMicState = findViewById(R.id.iv_mic_state);
        mTvInput = findViewById(R.id.tv_input);
        mViewGift = findViewById(R.id.view_gift);
        leftContainer = findViewById(R.id.left_container);
        rightContainer = findViewById(R.id.right_container);
    }

    public void showMicState() {
        mIvMicState.setVisibility(View.VISIBLE);
    }

    public void hideMicState() {
        mIvMicState.setVisibility(View.GONE);
    }

    public void showGotMic() {
        mViewGotMic.setVisibility(View.VISIBLE);
    }

    public void hideGotMic() {
        mViewGotMic.setVisibility(View.GONE);
    }

    public boolean isMicOpened() {
        return micOpened;
    }

    /** 设置上麦按钮、麦克风按钮等等，是否显示 */
    public void setLeftContainerChildViewsVisibility(int visibility) {
        for (int i = 0; i < leftContainer.getChildCount(); i++) {
            leftContainer.getChildAt(i).setVisibility(visibility);
        }
    }

    public void setMicOpened(boolean micOpened) {
        this.micOpened = micOpened;
        if (micOpened) {
            mIvMicState.setImageResource(R.drawable.ic_mic_opened);
        } else {
            mIvMicState.setImageResource(R.drawable.ic_mic_closed);
        }
    }

    public void setInputClickListener(OnClickListener listener) {
        mTvInput.setOnClickListener(listener);
    }

    public void setGotMicClickListener(OnClickListener listener) {
        mViewGotMic.setOnClickListener(listener);
    }

    /**
     * 麦克风按钮点击监听
     */
    public void setMicStateClickListener(OnClickListener listener) {
        mIvMicState.setOnClickListener(listener);
    }

    public void setGiftClickListener(OnClickListener listener) {
        mViewGift.setOnClickListener(listener);
    }

    /** 在右侧添加一个View */
    public void addViewToRight(View view, int index, LinearLayout.LayoutParams params) {
        rightContainer.addView(view, index, params);
    }

    /** 在左侧添加一个View */
    public void addViewToLeft(View view, int index, LinearLayout.LayoutParams params) {
        leftContainer.addView(view, index, params);
    }

    public void setGiftBackground(@DrawableRes int resId) {
        mViewGift.setBackgroundResource(resId);
    }

    public void setInputBackground(@DrawableRes int resId) {
        mTvInput.setBackgroundResource(resId);
    }

    public void setInputVisibility(int visibility) {
        mTvInput.setVisibility(visibility);
    }

    public void setGiftVisibility(int visibility) {
        mViewGift.setVisibility(visibility);
    }

}
