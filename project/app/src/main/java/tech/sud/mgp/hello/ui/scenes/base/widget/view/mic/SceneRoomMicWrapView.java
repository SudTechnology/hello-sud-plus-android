package tech.sud.mgp.hello.ui.scenes.base.widget.view.mic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.MicAnimModel;

/**
 * 场景房间顶层的麦位View，管理切换不同形态
 */
public class SceneRoomMicWrapView extends ConstraintLayout {

    protected ArrayList<AudioRoomMicModel> mDatas = new ArrayList<>();
    private BaseMicView<?> mBaseMicView;
    private OnMicItemClickListener onMicItemClickListener;

    public SceneRoomMicWrapView(@NonNull Context context) {
        this(context, null);
    }

    public SceneRoomMicWrapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SceneRoomMicWrapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void addBaseMicView(BaseMicView<?> baseMicView) {
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

    /**
     * 设置麦位的View
     *
     * @param baseMicView 麦位View
     */
    public void setMicView(BaseMicView<?> baseMicView) {
        if (mBaseMicView == baseMicView) {
            return;
        }
        removeMicView();
        addBaseMicView(baseMicView);
    }

    /** 设置数据集 */
    public void setList(List<AudioRoomMicModel> list) {
        mDatas.clear();
        if (list != null) {
            mDatas.addAll(list);
        }
        BaseMicView<?> baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.setList(list);
        }
    }

    /** 启动声浪显示 */
    public void startSoundLevel(int position) {
        BaseMicView<?> baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.startSoundLevel(position);
        }
    }

    /** 停止声浪显示 */
    public void stopSoundLevel(int position) {
        BaseMicView<?> baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.stopSoundLevel(position);
        }
    }

    /** 播放动画 */
    public void startAnim(int position, MicAnimModel model) {
        BaseMicView<?> baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.startAnim(position, model);
        }
    }

    /** 获取某一条数据 */
    public AudioRoomMicModel getItem(int position) {
        if (position >= 0 && position < mDatas.size()) {
            return mDatas.get(position);
        }
        return null;
    }

    /** 设置item点击监听 */
    public void setOnMicItemClickListener(OnMicItemClickListener listener) {
        onMicItemClickListener = listener;
        BaseMicView<?> baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.setOnMicItemClickListener(listener);
        }
    }

    /** 更新某一条数据 */
    public void notifyItemChange(int micIndex, AudioRoomMicModel model) {
        if (micIndex >= 0 && micIndex < mDatas.size()) {
            mDatas.set(micIndex, model);
        }
        BaseMicView<?> baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.notifyItemChange(micIndex, model);
        }
    }

    /** 收缩麦位 */
    public void shirnkMicView() {
        BaseMicView<?> baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.shirnkMicView();
        }
    }

    /** 展开麦位 */
    public void spreadMicView() {
        BaseMicView<?> baseMicView = mBaseMicView;
        if (baseMicView != null) {
            baseMicView.spreadMicView();
        }
    }

}
