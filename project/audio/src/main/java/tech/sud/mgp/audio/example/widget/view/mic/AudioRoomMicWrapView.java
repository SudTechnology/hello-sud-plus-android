package tech.sud.mgp.audio.example.widget.view.mic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.audio.example.model.AudioRoomMicModel;

/**
 * 语聊房顶层的麦位View，管理切换不同形态
 */
public class AudioRoomMicWrapView extends ConstraintLayout {

    private AudioRoomMicStyle micStyle = AudioRoomMicStyle.NORMAL;
    protected ArrayList<AudioRoomMicModel> mDatas = new ArrayList<>();
    private BaseMicView mBaseMicView;
    private OnMicItemClickListener onMicItemClickListener;

    public AudioRoomMicWrapView(@NonNull Context context) {
        this(context, null);
    }

    public AudioRoomMicWrapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRoomMicWrapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        buildMicView();
    }

    private void buildMicView() {
        BaseMicView baseMicView = null;
        switch (micStyle) {
            case NORMAL:
                baseMicView = new AudioRoomMicView(getContext());
                break;
            case GAME:
                baseMicView = new AudioRoomGameMicView(getContext());
                break;
        }
        if (baseMicView == null) return;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.topToTop = LayoutParams.PARENT_ID;
        addView(baseMicView, params);
        mBaseMicView = baseMicView;
        baseMicView.setList(mDatas);
        baseMicView.setOnMicItemClickListener(onMicItemClickListener);
    }

    private void removeMicView() {
        View baseMicView = mBaseMicView;
        if (baseMicView != null) {
            removeView(baseMicView);
            mBaseMicView = null;
        }
    }

    public void setMicStyle(AudioRoomMicStyle style) {
        if (micStyle == style) {
            return;
        }
        micStyle = style;
        removeMicView();
        buildMicView();
    }

    public void setList(List<AudioRoomMicModel> list) {
        mDatas.clear();
        if (list != null) {
            mDatas.addAll(list);
        }
        BaseMicView baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.setList(list);
        }
    }

    public void startSoundLevel(int position) {
        BaseMicView baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.startSoundLevel(position);
        }
    }

    public void stopSoundLevel(int position) {
        BaseMicView baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.stopSoundLevel(position);
        }
    }

    public AudioRoomMicModel getItem(int position) {
        if (position >= 0 && position < mDatas.size()) {
            return mDatas.get(position);
        }
        return null;
    }

    public void setOnMicItemClickListener(OnMicItemClickListener listener) {
        onMicItemClickListener = listener;
        BaseMicView baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.setOnMicItemClickListener(listener);
        }
    }

    public void notifyItemChange(int micIndex, AudioRoomMicModel model) {
        if (micIndex >= 0 && micIndex < mDatas.size()) {
            mDatas.set(micIndex, model);
        }
        BaseMicView baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.notifyItemChange(micIndex, model);
        }
    }

    public enum AudioRoomMicStyle {
        NORMAL, // 1+8麦位样式
        GAME    // 游戏样式
    }

}
