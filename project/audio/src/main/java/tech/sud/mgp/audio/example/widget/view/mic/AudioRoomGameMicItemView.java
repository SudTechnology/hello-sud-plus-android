package tech.sud.mgp.audio.example.widget.view.mic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.common.utils.DensityUtils;
import tech.sud.mgp.common.utils.ImageLoader;
import tech.sud.mgp.common.utils.ShapeUtils;
import tech.sud.mgp.common.widget.view.SoundLevelView;
import tech.sud.mgp.common.widget.view.round.RoundedImageView;

public class AudioRoomGameMicItemView extends BaseMicItemView {

    private SoundLevelView mSoundLevelView;
    private RoundedImageView mRivIcon;
    private TextView mTvName;
    private View mViewCaptain;
    private TextView mTvState;

    public AudioRoomGameMicItemView(@NonNull Context context) {
        this(context, null);
    }

    public AudioRoomGameMicItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRoomGameMicItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.audio_view_room_game_mic_item, this);
        mSoundLevelView = findViewById(R.id.sound_level_view);
        mRivIcon = findViewById(R.id.riv_avatar);
        mTvName = findViewById(R.id.tv_name);
        mViewCaptain = findViewById(R.id.view_captain);
        mTvState = findViewById(R.id.tv_game_state);
    }

    @Override
    public void convert(int position, AudioRoomMicModel item) {
        boolean hasUser = item.userId > 0;

        mSoundLevelView.setCurUserId(item.userId);

        if (hasUser) {
            ImageLoader.loadAvatar(mRivIcon, item.avatar);
        } else {
            ImageLoader.loadDrawable(mRivIcon, R.drawable.audio_ic_seat);
        }

        if (hasUser) {
            mTvName.setText(item.nickName);
        } else {
            mTvName.setText("");
        }

        if (hasUser) {
            if (item.isCaptain != null && item.isCaptain) {
                mViewCaptain.setVisibility(View.VISIBLE);
            } else {
                mViewCaptain.setVisibility(View.GONE);
            }
        } else {
            mViewCaptain.setVisibility(View.GONE);
        }

        if (hasUser) {
            mTvState.setVisibility(View.GONE);
        } else {
            switch (item.readyStatus) {
                case 1: // 已准备
                    mTvState.setVisibility(View.VISIBLE);
                    mTvState.setText(R.string.audio_has_ready);
                    int strokeWidth = DensityUtils.dp2px(getContext(), 1);
                    mTvState.setBackground(ShapeUtils.createShape(strokeWidth, (float) strokeWidth,
                            GradientDrawable.RECTANGLE, Color.parseColor("#ffffff"),
                            Color.parseColor("#60cb6a")));
                    break;
                case 2: // 未准备
                    mTvState.setVisibility(View.VISIBLE);
                    mTvState.setText(R.string.audio_not_ready);
                    strokeWidth = DensityUtils.dp2px(getContext(), 1);
                    mTvState.setBackground(ShapeUtils.createShape(strokeWidth, (float) strokeWidth,
                            GradientDrawable.RECTANGLE, Color.parseColor("#ffffff"),
                            Color.parseColor("#f7782f")));
                    break;
                default:
                    mTvState.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public void startSoundLevel() {
        mSoundLevelView.start();
    }

    @Override
    public void stopSoundLevel() {
        mSoundLevelView.stop();
    }

}
