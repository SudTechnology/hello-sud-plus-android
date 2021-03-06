package tech.sud.mgp.hello.ui.scenes.base.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.core.ISudListenerNotifyStateChange;
import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.AnimUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.common.utils.permission.PermissionFragment;
import tech.sud.mgp.hello.common.utils.permission.SudPermissionUtils;
import tech.sud.mgp.hello.common.widget.dialog.BottomOptionDialog;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.main.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.BooleanModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GameTextModel;
import tech.sud.mgp.hello.ui.scenes.base.model.OrderInviteModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomService;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.SceneRoomViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.dialog.GameModeDialog;
import tech.sud.mgp.hello.ui.scenes.base.widget.dialog.RoomMoreDialog;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.SceneRoomBottomView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.SceneRoomTopView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.RoomInputMsgView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.OnMicItemClickListener;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.SceneRoomMicWrapView;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.DanceModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdUserOrderModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.GiftSendClickListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftNotifyDetailModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.view.GiftEffectView;
import tech.sud.mgp.hello.ui.scenes.common.gift.view.RoomGiftDialog;
import tech.sud.mgp.rtc.audio.core.AudioPCMData;

/**
 * ?????????????????????
 */
public abstract class BaseRoomActivity<T extends AppGameViewModel> extends BaseActivity implements SceneRoomServiceCallback {

    protected RoomInfoModel roomInfoModel; // ????????????
    protected long playingGameId; // ????????????????????????id

    protected SceneRoomTopView topView;
    protected SceneRoomMicWrapView micView;
    protected SceneRoomChatView chatView;
    protected SceneRoomBottomView bottomView;
    protected FrameLayout giftContainer;
    protected GiftEffectView effectView;
    protected RoomInputMsgView inputMsgView;
    protected FrameLayout gameContainer;
    protected TextView tvGameNumber;
    protected View clOpenMic;
    private TextView tvOpenMic;
    private TextView tvASRHint;

    protected boolean closeing; // ??????????????????????????????
    protected SceneRoomService.MyBinder binder;
    protected final SceneRoomViewModel viewModel = new SceneRoomViewModel();
    protected final T gameViewModel = initGameViewModel();

    // ???????????????????????????
    protected abstract T initGameViewModel();

    private RoomGiftDialog roomGiftDialog;
    private GameModeDialog gameModeDialog;

    /** ??????????????????????????????????????????????????????????????? */
    protected final RoomConfig roomConfig = new RoomConfig();

    public long delayExitDuration = 500; // ?????????????????????

    @Override
    protected boolean beforeSetContentView() {
        Serializable modelSerializable = getIntent().getSerializableExtra("RoomInfoModel");
        if (modelSerializable instanceof RoomInfoModel) {
            roomInfoModel = (RoomInfoModel) modelSerializable;
        } else {
            boolean isPendingIntent = getIntent().getBooleanExtra(RequestKey.KEY_IS_PENDING_INTENT, false);
            if (isPendingIntent) {
                roomInfoModel = SceneRoomService.getRoomInfoModel();
            }
        }
        if (roomInfoModel == null || roomInfoModel.roomId == 0) {
            return true;
        }
        playingGameId = roomInfoModel.gameId;
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_scene;
    }

    @Override
    protected void setStatusBar() {
        updateStatusBar();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topView = findViewById(R.id.room_top_view);
        micView = findViewById(R.id.room_mic_view);
        chatView = findViewById(R.id.room_chat_view);
        bottomView = findViewById(R.id.room_bottom_view);
        giftContainer = findViewById(R.id.gift_container);
        inputMsgView = findViewById(R.id.room_input_msg_view);
        gameContainer = findViewById(R.id.game_container);
        tvGameNumber = findViewById(R.id.tv_game_number);
        clOpenMic = findViewById(R.id.cl_open_mic);
        tvOpenMic = findViewById(R.id.tv_open_mic);
        tvASRHint = findViewById(R.id.tv_asr_hint);
        clOpenMic.setVisibility(View.GONE);

        SudMGP.getCfg().setShowLoadingGameBg(true); // ?????????????????????????????????????????????
        gameViewModel.gameConfigModel.ui.lobby_players.hide = true; // ????????????????????????????????????

        // ????????????????????????????????????view?????????
        ViewGroup.LayoutParams topViewParams = topView.getLayoutParams();
        if (topViewParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) topViewParams;
            marginLayoutParams.topMargin = marginLayoutParams.topMargin + ImmersionBar.getStatusBarHeight(this);
            topView.setLayoutParams(marginLayoutParams);
        }

