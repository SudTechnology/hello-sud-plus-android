package tech.sud.mgp.hello.ui.scenes.crossroom.activity;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.room.model.PkStatus;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomGameMicView;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomMicView;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.BaseMicView;
import tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel.CrossRoomGameViewModel;
import tech.sud.mgp.hello.ui.scenes.crossroom.widget.RoomPkInfoView;

/**
 * 跨房互动类场景
 */
public class CrossRoomActivity extends BaseRoomActivity<CrossRoomGameViewModel> implements View.OnClickListener {

    private AbsAudioRoomActivity.AudioRoomMicStyle audioRoomMicStyle;
    private RoomPkInfoView roomPkInfoView;
    private TextView tvOpenPk;
    private TextView tvStartPk;
    private TextView tvRenewPk;
    private TextView tvPkSettings;
    private View viewContainerSelectGame;
    private View tvSelectGame;

    @Override
    protected CrossRoomGameViewModel initGameViewModel() {
        return new CrossRoomGameViewModel();
    }

    @Override
    protected boolean beforeSetContentView() {
        boolean isFinish = super.beforeSetContentView();
        if (roomInfoModel != null && roomInfoModel.roomPkModel != null) {
            roomInfoModel.roomPkModel.initInfo(roomInfoModel.roomId);
        }
        return isFinish;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cross_room;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        roomPkInfoView = findViewById(R.id.room_pk_info_view);
        viewContainerSelectGame = findViewById(R.id.container_select_game);
        tvSelectGame = findViewById(R.id.tv_select_game);

        roomConfig.isShowGameNumber = false; // 不显示游戏人数
        roomConfig.isShowASRTopHint = false; // 右上角不展示ASR提示

        int marginEnd = DensityUtils.dp2px(this, 12);
        int maxWidth = DensityUtils.dp2px(this, 90);
        int paddingHorizontal = DensityUtils.dp2px(this, 8);
        int textColor = ContextCompat.getColor(this, R.color.white);

        tvOpenPk = createTopTextView(maxWidth, paddingHorizontal, textColor, marginEnd);
        tvOpenPk.setText(R.string.open_room_pk);
        tvOpenPk.setBackgroundResource(R.drawable.shape_room_pk_btn_bg);
        topView.addCustomView(tvOpenPk, (LinearLayout.LayoutParams) tvOpenPk.getLayoutParams());

        tvStartPk = createTopTextView(maxWidth, paddingHorizontal, textColor, marginEnd);
        tvStartPk.setText(R.string.start_pk);
        tvStartPk.setBackgroundResource(R.drawable.shape_room_pk_btn_bg);
        topView.addCustomView(tvStartPk, (LinearLayout.LayoutParams) tvStartPk.getLayoutParams());

        tvRenewPk = createTopTextView(maxWidth, paddingHorizontal, textColor, marginEnd);
        tvRenewPk.setText(R.string.renew_pk);
        tvRenewPk.setBackgroundResource(R.drawable.shape_room_pk_btn_bg);
        topView.addCustomView(tvRenewPk, (LinearLayout.LayoutParams) tvRenewPk.getLayoutParams());

        tvPkSettings = createTopTextView(maxWidth, paddingHorizontal, textColor, marginEnd);
        tvPkSettings.setText(R.string.pk_settings);
        tvPkSettings.setBackgroundResource(R.drawable.shape_room_top_btn_bg);
        topView.addCustomView(tvPkSettings, (LinearLayout.LayoutParams) tvPkSettings.getLayoutParams());
    }

    private TextView createTopTextView(int maxWidth, int paddingHorizontal, int textColor, int marginEnd) {
        TextView tv = new TextView(this);
        tv.setMaxWidth(maxWidth);
        tv.setGravity(Gravity.CENTER);
        tv.setMaxLines(1);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        tv.setTextSize(12);
        tv.setTextColor(textColor);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                DensityUtils.dp2px(this, 20));
        params.setMarginEnd(marginEnd);
        tv.setLayoutParams(params);
        return tv;
    }

    @Override
    protected void initData() {
        super.initData();
        updatePkInfo();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvOpenPk.setOnClickListener(this);
        tvStartPk.setOnClickListener(this);
        tvRenewPk.setOnClickListener(this);
        tvPkSettings.setOnClickListener(this);
        tvSelectGame.setOnClickListener(this);
    }

    private void updatePkInfo() {
        roomPkInfoView.setRoomPkModel(roomInfoModel.roomPkModel);
        switch (getPkStatus()) {
            case PkStatus.MATCHING: // 匹配中
            case PkStatus.MATCHED: // 已匹配
                tvOpenPk.setVisibility(View.GONE);
                tvStartPk.setVisibility(View.VISIBLE);
                tvRenewPk.setVisibility(View.GONE);
                tvPkSettings.setVisibility(View.GONE);
                break;
            case PkStatus.STARTED: // 已开始
                tvOpenPk.setVisibility(View.GONE);
                tvStartPk.setVisibility(View.GONE);
                tvRenewPk.setVisibility(View.GONE);
                tvPkSettings.setVisibility(View.VISIBLE);
                break;
            case PkStatus.PK_END: // pk结束
                tvOpenPk.setVisibility(View.GONE);
                tvStartPk.setVisibility(View.GONE);
                tvRenewPk.setVisibility(View.VISIBLE);
                tvPkSettings.setVisibility(View.GONE);
                break;
            case PkStatus.MATCH_CLOSED: // 结束了匹配
                tvOpenPk.setVisibility(View.VISIBLE);
                tvStartPk.setVisibility(View.GONE);
                tvRenewPk.setVisibility(View.GONE);
                tvPkSettings.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /** 获取pk状态 */
    private int getPkStatus() {
        if (roomInfoModel.roomPkModel != null) {
            return roomInfoModel.roomPkModel.pkStatus;
        }
        return PkStatus.MATCH_CLOSED;
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        // 公屏处理
        if (playingGameId > 0) { // 玩着游戏
            chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.GAME);
        } else {
            chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.NORMAL);
        }
        // 麦位处理
        if (playingGameId > 0 || getPkStatus() != PkStatus.MATCH_CLOSED) {
            setMicStyle(AbsAudioRoomActivity.AudioRoomMicStyle.GAME);
        } else {
            setMicStyle(AbsAudioRoomActivity.AudioRoomMicStyle.NORMAL);
        }
        // 是否要提示其选择游戏
        if (playingGameId > 0 || getPkStatus() == PkStatus.MATCH_CLOSED) {
            viewContainerSelectGame.setVisibility(View.GONE);
        } else {
            viewContainerSelectGame.setVisibility(View.VISIBLE);
        }
    }

    private void setMicStyle(AbsAudioRoomActivity.AudioRoomMicStyle micStyle) {
        if (audioRoomMicStyle == micStyle) {
            return;
        }
        audioRoomMicStyle = micStyle;
        BaseMicView<?> baseMicView;
        switch (micStyle) {
            case NORMAL:
                baseMicView = new AudioRoomMicView(this);
                break;
            case GAME:
                baseMicView = new AudioRoomGameMicView(this);
                break;
            default:
                return;
        }
        micView.setMicView(baseMicView);
    }

    @Override
    public void onClick(View v) {
        if (v == tvOpenPk) {

        } else if (v == tvStartPk) {

        } else if (v == tvRenewPk) {

        } else if (v == tvPkSettings) {

        } else if (v == tvSelectGame) {
            clickSelectGame();
        }
    }

}