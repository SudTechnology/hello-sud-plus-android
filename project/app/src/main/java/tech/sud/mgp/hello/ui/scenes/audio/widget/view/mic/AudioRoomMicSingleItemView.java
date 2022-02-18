package tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.widget.view.SoundLevelView;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;
import tech.sud.mgp.hello.ui.scenes.audio.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.audio.model.RoleType;

public class AudioRoomMicSingleItemView extends BaseMicItemView {

    private SoundLevelView mSoundLevelView;
    private RoundedImageView mRivIcon;
    private TextView mTvName;
    private ImageView giftIcon;

    public AudioRoomMicSingleItemView(@NonNull Context context) {
        this(context, null);
    }

    public AudioRoomMicSingleItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRoomMicSingleItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_room_mic_single_item, this);
        mSoundLevelView = findViewById(R.id.sound_level_view);
        mRivIcon = findViewById(R.id.riv_avatar);
        mTvName = findViewById(R.id.tv_name);
        giftIcon = findViewById(R.id.gift_icon_iv);
    }

    @Override
    public void convert(int position, AudioRoomMicModel item) {
        boolean hasUser = item.userId > 0;
        mSoundLevelView.setCurUserId(item.userId);
        if (hasUser) {
            ImageLoader.loadAvatar(mRivIcon, item.avatar);
        } else {
            ImageLoader.loadDrawable(mRivIcon, R.drawable.ic_seat);
        }
        if (hasUser) {
            if (item.roleType == RoleType.OWNER) { // 房主标识
                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append("* ");
                sb.setSpan(new ImageSpan(getContext(), R.drawable.ic_room_owner), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (item.nickName != null) {
                    sb.append(item.nickName);
                }
                mTvName.setText(sb);
            } else {
                mTvName.setText(item.nickName);
            }
        } else {
            mTvName.setText(R.string.audio_click_got_mic);
        }
        if (hasUser && item.giftEnable) {
            giftIcon.setVisibility(View.VISIBLE);
        } else {
            giftIcon.setVisibility(View.GONE);
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
