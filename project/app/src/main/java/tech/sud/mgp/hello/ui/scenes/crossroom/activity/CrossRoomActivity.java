package tech.sud.mgp.hello.ui.scenes.crossroom.activity;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.common.widget.view.MarqueeTextView;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.room.model.PkStatus;
import tech.sud.mgp.hello.service.room.resp.RoomPkModel;
import tech.sud.mgp.hello.service.room.resp.RoomPkRoomInfo;
import tech.sud.mgp.hello.ui.main.home.model.RoomItemModel;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomGameMicView;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomMicView;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.activity.BaseInteractionRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.model.ReportGameInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.BaseMicView;
import tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel.CrossRoomGameViewModel;
import tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel.CrossRoomViewModel;
import tech.sud.mgp.hello.ui.scenes.crossroom.widget.dialog.InvitePkDialog;
import tech.sud.mgp.hello.ui.scenes.crossroom.widget.dialog.PkRuleDialog;
import tech.sud.mgp.hello.ui.scenes.crossroom.widget.dialog.PkSettingsDialog;
import tech.sud.mgp.hello.ui.scenes.crossroom.widget.view.RoomPkInfoView;

/**
 * 跨房互动类场景
 */
public class CrossRoomActivity extends BaseInteractionRoomActivity<CrossRoomGameViewModel> implements View.OnClickListener {

    private AbsAudioRoomActivity.AudioRoomMicStyle audioRoomMicStyle;
    private RoomPkInfoView roomPkInfoView;
    private TextView tvOpenPk;
    private TextView tvStartPk;
    private TextView tvRenewPk;
    private TextView tvPkSettings;
    private View viewContainerSelectGame;
    private View tvSelectGame;
    private TextView tvSelectGameTitle;

    private final CrossRoomViewModel viewModel = new CrossRoomViewModel();

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
        roomConfig.isSupportAddRobot = true;
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
        tvSelectGameTitle = findViewById(R.id.tv_select_game_title);

        roomConfig.isShowGameNumber = false; // 不显示游戏人数
        roomConfig.isShowASRTopHint = false; // 右上角不展示ASR提示
        gameViewModel.gameConfigModel.ui.start_btn.custom = true; // 接管游戏的开始按钮事件
        gameViewModel.gameConfigModel.ui.lobby_game_setting.hide = true; // 隐藏玩法设置
        gameViewModel.gameConfigModel.ui.lobby_players.hide = false; // 展示玩家游戏位

        int marginEnd = DensityUtils.dp2px(this, 12);
        int maxWidth = DensityUtils.dp2px(this, 100);
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

