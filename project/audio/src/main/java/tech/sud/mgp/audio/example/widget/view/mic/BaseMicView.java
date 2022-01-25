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

public abstract class BaseMicView extends ConstraintLayout {

    protected ArrayList<AudioRoomMicModel> mDatas = new ArrayList<>();
    protected ArrayList<BaseMicItemView> mItemViews = new ArrayList<>();
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

    private void notifyDataSetChanged() {
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

}
