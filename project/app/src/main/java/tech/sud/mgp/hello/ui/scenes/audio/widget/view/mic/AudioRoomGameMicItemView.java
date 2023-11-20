package tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.common.utils.WebPUtils;
import tech.sud.mgp.hello.common.widget.view.SoundLevelView;
import tech.sud.mgp.hello.ui.scenes.audio3d.model.EmojiModel;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.MicAnimModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.BaseMicItemView;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.PlayResultListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.PlayResult;

/**
 * 语聊房，游戏时显示的单个麦位
 */
public class AudioRoomGameMicItemView extends BaseMicItemView {

    private SoundLevelView soundLevelView;
    private ImageView ivIcon;
    private TextView tvName;
    private View viewCaptain;
    private TextView tvState;
    private View viewPlayingGame;
    private FrameLayout mContainerAnim;
    private ImageView mIvAnim;
    private long mUserId;
    private AudioRoomGameMicView.GameMicMode micMode;

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
        inflate(getContext(), R.layout.view_room_game_mic_item, this);
        soundLevelView = findViewById(R.id.sound_level_view);
        ivIcon = findViewById(R.id.riv_avatar);
        tvName = findViewById(R.id.tv_name);
        viewCaptain = findViewById(R.id.view_captain);
        tvState = findViewById(R.id.tv_game_state);
        viewPlayingGame = findViewById(R.id.view_playing_game);
        mContainerAnim = findViewById(R.id.container_anim);
        initIvAnim();
    }

    private void initIvAnim() {
        mIvAnim = new AppCompatImageView(getContext());
//        mIvAnim.setScaleType(ImageView.ScaleType.CENTER);
        mContainerAnim.addView(mIvAnim, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public void convert(int position, AudioRoomMicModel item) {
        if (mUserId != item.userId) {
            mUserId = item.userId;
            mIvAnim.setImageBitmap(null);
        }

        // 给View设置数据
        if (micMode != null && micMode.isShirnk) {
            shirnkConvert(position, item);
        } else {
            normalConvert(position, item);
        }
    }

    /** 缩放模式下的数据设置 */
    private void shirnkConvert(int position, AudioRoomMicModel item) {
        boolean hasUser = item.userId > 0;
        // 声浪
        soundLevelView.setCurUserId(item.userId);

        // 头像
        if (hasUser) {
            ImageLoader.loadAvatar(ivIcon, item.avatar);
        } else {
            ImageLoader.loadDrawable(ivIcon, R.drawable.ic_seat);
        }

        tvName.setVisibility(View.GONE);
        viewCaptain.setVisibility(View.GONE);
        tvState.setVisibility(View.GONE);
        viewPlayingGame.setVisibility(View.GONE);
    }

    private void normalConvert(int position, AudioRoomMicModel item) {
        boolean hasUser = item.userId > 0;
        tvName.setVisibility(View.VISIBLE);
        // 昵称
        if (hasUser) {
            tvName.setText(item.nickName);
        } else {
            tvName.setText("");
        }

        // 声浪
        soundLevelView.setCurUserId(item.userId);

        // 头像
        if (hasUser) {
            ImageLoader.loadAvatar(ivIcon, item.avatar);
        } else {
            ImageLoader.loadDrawable(ivIcon, R.drawable.ic_seat);
        }

        // 队长标识
        if (hasUser) {
            if (item.isCaptain != null && item.isCaptain) {
                viewCaptain.setVisibility(View.VISIBLE);
            } else {
                viewCaptain.setVisibility(View.GONE);
            }
        } else {
            viewCaptain.setVisibility(View.GONE);
        }

        // 准备状态
        if (hasUser) {
            switch (item.readyStatus) {
                case 1: // 已准备
                    tvState.setVisibility(View.VISIBLE);
                    tvState.setText(R.string.audio_has_ready);
                    int strokeWidth = DensityUtils.dp2px(getContext(), 0.5f);
                    tvState.setBackground(ShapeUtils.createShape(strokeWidth, (float) strokeWidth, null,
                            GradientDrawable.RECTANGLE, Color.parseColor("#ffffff"),
                            Color.parseColor("#60cb6a")));
                    break;
                case 2: // 未准备
                    tvState.setVisibility(View.VISIBLE);
                    tvState.setText(R.string.audio_not_ready);
                    strokeWidth = DensityUtils.dp2px(getContext(), 0.5f);
                    tvState.setBackground(ShapeUtils.createShape(strokeWidth, (float) strokeWidth, null,
                            GradientDrawable.RECTANGLE, Color.parseColor("#ffffff"),
                            Color.parseColor("#f7782f")));
                    break;
                default:
                    tvState.setVisibility(View.GONE);
                    break;
            }
        } else {
            tvState.setVisibility(View.GONE);
        }

        // 是否正在游戏中
        if (item.isPlayingGame) {
            viewPlayingGame.setVisibility(View.VISIBLE);
        } else {
            viewPlayingGame.setVisibility(View.GONE);
        }
    }

    @Override
    public void startSoundLevel() {
        soundLevelView.start();
    }

    @Override
    public void stopSoundLevel() {
        soundLevelView.stop();
    }

    @Override
    public void startAnim(MicAnimModel model) {
        if (model == null) {
            return;
        }
        if (model.animModel instanceof EmojiModel) {
            EmojiModel emojiModel = (EmojiModel) model.animModel;
            if (emojiModel.type == 1) { // 爆灯
                WebPUtils.loadWebP(mIvAnim, R.raw.bao_deng, 3, new PlayResultListener() {
                    @Override
                    public void onResult(PlayResult result) {
                        if (result == PlayResult.PLAYEND) {
                            mIvAnim.setImageBitmap(null);
                        }
                    }
                });
            }
        }
    }

    public void setMicMode(AudioRoomGameMicView.GameMicMode micMode) {
        this.micMode = micMode;
    }

    public void changeSize() {
        if (micMode.isShirnk) {
            setShirnkSize();
        } else {
            setSpreadSize();
        }
    }

    private void setShirnkSize() {
        ViewUtils.setSize(soundLevelView, DensityUtils.dp2px(getContext(), 43));
        ViewUtils.setMarginTop(ivIcon, DensityUtils.dp2px(getContext(), 16));
    }

    private void setSpreadSize() {
        ViewUtils.setSize(soundLevelView, DensityUtils.dp2px(getContext(), 48));
        ViewUtils.setMarginTop(ivIcon, DensityUtils.dp2px(getContext(), 10));
    }

}