        topView.setFinishGameVisible(false);
        topView.setSelectGameVisible(roomInfoModel.roleType == RoleType.OWNER);
    }

    protected void bringToFrontViews() {
        giftContainer.bringToFront();
        inputMsgView.bringToFront();
        clOpenMic.bringToFront();
    }

    @Override
    protected void initData() {
        super.initData();
        topView.setName(roomInfoModel.roomName);
        topView.setId(getString(R.string.audio_room_number) + " " + roomInfoModel.roomNumber);
        viewModel.initData();
        initGame();
        bindService();
    }

    private void enterRoom() {
        if (binder != null) {
            binder.setCallback(this);
            binder.enterRoom(roomConfig, getClass(), roomInfoModel);
        }
        updatePageStyle();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        setViewListeners();
        setGameListeners();
    }

    private void setViewListeners() {
        KeyboardUtils.registerSoftInputChangedListener(this, new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChanged(int height) {
                inputMsgView.onSoftInputChanged(height);
                if (height == 0) {
                    updateStatusBar();
                }
            }
        });
        micView.setOnMicItemClickListener(new OnMicItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AudioRoomMicModel model = micView.getItem(position);
                if (model != null) {
                    if (model.userId == 0) {
                        if (binder != null) {
                            binder.micLocationSwitch(position, true, OperateMicType.USER);
                        }
                    } else if (model.userId == HSUserInfo.userId) {
                        clickSelfMicLocation(position);
                    } else if (model.userId > 0) {
                        clickOtherMicLocation(position, model);
                    }
                }
            }
        });
        bottomView.setGiftClickListener(v -> {
            showSendGiftDialog(null, 0);
        });
        bottomView.setGotMicClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binder != null) {
                    binder.autoUpMic(OperateMicType.USER);
                }
            }
        });
        bottomView.setInputClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.logSoftInputMode(getWindow().getAttributes().softInputMode);
                inputMsgView.show();
            }
        });
        bottomView.setMicStateClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean micOpen = !bottomView.isMicOpened();
                if (gameViewModel.isInterceptSwitchMic(micOpen)) {
                    ToastUtils.showLong(R.string.unable_to_speak);
                    return;
                }
                setMicStatus(micOpen);
            }
        });
        inputMsgView.setSendMsgListener(new RoomInputMsgView.SendMsgListener() {
            @Override
            public void onSendMsg(CharSequence msg) {
                if (binder != null) {
                    binder.sendPublicMsg(msg);
                }
                inputMsgView.hide();
                inputMsgView.clearInput();
            }
        });
        topView.setSelectGameClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSelectGame();
            }
        });
        topView.setMoreOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMore();
            }
        });
        topView.setFinishGameOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFinishGame();
            }
        });
    }

    /** ????????????????????? */
    protected void clickSelectGame() {
        GameModeDialog dialog = GameModeDialog.getInstance(roomInfoModel.sceneType);
        dialog.setFinishGame(gameViewModel.isOperateFinishGame());
        dialog.setPlayingGameId(playingGameId);
        dialog.show(getSupportFragmentManager(), null);
        dialog.setSelectGameListener(new GameModeDialog.SelectGameListener() {
            @Override
            public void onSelectGame(long gameId, boolean isFinishGame) {
                if (gameId == 0 && isFinishGame) { // ????????????
                    clickFinishGame();
                } else {
                    // ??????????????????????????????????????????????????????
                    int gameState = gameViewModel.getGameState();
                    if (gameState == SudMGPMGState.MGCommonGameState.LOADING || gameState == SudMGPMGState.MGCommonGameState.PLAYING) {
                        if (gameId > 0) {
                            ToastUtils.showLong(R.string.switch_game_warn);
                        } else {
                            ToastUtils.showLong(R.string.close_game_warn);
                        }
                        return;
                    }
                    intentSwitchGame(gameId);
                }
            }
        });
        dialog.setOnDestroyListener(() -> gameModeDialog = null);
        gameModeDialog = dialog;
    }

    /** ????????????????????? */
    protected void clickMore() {
        RoomMoreDialog dialog = RoomMoreDialog.getInstance(isGamePlaying());
        dialog.setHangOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                intentHang();
            }
        });
        dialog.setExitOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                intentClose();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    /** ?????????????????? */
    private void intentHang() {
        if (SudPermissionUtils.checkFloatPermission(this)) {
            hangRoom();
        } else {
            SimpleChooseDialog dialog = new SimpleChooseDialog(this,
                    getString(R.string.floating_permission_info),
                    getString(R.string.exit_room),
                    getString(R.string.go_setting));
            dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
                @Override
                public void onChoose(int index) {
                    if (index == 0) {
                        exitRoom();
                    } else if (index == 1) {
                        SudPermissionUtils.setFloatPermission(context);
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /** ???????????????????????? */
    private boolean isGamePlaying() {
        return playingGameId > 0;
    }

    /** ???????????? */
    private void hangRoom() {
        if (binder != null) {
            binder.showFloating(roomInfoModel, getClass());
        }
        releaseService();
        finish();
    }

    /**
     * ????????????????????????
     *
     * @param micOpen true?????????????????? false??????????????????
     */
    private void setMicStatus(boolean micOpen) {
        if (micOpen) {
            openMic();
        } else {
            closeMic();
        }
    }

    private void clickFinishGame() {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this, getString(R.string.finish_game_confirm));
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    gameViewModel.finishGame();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setGameListeners() {
        gameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) {
                    gameContainer.removeAllViews();
                } else {
                    gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });
        gameViewModel.gameMessageLiveData.observe(this, new Observer<GameTextModel>() {
            @Override
            public void onChanged(GameTextModel msg) {
                if (msg != null) {
                    if (binder != null) {
                        binder.addChatMsg(msg);
                    }
                }
            }
        });
        gameViewModel.updateMicLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object obj) {
                if (binder != null) {
                    binder.updateMicList();
                }
            }
        });
        gameViewModel.autoUpMicLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                businessAutoUpMic();
            }
        });
        gameViewModel.gameASRLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                onASRChanged(aBoolean);
            }
        });
        gameViewModel.gameRTCPublishLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }
                setMicStatus(aBoolean);
            }
        });
        gameViewModel.gameRTCPlayLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (binder != null) {
                    binder.setRTCPlay(aBoolean);
                }
            }
        });
        gameViewModel.showFinishGameBtnLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isShow) {
                boolean isOperateFinishGame = isShow != null && isShow;
                // ???????????????????????????????????????????????????????????????????????????
                if (roomInfoModel.roleType == RoleType.OWNER) {
                    topView.setFinishGameVisible(false);
                } else {
                    topView.setFinishGameVisible(isOperateFinishGame);
                }
                if (gameModeDialog != null) {
                    gameModeDialog.setFinishGame(isOperateFinishGame);
                }
            }
        });
        gameViewModel.playerInLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                updateGameNumber();
            }
        });
        gameViewModel.micSpaceMaxLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean spaceMax) {
                if (spaceMax) {
                    micView.shirnkMicView();
                }
            }
        });
        gameViewModel.autoJoinGameLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                autoJoinGame();
            }
        });
    }

    /** ???????????????????????? */
    protected void autoJoinGame() {
        gameViewModel.joinGame();
    }

    // asr??????????????????
    protected void onASRChanged(boolean open) {
        if (binder != null) {
            binder.setASROpen(open);
            if (open && !binder.isOpenedMic()) {
                clOpenMic.setVisibility(View.VISIBLE);
                clOpenMic.removeCallbacks(delayDismissTask);
                clOpenMic.postDelayed(delayDismissTask, 3000);
                AnimUtils.shakeVertical(clOpenMic);
                setOpenMicText();
            }
        }
    }

    /** ??????????????????????????? */
    private void setOpenMicText() {
        if (playingGameId == GameIdCons.I_GUESS_YOU_SAID) { //  ????????????
            tvOpenMic.setText(R.string.asr_guide_draw);
        } else if (playingGameId == GameIdCons.DIGITAL_BOMB) { // ????????????
            tvOpenMic.setText(R.string.asr_guide_number_boom);
        } else if (playingGameId == GameIdCons.YOU_DRAW_AND_I_GUESS) { // ????????????
            tvOpenMic.setText(R.string.asr_guide_draw);
        }
    }

    // ????????????ASR
    private boolean isSupportASR(long gameId) {
        if (gameId == GameIdCons.I_GUESS_YOU_SAID) return true;
        if (gameId == GameIdCons.DIGITAL_BOMB) return true;
        if (gameId == GameIdCons.YOU_DRAW_AND_I_GUESS) return true;
        return false;
    }

    // ????????????????????????
    private void clickSelfMicLocation(int position) {
        BottomOptionDialog dialog = new BottomOptionDialog(this);
        int downMicKey = 1;
        dialog.addOption(downMicKey, getString(R.string.audio_down_mic)); // ??????????????????
        dialog.setOnItemClickListener(new BottomOptionDialog.OnItemClickListener() {
            @Override
            public void onItemClick(BottomOptionDialog.BottomOptionModel model) {
                dialog.dismiss();
                if (model.key == downMicKey) {
                    //?????????????????????????????????????????????????????????
                    if (gameViewModel.playerIsPlaying(HSUserInfo.userId)) {
                        playingDownMic(position);
                    } else {
                        if (binder != null) {
                            binder.micLocationSwitch(position, false, OperateMicType.USER); // ????????????
                        }
                        gameViewModel.exitGame();
                    }
                }
            }
        });
        dialog.show();
    }

    // ???????????????????????????
    private void clickOtherMicLocation(int position, AudioRoomMicModel model) {
        long userId = model.userId;
        long selfUserId = HSUserInfo.userId;

        BottomOptionDialog dialog = new BottomOptionDialog(this);
        HashMap<Integer, String> options = new HashMap<>();

        // 1,???????????????
        // 2,????????????????????????app??????????????????????????????????????????????????????????????????????????????
        // 3,??????????????????????????????????????????????????????
        int transferCaptainKey = 1;
        if (playingGameId > 0 && gameViewModel.isCaptain(selfUserId) && gameViewModel.playerIsIn(userId)) {
            options.put(transferCaptainKey, getString(R.string.transfer_captain));
        }

        // 1,???????????????
        // 2,???????????????
        // 3,????????????????????????app??????????????????????????????????????????????????????????????????????????????
        // 4,?????????????????????????????????????????????????????????
        int kickGameKey = 2;
        if (playingGameId > 0
                && gameViewModel.getGameState() != SudMGPMGState.MGCommonGameState.PLAYING
                && gameViewModel.getGameState() != SudMGPMGState.MGCommonGameState.LOADING
                && gameViewModel.isCaptain(selfUserId)
                && gameViewModel.playerIsIn(userId)) {
            options.put(kickGameKey, getString(R.string.kick_game));
        }

        if (options.size() > 0) {
            for (Map.Entry<Integer, String> next : options.entrySet()) {
                dialog.addOption(next.getKey(), next.getValue()); // ??????????????????
            }
        } else {
            return;
        }

        dialog.setOnItemClickListener(new BottomOptionDialog.OnItemClickListener() {
            @Override
            public void onItemClick(BottomOptionDialog.BottomOptionModel model) {
                dialog.dismiss();
                if (model.key == transferCaptainKey) {
                    gameViewModel.notifyAPPCommonSelfCaptain(userId + "");
                } else if (model.key == kickGameKey) {
                    gameViewModel.notifyAPPCommonSelfKick(userId + "");
                }
            }
        });
        dialog.show();
    }

    /**
     * ???????????????????????????
     */
    private void playingDownMic(int position) {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this,
                getString(R.string.playing_down_mic),
                getString(R.string.audio_cancle),
                getString(R.string.confirm_));
        dialog.show();
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    if (binder != null) {
                        binder.micLocationSwitch(position, false, OperateMicType.USER); // ????????????
                    }
                    gameViewModel.exitGame();
                }
                dialog.dismiss();
            }
        });
    }

    /** ?????????????????? */
    private void intentClose() {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this,
                getString(R.string.audio_close_room_title),
                getString(R.string.audio_cancle),
                getString(R.string.confirm));
        dialog.show();
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    delayExitRoom();
                } else {
                    dialog.dismiss();
                }
            }
        });
    }

    // ??????????????????
    public long delayExitRoom() {
        if (closeing) return 0;
        closeing = true;
        if (playingGameId > 0) {
            // ?????????????????????
            gameViewModel.exitGame();
            topView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitRoom();
                }
            }, delayExitDuration);
            return delayExitDuration;
        } else {
            exitRoom();
            return 0;
        }
    }

    // ????????????
    private void exitRoom() {
        if (binder != null) {
            binder.exitRoom();
        }
        releaseService();
        finish();
    }

    @Override
    public void onBackPressed() {
        intentHang();
    }

    /**
     * ????????????????????????
     *
     * @param gameId ??????id
     */
    protected void intentSwitchGame(long gameId) {
        // ??????http????????????
        GameRepository.switchGame(this, roomInfoModel.roomId, gameId, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (switchGame(gameId)) {
                    onSelfSwitchGame(gameId);
                }
            }
        });
    }

    /** ??????????????????????????? */
    protected void onSelfSwitchGame(long gameId) {
        if (binder != null) {
            binder.switchGame(gameId);
        }
    }

    protected void updateGameNumber() {
        long gameId = playingGameId;
        if (!roomConfig.isSudGame || gameId <= 0 || !roomConfig.isShowGameNumber) {
            tvGameNumber.setText("");
            return;
        }
        int gameMaxNumber = viewModel.getGameMaxNumber(gameId);
        int playerInNumber = gameViewModel.getPlayerInNumber();
        String numberStr = getString(R.string.game_number) + "???" + playerInNumber + "/" + gameMaxNumber;
        tvGameNumber.setText(numberStr);
    }

    private void openMic() {
        SudPermissionUtils.requirePermission(this, getSupportFragmentManager(),
                new String[]{Manifest.permission.RECORD_AUDIO},
                new PermissionFragment.OnPermissionListener() {
                    @Override
                    public void onPermission(boolean success) {
                        if (success) {
                            if (binder != null) {
                                binder.setMicState(true);
                                boolean asrIsOpen = gameViewModel.gameASRLiveData.getValue() != null && gameViewModel.gameASRLiveData.getValue();
                                binder.setASROpen(asrIsOpen);
                            }
                        }
                    }
                });
    }

    private void closeMic() {
        if (binder != null) {
            binder.setMicState(false);
        }
    }

    /** ????????????????????????????????????????????? */
    protected List<AudioRoomMicModel> getGiftDialogMicList() {
        if (binder != null) {
            return binder.getMicList();
        }
        return null;
    }

    /**
     * 1.????????????????????????
     * underUser:?????????????????????
     * index:???0
     * 2.??????????????????
     * underUser:null
     * index:????????????????????????????????????
     */
    private void showSendGiftDialog(UserInfo underUser, int index) {
        roomGiftDialog = RoomGiftDialog.newInstance(roomInfoModel.sceneType, roomInfoModel.gameId);
        if (underUser == null) {
            roomGiftDialog.setMicUsers(getGiftDialogMicList(), index);
        } else {
            roomGiftDialog.setToUser(underUser);
        }
        roomGiftDialog.giftSendClickListener = new GiftSendClickListener() {
            @Override
            public void onSendClick(GiftModel giftModel, int giftCount, List<UserInfo> toUsers) {
                onSendGift(giftModel, giftCount, toUsers);
            }

            @Override
            public void onNotify(Map<Long, Boolean> userState) {
                if (binder != null) {
                    binder.updateMicList();
                }
            }
        };
        roomGiftDialog.show(getSupportFragmentManager(), "gift");
        roomGiftDialog.setOnDestroyListener(new BaseDialogFragment.OnDestroyListener() {
            @Override
            public void onDestroy() {
                roomGiftDialog = null;
                if (binder != null) {
                    binder.updateMicList();
                }
            }
        });
    }

    protected void onSendGift(GiftModel giftModel, int giftCount, List<UserInfo> toUsers) {
        if (toUsers == null || toUsers.size() == 0) {
            return;
        }
        BooleanModel isShowGift = new BooleanModel();
        isShowGift.value = true;
        for (UserInfo user : toUsers) {
            // ??????http?????????
            int giftConfigType; // ?????????????????????1???????????????2???????????????
            if (giftModel.type == 0) { // 1.4.0??????:???????????? 0??????????????? 1?????????????????????
                giftConfigType = 1;
            } else {
                giftConfigType = 2;
            }
            RoomRepository.sendGift(context, roomInfoModel.roomId, giftModel.giftId, giftCount,
                    giftConfigType, giftModel.giftPrice, new RxCallback<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            super.onSuccess(o);
                            if (binder != null) {
                                binder.sendGift(giftModel, giftCount, user);
                            }
                            if (isShowGift.value) { // ?????????????????????
                                isShowGift.value = false;
                                showGift(giftModel);
                            }
                        }
                    });
        }
    }

    /** ???????????? */
    protected void showGift(GiftModel giftModel) {
        if (!canShowGift()) {
            return;
        }
        if (effectView == null) {
            effectView = new GiftEffectView(this);
            effectView.addLifecycleObserver(this);
            giftContainer.addView(effectView);
        }
        effectView.showEffect(giftModel);
    }

    /** ?????????????????????????????? */
    protected boolean canShowGift() {
        return true;
    }

    private void initGame() {
        if (roomConfig.isSudGame) {
            gameViewModel.switchGame(this, getGameRoomId(), roomInfoModel.gameId);
        }
        updateGameNumber();
    }

    /** ?????????????????????id */
    protected long getGameRoomId() {
        return roomInfoModel.roomId;
    }

    // ???????????????????????????????????????
    protected void updatePageStyle() {
        if (isSupportASR(playingGameId) && roomConfig.isShowASRTopHint) {
            tvASRHint.setVisibility(View.VISIBLE);
        } else {
            tvASRHint.setVisibility(View.GONE);
        }
    }

    protected void updateStatusBar() {
        if (roomConfig.isSudGame && roomInfoModel != null && roomInfoModel.gameId > 0) { // ????????????
            ImmersionBar.with(this).statusBarColor(R.color.transparent).fullScreen(true).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.transparent).fullScreen(false).hideBar(BarHide.FLAG_SHOW_BAR).init();
        }
    }

    /**
     * ??????????????????
     */
    private void bindService() {
        Intent intent = new Intent(this, SceneRoomService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //Android8????????????????????????????????????
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (SceneRoomService.MyBinder) service;
            binder.dismissFloating();
            enterRoom();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // ?????????????????????????????????????????????????????????activity ???????????????stop?????????????????????????????????????????????????????????????????????????????????
            finish();
        }
    };


    // region service??????
    @Override
    public void onEnterRoomSuccess() {
        businessAutoUpMic();
    }

    /** ?????????????????? */
    protected void businessAutoUpMic() {
        if (binder != null) {
            binder.autoUpMic(OperateMicType.USER);
        }
    }

    @Override
    public void onMicList(List<AudioRoomMicModel> list) {
        micView.setList(list);
        if (roomGiftDialog != null) {
            roomGiftDialog.updateMicUsers(getGiftDialogMicList());
        }
    }

    @Override
    public void notifyMicItemChange(int micIndex, AudioRoomMicModel model) {
        micView.notifyItemChange(micIndex, model);
        updateGiftDialogMicUsers(model);
    }

    /** ??????????????????????????????????????? */
    protected void updateGiftDialogMicUsers(AudioRoomMicModel model) {
        if (roomGiftDialog != null) {
            roomGiftDialog.updateOneMicUsers(model);
        }
    }

    @Override
    public void selfMicIndex(int micIndex) {
        if (micIndex >= 0) {
            bottomView.hideGotMic();
            bottomView.showMicState();
        } else {
            bottomView.showGotMic();
            bottomView.hideMicState();
        }
        gameViewModel.selfMicIndex(micIndex);
    }

    @Override
    public void addPublicMsg(Object msg) {
        chatView.addMsg(msg);
    }

    @Override
    public void onChatList(List<Object> list) {
        chatView.setList(list);
    }

    @Override
    public void sendGiftsNotify(GiftNotifyDetailModel notify) {
        showGift(notify.gift);
    }

    @Override
    public void onMicStateChanged(boolean isOpened) {
        bottomView.setMicOpened(isOpened);
    }

    @Override
    public void onSoundLevel(int micIndex) {
        micView.startSoundLevel(micIndex);
    }

    @Override
    public void onRoomOnlineUserCountUpdate(int count) {
        topView.setNumber(count + "");
    }

    @Override
    public void onGameChange(long gameId) {
        switchGame(gameId);
    }

    protected boolean switchGame(long gameId) {
        if (!roomConfig.isSudGame) {
            return false;
        }
        if (playingGameId == gameId && getGameRoomId() == gameViewModel.getGameRoomId()) {
            return false;
        }
        playingGameId = gameId;
        roomInfoModel.gameId = gameId;
        gameViewModel.switchGame(this, getGameRoomId(), gameId);
        updatePageStyle();
        updateStatusBar();
        updateGameNumber();
        if (binder != null) {
            binder.updateMicList();
        }
        return true;
    }

    @Override
    public void onWrapMicModel(AudioRoomMicModel model) {
        gameViewModel.wrapMicModel(model);
        if (roomGiftDialog != null) {
            roomGiftDialog.setGiftEnable(model);
        } else {
            model.giftEnable = false;
        }
    }

    @Override
    public void onCapturedAudioData(AudioPCMData audioPCMData) {
        gameViewModel.onCapturedAudioData(audioPCMData);
    }

    @Override
    public void onSelfSendMsg(String msg) {
        gameViewModel.sendMsgCompleted(msg);
    }

    @Override
    public void onMicLocationSwitchCompleted(int micIndex, boolean operate, OperateMicType type) {
        if (operate) {
            // ??????????????????????????????????????????
            // ?????????????????????????????????????????????????????????????????????????????????mg_common_player_in??????mg_common_self_microphone
            // app????????????????????????????????????????????????????????????????????????????????????????????????????????????streamId
            // ?????????????????????????????????????????????????????????????????????????????????
            if (type == OperateMicType.GAME_AUTO) {
                Boolean isPublish = gameViewModel.gameRTCPublishLiveData.getValue();
                if (isPublish != null && isPublish) {
                    openMic();
                }
            }
        }
    }

    @Override
    public void onOrderInvite(RoomCmdUserOrderModel model) {
    }

    @Override
    public void onOrderInviteAnswered(OrderInviteModel model) {
    }

    @Override
    public void onOrderOperate(long orderId, long gameId, String gameName, String userId, String userName, boolean operate) {
    }

    @Override
    public void onReceiveInvite(boolean agreeState) {
    }

    @Override
    public void onRoomPkUpdate() {
    }

    @Override
    public void onRoomPkChangeGame(long gameId) {
    }

    @Override
    public void onRoomPkRemoveRival() {
    }

    @Override
    public void onRoomPkCoutndown() {
    }

    @Override
    public void onRecoverCompleted() {
    }

    @Override
    public void notifyStateChange(String state, String dataJson, ISudListenerNotifyStateChange listener) {
        gameViewModel.notifyStateChange(state, dataJson, listener);
    }

    @Override
    public void onDanceList(List<DanceModel> list) {
    }

    @Override
    public void onUpdateDance(int index) {
    }

    @Override
    public void onDanceWait() {
    }

    @Override
    public void onDiscoContribution(List<ContributionModel> list) {
    }

    @Override
    public void onDJCountdown(int countdown) {
    }
    // endregion service??????

    @Override
    protected void onStart() {
        super.onStart();
        gameViewModel.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameViewModel.onResume();
        updateStatusBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameViewModel.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameViewModel.onStop();
    }

    // ?????????????????????
    protected void releaseService() {
        if (binder != null) {
            binder.removeCallback(this);
            unbindService(serviceConnection);
            binder = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseService();
        if (effectView != null) {
            effectView.onDestory();
        }
        gameViewModel.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateStatusBar();
        }
    }

    private final Runnable delayDismissTask = new Runnable() {
        @Override
        public void run() {
            Animation animation = clOpenMic.getAnimation();
            if (animation != null) {
                animation.cancel();
            }
            clOpenMic.setVisibility(View.GONE);
        }
    };

}