        interactionBannerView.bringToFront();
        bringToFrontViews();
    }

    private TextView createTopTextView(int maxWidth, int paddingHorizontal, int textColor, int marginEnd) {
        MarqueeTextView tv = new MarqueeTextView(this);
        tv.setMaxWidth(maxWidth);
        tv.setGravity(Gravity.CENTER);
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
        setGameLiveListeners();
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
        roomPkInfoView.setPkRivalOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPkStatus() == PkStatus.MATCHING) {
                    if (roomInfoModel.roleType == RoleType.OWNER) {
                        showInviteDialog();
                    }
                } else {
                    if (roomInfoModel.roleType == RoleType.OWNER) {
                        intentRemovePkRival();
                    } else {
                        intentEnterPkRivalRoom();
                    }
                }
            }
        });
    }

    /** 意图移除pk对手 */
    private void intentRemovePkRival() {
        if (roomInfoModel.roomPkModel == null) return;
        RoomPkRoomInfo pkRival = roomInfoModel.roomPkModel.getPkRival();
        if (pkRival == null) return;
        SimpleChooseDialog dialog = new SimpleChooseDialog(this, getString(R.string.remove_rival_confirm),
                getString(R.string.cancel), getString(R.string.confirm_remove));
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                dialog.dismiss();
                if (index == 1) {
                    // 游戏中不可以移除
                    int gameState = gameViewModel.getGameState();
                    if (gameState == SudGIPMGState.MGCommonGameState.LOADING || gameState == SudGIPMGState.MGCommonGameState.PLAYING) {
                        ToastUtils.showShort(R.string.game_running_remove_warn);
                        return;
                    }
                    if (binder != null) {
                        binder.removePkRival();
                    }
                }
            }
        });
        dialog.show();
    }

    /** 意图进入pk对手的房间 */
    private void intentEnterPkRivalRoom() {
        if (roomInfoModel.roomPkModel == null) return;
        RoomPkRoomInfo pkRival = roomInfoModel.roomPkModel.getPkRival();
        if (pkRival == null) return;
        SimpleChooseDialog dialog = new SimpleChooseDialog(this, getString(R.string.enter_other_room_confirm),
                getString(R.string.cancel), getString(R.string.confirm_to));
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    enterPkRivalRoom(pkRival);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /** 进入pk对手的房间 */
    private void enterPkRivalRoom(RoomPkRoomInfo pkRival) {
        if (closeing) return;
        long delayDuration = delayExitRoom();
        ThreadUtils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                EnterRoomUtils.enterRoom(null, pkRival.roomId);
            }
        }, delayDuration + 200);
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

    /** 设置游戏相关的事件 */
    private void setGameLiveListeners() {
        gameViewModel.clickStartBtnLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                clickStartGame();
            }
        });
    }

    /** 点击了开始游戏 */
    private void clickStartGame() {
        ReportGameInfoModel reportGameInfoModel = new ReportGameInfoModel();
        if (roomInfoModel.roomPkModel != null) {
            reportGameInfoModel.pkId = roomInfoModel.roomPkModel.pkId;
        }
        reportGameInfoModel.sceneId = roomInfoModel.sceneType;
        gameViewModel.notifyAPPCommonSelfPlaying(true, SudJsonUtils.toJson(reportGameInfoModel), null);
    }

    private void updatePkInfo() {
        roomPkInfoView.setRoomPkModel(roomInfoModel.roomPkModel);
        switch (getPkStatus()) {
            case PkStatus.MATCHING: // 匹配中
            case PkStatus.MATCHED: // 已匹配
                topView.setSelectGameVisible(roomInfoModel.roleType == RoleType.OWNER);
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
                topView.setSelectGameVisible(roomInfoModel.roleType == RoleType.OWNER);
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
                topView.setSelectGameVisible(roomInfoModel.roleType == RoleType.OWNER);
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
                topView.setSelectGameVisible(false);
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
                topView.setSelectGameVisible(false);
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
    public String getGameRoomId() {
        return viewModel.getGameRoomId(roomInfoModel) + "";
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
        if (playingGameId > 0 || getPkStatus() == PkStatus.MATCH_CLOSED) {
            viewContainerSelectGame.setVisibility(View.GONE);
        } else {
            viewContainerSelectGame.setVisibility(View.VISIBLE);
            if (roomInfoModel.roleType == RoleType.OWNER) {
                tvSelectGame.setVisibility(View.VISIBLE);
                tvSelectGameTitle.setText(R.string.select_game_hint);
            } else {
                tvSelectGame.setVisibility(View.GONE);
                tvSelectGameTitle.setText(R.string.room_pk_wait_onwer_select);
            }
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
        if (v == tvOpenPk) { // 开启匹配
            clickStartMatch();
        } else if (v == tvStartPk) { // 开始pk
            clickStartPk();
        } else if (v == tvRenewPk) { // 重新开始pk
            clickStartPk();
        } else if (v == tvPkSettings) {
            clickPkSettings();
        } else if (v == tvSelectGame) {
            clickSelectGame();
        }
    }

    /** 点击了打开pk */
    private void clickStartMatch() {
        if (binder != null) {
            binder.roomPkSwitch(true);
        }
    }

    /** 点击pk设置 */
    private void clickPkSettings() {
        PkSettingsDialog dialog = new PkSettingsDialog();
        dialog.setSettingsMode(PkSettingsDialog.SettingsMode.CHANGE);
        dialog.setClosePkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentClosePk();
            }
        });
        if (roomInfoModel.roomPkModel != null) {
            dialog.setSelectedMinute(roomInfoModel.roomPkModel.totalMinute);
        }
        dialog.setOnSelectedListener(new PkSettingsDialog.OnSelectedListener() {
            @Override
            public void onSelected(int minute, PkSettingsDialog.SettingsMode mode) {
                if (binder != null) {
                    binder.roomPkSettings(minute);
                }
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    /** 点击了开始pk */
    private void clickStartPk() {
        RoomPkRoomInfo pkRival = getPkRival();
        if (pkRival == null) {
            ToastUtils.showShort(R.string.invite_pk_hint);
            showInviteDialog();
            return;
        }
        // 弹出设置pk时长
        PkSettingsDialog dialog = new PkSettingsDialog();
        dialog.setSettingsMode(PkSettingsDialog.SettingsMode.START);
        dialog.setClosePkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentClosePk();
            }
        });
        if (roomInfoModel.roomPkModel != null) {
            dialog.setSelectedMinute(roomInfoModel.roomPkModel.totalMinute);
        }
        dialog.setOnSelectedListener(new PkSettingsDialog.OnSelectedListener() {
            @Override
            public void onSelected(int minute, PkSettingsDialog.SettingsMode mode) {
                if (binder != null) {
                    binder.roomPkStart(minute);
                }
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    /** 意图关闭pk */
    private void intentClosePk() {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this, getString(R.string.room_close_pk_advance_tip),
                getString(R.string.cancel), getString(R.string.close));
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    if (binder != null) {
                        binder.roomPkSwitch(false);
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /** 获取pk对手 */
    private RoomPkRoomInfo getPkRival() {
        if (roomInfoModel.roomPkModel != null) {
            return roomInfoModel.roomPkModel.getPkRival();
        }
        return null;
    }

    @Override
    protected void onSelfSwitchGame(long gameId) {
        super.onSelfSwitchGame(gameId);
        if (binder != null) {
            binder.roomPkSyncGame();
        }
    }

    // region service回调
    @Override
    public void onEnterRoomSuccess() {
        super.onEnterRoomSuccess();
        if (binder != null) {
            // 在进房连接service、rtc这段时间，pk状态可能会产生变化
            binder.refreshRoomPkInfo();
        }
    }

    @Override
    public void onRoomPkUpdate() {
        super.onRoomPkUpdate();
        updatePkInfo();
        updatePageStyle();
    }

    @Override
    public void onRoomPkChangeGame(long gameId) {
        super.onRoomPkChangeGame(gameId);
        if (roomInfoModel.roleType == RoleType.OWNER) {
            ownerSwitchGame(gameId);
        }
    }

    @Override
    public void onRoomPkRemoveRival() {
        super.onRoomPkRemoveRival();
        switchGame(playingGameId);
    }

    @Override
    public void onRoomPkCoutndown() {
        super.onRoomPkCoutndown();
        RoomPkModel roomPkModel = roomInfoModel.roomPkModel;
        if (roomPkModel != null) {
            roomPkInfoView.setCountdown(roomPkModel.remainSecond);
        }
    }
    // endregion service回调

    protected void ownerSwitchGame(long gameId) {
        // 发送http通知后台
        GameRepository.switchGame(this, roomInfoModel.roomId, gameId, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (switchGame(gameId)) {
                    if (binder != null) {
                        binder.switchGame(gameId);
                    }
                }
            }
        });
    }

}