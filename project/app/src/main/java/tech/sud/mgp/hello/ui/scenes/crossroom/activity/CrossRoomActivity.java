package tech.sud.mgp.hello.ui.scenes.crossroom.activity;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.room.model.PkStatus;
import tech.sud.mgp.hello.service.room.response.RoomPkRoomInfo;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.home.model.RoomItemModel;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomGameMicView;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomMicView;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.BaseMicView;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSendInviteModel;
import tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel.CrossRoomGameViewModel;
import tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel.CrossRoomViewModel;
import tech.sud.mgp.hello.ui.scenes.crossroom.widget.dialog.InvitePkDialog;
import tech.sud.mgp.hello.ui.scenes.crossroom.widget.dialog.PkRuleDialog;
import tech.sud.mgp.hello.ui.scenes.crossroom.widget.view.RoomPkInfoView;

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

    private final CrossRoomViewModel viewModel = new CrossRoomViewModel();
    private SimpleChooseDialog inviteAnswerDialog;

    @Override
    protected CrossRoomGameViewModel initGameViewModel() {
        return new CrossRoomGameViewModel();
    }

    @Override
    protected boolean beforeSetContentView() {
        boolean isFinish = super.beforeSetContentView();
        if (roomInfoModel != null && roomInfoModel.roomPkModel != null) {
            roomInfoModel.roomPkModel.totalMinute = APPConfig.ROOM_PK_MINUTE;
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
        setClickListeners();
        setLiveListeners();
    }

    private void setClickListeners() {
        ClickUtils.applySingleDebouncing(tvOpenPk, this);
        ClickUtils.applySingleDebouncing(tvStartPk, this);
        ClickUtils.applySingleDebouncing(tvRenewPk, this);
        ClickUtils.applySingleDebouncing(tvPkSettings, this);
        ClickUtils.applySingleDebouncing(tvSelectGame, this);
        roomPkInfoView.setIssueOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PkRuleDialog().show(getSupportFragmentManager(), null);
            }
        });
        roomPkInfoView.setInviteOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInviteDialog();
            }
        });
    }

    /** 展示邀请pk弹窗 */
    private void showInviteDialog() {
        InvitePkDialog dialog = new InvitePkDialog();
        dialog.setOnSelectedRoomListener(new InvitePkDialog.OnSelectedRoomListener() {
            @Override
            public void onSelected(RoomItemModel model) {
                if (binder != null) {
                    binder.roomPkInvite(model);
                }
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    /** 展示邀请应答弹窗 */
    private void showInviteAnswerDialog(RoomCmdPKSendInviteModel model) {
        if (inviteAnswerDialog != null && inviteAnswerDialog.isShowing()) return;
        // 显示dialog
        SimpleChooseDialog dialog = new SimpleChooseDialog(this, getString(R.string.invite_pk_answer_title, model.sendUser.name)
                , "", getString(R.string.accept));
        dialog.setCustomCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        // 倒计时
        CustomCountdownTimer countdownTimer = new CustomCountdownTimer(8) {
            @Override
            protected void onTick(int count) {
                String text = getString(R.string.refuse) + "(" + count + "s)";
                dialog.setLeftText(text);
            }

            @Override
            protected void onFinish() {
                dialog.dismiss();
            }
        };
        countdownTimer.start();
        // 监听
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (binder != null) {
                    if (index == 0) { // 拒绝
                        binder.roomPkAnswer(model, false);
                    } else if (index == 1) { // 接受
                        binder.roomPkAnswer(model, true);
                    }
                }
                dialog.dismiss();
            }
        });
        inviteAnswerDialog = dialog;
        inviteAnswerDialog.setOnDestroyListener(() -> {
            inviteAnswerDialog = null;
            countdownTimer.cancel();
        });
    }

    private void setLiveListeners() {
        viewModel.pkSwitchLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean pkSwitch) {
                if (pkSwitch == null) return;
                if (binder != null) {
                    binder.roomPkSwitch(pkSwitch);
                }
            }
        });
    }

    private void updatePkInfo() {
        roomPkInfoView.setRoomPkModel(roomInfoModel.roomPkModel);
        switch (getPkStatus()) {
            case PkStatus.MATCHING: // 匹配中
            case PkStatus.MATCHED: // 已匹配
                roomPkInfoView.setVisibility(View.VISIBLE);
                tvOpenPk.setVisibility(View.GONE);
                if (canOperatePk()) {
                    tvStartPk.setVisibility(View.VISIBLE);
                } else {
                    tvStartPk.setVisibility(View.GONE);
                }
                tvRenewPk.setVisibility(View.GONE);
                tvPkSettings.setVisibility(View.GONE);
                break;
            case PkStatus.STARTED: // 已开始
                roomPkInfoView.setVisibility(View.VISIBLE);
                tvOpenPk.setVisibility(View.GONE);
                tvStartPk.setVisibility(View.GONE);
                tvRenewPk.setVisibility(View.GONE);
                if (canOperatePk()) {
                    tvPkSettings.setVisibility(View.VISIBLE);
                } else {
                    tvPkSettings.setVisibility(View.GONE);
                }
                break;
            case PkStatus.PK_END: // pk结束
                roomPkInfoView.setVisibility(View.VISIBLE);
                tvOpenPk.setVisibility(View.GONE);
                tvStartPk.setVisibility(View.GONE);
                if (canOperatePk()) {
                    tvRenewPk.setVisibility(View.VISIBLE);
                } else {
                    tvRenewPk.setVisibility(View.GONE);
                }
                tvPkSettings.setVisibility(View.GONE);
                break;
            case PkStatus.MATCH_CLOSED: // 结束了匹配
                roomPkInfoView.setVisibility(View.GONE);
                if (canOperatePk()) {
                    tvOpenPk.setVisibility(View.VISIBLE);
                } else {
                    tvOpenPk.setVisibility(View.GONE);
                }
                tvStartPk.setVisibility(View.GONE);
                tvRenewPk.setVisibility(View.GONE);
                tvPkSettings.setVisibility(View.GONE);
                break;
            default:
                roomPkInfoView.setVisibility(View.VISIBLE);
                tvOpenPk.setVisibility(View.GONE);
                tvStartPk.setVisibility(View.GONE);
                tvRenewPk.setVisibility(View.GONE);
                tvPkSettings.setVisibility(View.GONE);
                break;
        }
    }

    /** 是否可以操作pk流程 */
    private boolean canOperatePk() {
        return roomInfoModel.roleType == RoleType.OWNER;
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
        // 公屏处理、麦位处理
        if (playingGameId > 0 || getPkStatus() != PkStatus.MATCH_CLOSED) { // 玩着游戏并且pk没有关闭
            setMicStyle(AbsAudioRoomActivity.AudioRoomMicStyle.GAME);
            chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.GAME);
        } else {
            setMicStyle(AbsAudioRoomActivity.AudioRoomMicStyle.NORMAL);
            chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.NORMAL);
        }
        // 是否要提示其选择游戏
        if (playingGameId > 0 || getPkStatus() == PkStatus.MATCH_CLOSED || roomInfoModel.roleType != RoleType.OWNER) {
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

    private long getRoomId() {
        return roomInfoModel.roomId;
    }

    @Override
    public void onClick(View v) {
        if (v == tvOpenPk) { // 开启匹配
            viewModel.roomPkSwitch(this, getRoomId(), true);
        } else if (v == tvStartPk) {
            clickStartPk();
        } else if (v == tvRenewPk) {

        } else if (v == tvPkSettings) {

        } else if (v == tvSelectGame) {
            clickSelectGame();
        }
    }

    /** 点击了开始pk */
    private void clickStartPk() {
        RoomPkRoomInfo pkRival = getPkRival();
        if (pkRival == null) {
            ToastUtils.showLong(R.string.invite_pk_hint);
            showInviteDialog();
            return;
        }
        if (binder != null) {
            binder.roomPkStart();
        }
    }

    /** 获取pk对手 */
    private RoomPkRoomInfo getPkRival() {
        if (roomInfoModel.roomPkModel != null) {
            return roomInfoModel.roomPkModel.getPkRival();
        }
        return null;
    }

    // region service回调
    @Override
    public void onRoomPkUpdate() {
        super.onRoomPkUpdate();
        updatePkInfo();
        updatePageStyle();
    }

    @Override
    public void onRoomPkInvite(RoomCmdPKSendInviteModel model) {
        super.onRoomPkInvite(model);
        if (model == null) return;
        LifecycleUtils.safeLifecycle(this, new LifecycleUtils.CompletedListener() {
            @Override
            public void onCompleted() {
                showInviteAnswerDialog(model);
            }
        });
    }
    // endregion service回调

}