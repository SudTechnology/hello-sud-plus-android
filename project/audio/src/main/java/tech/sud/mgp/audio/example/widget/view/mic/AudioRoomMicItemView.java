package tech.sud.mgp.audio.example.widget.view.mic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.common.utils.ImageLoader;
import tech.sud.mgp.common.widget.view.SoundLevelView;
import tech.sud.mgp.common.widget.view.round.RoundedImageView;

public class AudioRoomMicItemView extends BaseMicItemView {

    private SoundLevelView mSoundLevelView;
    private RoundedImageView mRivIcon;
    private TextView mTvName;

    public AudioRoomMicItemView(@NonNull Context context) {
        super(context);
    }

    public AudioRoomMicItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioRoomMicItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.audio_view_room_mic_item, this);
        mSoundLevelView = findViewById(R.id.sound_level_view);
        mRivIcon = findViewById(R.id.riv_avatar);
        mTvName = findViewById(R.id.tv_name);
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
            mTvName.setText(R.string.audio_click_got_mic);
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
