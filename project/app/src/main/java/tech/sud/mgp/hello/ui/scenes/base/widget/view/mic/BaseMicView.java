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

public abstract class BaseMicView<T extends BaseMicItemView> extends ConstraintLayout {

    protected ArrayList<AudioRoomMicModel> mDatas = new ArrayList<>();
    protected ArrayList<T> mItemViews = new ArrayList<>();
    protected OnMicItemClickListener mOnMicItemClickListener;

    public BaseMicView(@NonNull Context context) {
        this(context, null);
    }

    public BaseMicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseMicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setList(List<AudioRoomMicModel> list) {
        mDatas.clear();
        if (list != null) {
            mDatas.addAll(list);
        }
        notifyDataSetChanged();
    }

    protected void notifyDataSetChanged() {
        for (int i = 0; i < mItemViews.size(); i++) {
            BaseMicItemView micItemView = mItemViews.get(i);
            AudioRoomMicModel model = getModel(i);
            if (model == null) {
                micItemView.setVisibility(View.GONE);
            } else {
                micItemView.setVisibility(View.VISIBLE);
                micItemView.convert(i, model);
            }
        }
    }

    private AudioRoomMicModel getModel(int position) {
        if (position >= 0 && position < mDatas.size()) {
            return mDatas.get(position);
        }
        return null;
    }

    private BaseMicItemView getItemView(int position) {
        if (position >= 0 && position < mItemViews.size()) {
            return mItemViews.get(position);
        }
        return null;
    }

    public void setOnMicItemClickListener(OnMicItemClickListener listener) {
        mOnMicItemClickListener = listener;
    }

    public void startSoundLevel(int position) {
        BaseMicItemView itemView = getItemView(position);
        if (itemView != null) {
            itemView.startSoundLevel();
        }
    }

    public void stopSoundLevel(int position) {
        BaseMicItemView itemView = getItemView(position);
        if (itemView != null) {
            itemView.stopSoundLevel();
        }
    }

    public void notifyItemChange(int micIndex, AudioRoomMicModel model) {
        if (micIndex >= 0 && micIndex < mDatas.size()) {
            mDatas.set(micIndex, model);
        }
        BaseMicItemView itemView = getItemView(micIndex);
        if (itemView != null) {
            if (model == null) {
                itemView.setVisibility(View.GONE);
            } else {
                itemView.setVisibility(View.VISIBLE);
                itemView.convert(micIndex, model);
            }
        }
    }

    /** 收缩麦位 */
    protected void shirnkMicView() {
    }

    /** 展开麦位 */
    protected void spreadMicView() {
    }

    /** 播放麦位动画 */
    public void startAnim(int position, MicAnimModel model) {
        BaseMicItemView itemView = getItemView(position);
        if (itemView != null) {
            itemView.startAnim(model);
        }
    }

}
