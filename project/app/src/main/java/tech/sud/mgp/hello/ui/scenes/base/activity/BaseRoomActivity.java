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
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState.AIPlayers;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.core.ISudListenerNotifyStateChange;
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
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.RobotListResp;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.main.base.activity.MainActivity;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.BooleanModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GameLoadingProgressModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GameTextModel;
import tech.sud.mgp.hello.ui.scenes.base.model.OrderInviteModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomService;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.base.utils.AIPlayersConverter;
import tech.sud.mgp.hello.ui.scenes.base.utils.UserInfoRespConverter;
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
 * 场景房间的基类
 */
public abstract class BaseRoomActivity<T extends AppGameViewModel> extends BaseActivity implements SceneRoomServiceCallback {

    protected RoomInfoModel roomInfoModel; // 房间信息
    protected long playingGameId; // 当前正在玩的游戏id

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
    private TextView tvAddRobot;

    protected boolean closeing; // 标识是否正在关闭房间
    protected SceneRoomService.MyBinder binder;
    protected final SceneRoomViewModel viewModel = new SceneRoomViewModel();
    protected final T gameViewModel = initGameViewModel();

    // 初始化游戏业务模型
    protected abstract T initGameViewModel();

    private RoomGiftDialog roomGiftDialog;
    private GameModeDialog gameModeDialog;

    /** 场景配置，子类可对其进行修改进行定制化需求 */
    protected final RoomConfig roomConfig = new RoomConfig();

    public long delayExitDuration = 500; // 延时关闭的时长

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
        tvAddRobot = findViewById(R.id.tv_add_robot);

        clOpenMic.setVisibility(View.GONE);

        gameViewModel.gameConfigModel.ui.lobby_players.hide = true; // 配置不展示大厅玩家展示位
        gameViewModel.gameConfigModel.ui.nft_avatar.hide = false; // 显示NFT图像
        gameViewModel.gameConfigModel.ui.game_opening.hide = false; // 显示开场动画
        gameViewModel.gameConfigModel.ui.game_mvp.hide = false; // 显示MVP动画

        // 设置沉浸式状态栏时，顶部view的间距
        ViewGroup.LayoutParams topViewParams = topView.getLayoutParams();
        if (topViewParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) topViewParams;
            marginLayoutParams.topMargin = marginLayoutParams.topMargin + ImmersionBar.getStatusBarHeight(this);
            topView.setLayoutParams(marginLayoutParams);
        }

        topView.setFinishGameVisible(false);
        topView.setSelectGameVisible(roomInfoModel.roleType == RoleType.OWNER);

        if (roomConfig.isSupportAddRobot) {
            tvAddRobot.setVisibility(View.VISIBLE);
        }
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
        tvAddRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddRobot();
            }
        });
    }

    /** 点击了添加机器人 */
    protected void onClickAddRobot() {
        RoomRepository.robotList(this, 30, new RxCallback<RobotListResp>() {
            @Override
            public void onSuccess(RobotListResp robotListResp) {
                super.onSuccess(robotListResp);
                if (robotListResp == null || robotListResp.robotList == null || robotListResp.robotList.size() == 0) {
                    return;
                }
                if (binder == null) {
                    return;
                }
                List<AudioRoomMicModel> micList = binder.getMicList();
                // 找到一个可以用的机器人
                AIPlayers aiPlayers = findAvailableAiPlayers(robotListResp.robotList, micList);

                if (aiPlayers == null) {
                    return;
                }

                // 找到一个空位置
                AudioRoomMicModel newRobotMic = findNewRobotMic(micList);
                if (newRobotMic == null) {
                    ToastUtils.showShort(R.string.no_empty_seat);
                    return;
                }

                // 上麦位
                binder.robotUpMicLocation(UserInfoRespConverter.conver(aiPlayers), newRobotMic.micIndex);

                // 添加到游戏中
                List<AIPlayers> aiPlayersList = new ArrayList<>();
                aiPlayersList.add(aiPlayers);
                gameViewModel.sudFSTAPPDecorator.notifyAPPCommonGameAddAIPlayers(aiPlayersList, 1);
            }
        });
    }

    private AudioRoomMicModel findNewRobotMic(List<AudioRoomMicModel> micList) {
        int totalRobotCount = 0;
        AudioRoomMicModel emptyMicModel = null;
        for (AudioRoomMicModel model : micList) {
            if (model.isAi) {
                totalRobotCount++;
            }
            if (emptyMicModel == null && !model.hasUser()) {
                emptyMicModel = model;
            }
        }
        if (totalRobotCount >= 8) {
            return null;
        }
        return emptyMicModel;
    }

    /**
     * 不在麦位上就可以使用
     * 修改为随机获取机器人
     *
     * @param robotList 机器人列表
     * @param micList   麦位列表
     */
    private AIPlayers findAvailableAiPlayers(List<AIPlayers> robotList, List<AudioRoomMicModel> micList) {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            int position = random.nextInt(robotList.size());
            AIPlayers aiPlayers = robotList.get(position);
            boolean exists = false;
            // 判断该机器人是否已经在麦位上了
            for (AudioRoomMicModel audioRoomMicModel : micList) {
                if ((audioRoomMicModel.userId + "").equals(aiPlayers.userId)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                return aiPlayers;
            }
        }
        return null;
    }

    /** 点击了选择游戏 */
    protected void clickSelectGame() {
        GameModeDialog dialog = GameModeDialog.getInstance(roomInfoModel.sceneType);
        dialog.setFinishGame(gameViewModel.isOperateFinishGame());
        dialog.setPlayingGameId(playingGameId);
        dialog.show(getSupportFragmentManager(), null);
        dialog.setSelectGameListener(new GameModeDialog.SelectGameListener() {
            @Override
            public void onSelectGame(long gameId, boolean isFinishGame) {
                if (gameId == 0 && isFinishGame) { // 结束游戏
                    clickFinishGame();
                } else {
                    // 自己主动切游戏，需判断一下是否要拦截
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

    /** 点击了更多按钮 */
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

    /** 意图挂起房间 */
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

    /** 是否是全屏显示的 */
    private boolean isGamePlaying() {
        return playingGameId > 0;
    }

    /** 挂起房间 */
    private void hangRoom() {
        if (binder != null) {
            binder.showFloating(roomInfoModel, getClass());
        }
        releaseService();
        finish();
    }

    /**
     * 设置麦克风的状态
     *
     * @param micOpen true为开启麦克风 false为关闭麦克风
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
                onShowFinishGameChange(isShow);
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
        gameViewModel.captainChangeLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                checkGameAddAiPlayers();
            }
        });
        gameViewModel.gameLoadingProgressLiveData.observe(this, new Observer<GameLoadingProgressModel>() {
            @Override
            public void onChanged(GameLoadingProgressModel model) {
                if (model != null && model.progress == 99) {
                    gameContainer.postDelayed(() -> {
                        // 兼容小米手机全屏处理
                        updateStatusBar();
                    }, 100);
                }
            }
        });
        gameViewModel.onGameGetScoreLiveData.observe(this, (o) -> {
            onGameGetScore();
        });
        gameViewModel.onGameSetScoreLiveData.observe(this, this::onGameSetScore);
    }

    private void onGameGetScore() {
        HomeRepository.getAccount(this, new RxCallback<GetAccountResp>() {
            @Override
            public void onSuccess(GetAccountResp resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    gameViewModel.sudFSTAPPDecorator.notifyAPPCommonGameScore(resp.coin);
                }
            }
        });
    }

    private void onGameSetScore(SudMGPMGState.MGCommonGameSetScore model) {
        if (model == null) {
            return;
        }
        GameRepository.bringChip(this, gameViewModel.getPlayingGameId(), gameViewModel.getGameRoomId(),
                model.roundId, model.lastRoundScore, model.incrementalScore, model.totalScore, new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        LogUtils.d("bringChip onSuccess");
                    }
                });
    }

    /**
     * 是否可以结束游戏变化
     *
     * @param isShow
     */
    protected void onShowFinishGameChange(Boolean isShow) {
        boolean isOperateFinishGame = isShow != null && isShow;
        // 是房主，则不显示结束游戏按钮，放到选择游戏弹窗当中
        if (roomInfoModel.roleType == RoleType.OWNER) {
            topView.setFinishGameVisible(false);
        } else {
            topView.setFinishGameVisible(isOperateFinishGame);
        }
        if (gameModeDialog != null) {
            gameModeDialog.setFinishGame(isOperateFinishGame);
        }
    }

    /** 把麦位上的机器人都添加到游戏上去 */
    private void checkGameAddAiPlayers() {
        if (gameViewModel.isCaptain(HSUserInfo.userId) && roomConfig.isSupportAddRobot) {
            if (binder != null) {
                List<AIPlayers> aiPlayers = new ArrayList<>();
                for (AudioRoomMicModel audioRoomMicModel : binder.getMicList()) {
                    if (audioRoomMicModel.hasUser() && audioRoomMicModel.isAi) {
                        aiPlayers.add(AIPlayersConverter.conver(audioRoomMicModel));
                    }
                }
                if (aiPlayers.size() > 0) {
                    gameViewModel.sudFSTAPPDecorator.notifyAPPCommonGameAddAIPlayers(aiPlayers, 1);
                }
            }
        }
    }

    /** 触发自动加入游戏 */
    protected void autoJoinGame() {
        gameViewModel.joinGame();
    }

    // asr的开启与关闭
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

    /** 设置开麦提示的文字 */
    private void setOpenMicText() {
        if (playingGameId == GameIdCons.I_GUESS_YOU_SAID) { //  你说我猜
            tvOpenMic.setText(R.string.asr_guide_draw);
        } else if (playingGameId == GameIdCons.DIGITAL_BOMB) { // 数字炸弹
            tvOpenMic.setText(R.string.asr_guide_number_boom);
        } else if (playingGameId == GameIdCons.YOU_DRAW_AND_I_GUESS) { // 你画我猜
            tvOpenMic.setText(R.string.asr_guide_draw);
        }
    }

    // 是否支持ASR
    private boolean isSupportASR(long gameId) {
        if (gameId == GameIdCons.I_GUESS_YOU_SAID) return true;
        if (gameId == GameIdCons.DIGITAL_BOMB) return true;
        if (gameId == GameIdCons.YOU_DRAW_AND_I_GUESS) return true;
        return false;
    }

    // 点击了自己的麦位
    private void clickSelfMicLocation(int position) {
        BottomOptionDialog dialog = new BottomOptionDialog(this);
        int downMicKey = 1;
        dialog.addOption(downMicKey, getString(R.string.audio_down_mic)); // 增加下麦按钮
        dialog.setOnItemClickListener(new BottomOptionDialog.OnItemClickListener() {
            @Override
            public void onItemClick(BottomOptionDialog.BottomOptionModel model) {
                dialog.dismiss();
                if (model.key == downMicKey) {
                    //如果正在游戏中，则需要一个逃跑弹窗显示
                    if (gameViewModel.playerIsPlaying(HSUserInfo.userId)) {
                        playingDownMic(position);
                    } else {
                        if (binder != null) {
                            binder.micLocationSwitch(position, false, OperateMicType.USER); // 执行下麦
                        }
                        gameViewModel.exitGame();
                    }
                }
            }
        });
        dialog.show();
    }

    // 点击了其他人的麦位
    private void clickOtherMicLocation(int position, AudioRoomMicModel audioRoomMicModel) {
        long userId = audioRoomMicModel.userId;
        long selfUserId = HSUserInfo.userId;

        BottomOptionDialog dialog = new BottomOptionDialog(this);
        HashMap<Integer, String> options = new HashMap<>();

        // 1,加载了游戏
        // 2,自己是队长
        // 3,并且该用户也在游戏当中，可以转让队长
        int transferCaptainKey = 1;
        if (playingGameId > 0 && gameViewModel.isCaptain(selfUserId) && gameViewModel.playerIsIn(userId)) {
            options.put(transferCaptainKey, getString(R.string.transfer_captain));
        }

        // 1,加载了游戏
        // 2,游戏未开始
        // 3,自己是队长
        // 4,并且该用户加入了游戏，可以将他踢出游戏
        int kickGameKey = 2;
        if (playingGameId > 0
                && gameViewModel.getGameState() != SudMGPMGState.MGCommonGameState.PLAYING
                && gameViewModel.getGameState() != SudMGPMGState.MGCommonGameState.LOADING
                && gameViewModel.isCaptain(selfUserId)
                && gameViewModel.playerIsIn(userId)) {
            options.put(kickGameKey, getString(R.string.kick_game));
        }

        // 自己是房主，可以把其他人给踢出
        int kickOutRoomKey = 3;
        if (roomInfoModel.roleType == RoleType.OWNER) {
            options.put(kickOutRoomKey, getString(R.string.kick_out_room));
        }

        if (options.size() > 0) {
            for (Map.Entry<Integer, String> next : options.entrySet()) {
                dialog.addOption(next.getKey(), next.getValue()); // 增加下麦按钮
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
                } else if (model.key == kickOutRoomKey) {
                    if (binder != null) {
                        binder.kickOutRoom(audioRoomMicModel);
                    }
                    if (audioRoomMicModel.hasUser()) {
                        kickUserFromGame(audioRoomMicModel.userId + "");
                    }
                }
            }
        });
        dialog.show();
    }

    /**
     * 游戏中下麦提示弹窗
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
                        binder.micLocationSwitch(position, false, OperateMicType.USER); // 执行下麦
                    }
                    gameViewModel.exitGame();
                }
                dialog.dismiss();
            }
        });
    }

    /** 意图退出房间 */
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

    // 延迟退出房间
    public long delayExitRoom() {
        return delayExitRoom(false);
    }

    // 延迟退出房间
    public long delayExitRoom(boolean isStartMainPage) {
        if (closeing) return 0;
        closeing = true;
        if (playingGameId > 0) {
            // 在游戏时才需要
            gameViewModel.exitGame();
            topView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitRoom(isStartMainPage);
                }
            }, delayExitDuration);
            return delayExitDuration;
        } else {
            exitRoom(isStartMainPage);
            return 0;
        }
    }

    // 退出房间
    private void exitRoom() {
        exitRoom(false);
    }

    // 退出房间
    private void exitRoom(boolean isStartMainPage) {
        if (binder != null) {
            binder.exitRoom();
        }
        releaseService();
        if (isStartMainPage) {
            startMainPage();
        }
        finish();
    }

    private void startMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        intentHang();
    }

    /**
     * 自己主动切换游戏
     *
     * @param gameId 游戏id
     */
    protected void intentSwitchGame(long gameId) {
        // 发送http通知后台
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

    /** 自己主动切换了游戏 */
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
        String numberStr = getString(R.string.game_number) + "：" + playerInNumber + "/" + gameMaxNumber;
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

    /** 获取初始化礼物弹窗时的麦位数据 */
    protected List<AudioRoomMicModel> getGiftDialogMicList() {
        if (binder != null) {
            return binder.getMicList();
        }
        return null;
    }

    /**
     * 1.如果送给单个对象
     * underUser:代表送礼人信息
     * index:传0
     * 2.展示麦上列表
     * underUser:null
     * index:默认选中的麦上用户麦序号
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
        roomGiftDialog.setOnShowCustomRocketClickListener((v) -> {
            onGiftDialogShowCustomRocket();
        });
    }

    /** 礼物弹窗，点击显示定制火箭 */
    protected void onGiftDialogShowCustomRocket() {
    }

    protected void onSendGift(GiftModel giftModel, int giftCount, List<UserInfo> toUsers) {
        if (toUsers == null || toUsers.size() == 0) {
            return;
        }
        BooleanModel isShowGift = new BooleanModel();
        isShowGift.value = true;
        for (UserInfo user : toUsers) {
            // 发送http到后端
            int giftConfigType; // 礼物配置方式（1：客户端，2：服务端）
            if (giftModel.type == 0) { // 1.4.0新增:礼物类型 0：内置礼物 1：后端配置礼物
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
                            if (isShowGift.value) { // 只显示一次动效
                                isShowGift.value = false;
                                showGift(giftModel);
                            }
                        }
                    });
        }
    }

    /** 显示礼物 */
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

    /** 是否可以展示礼物动画 */
    protected boolean canShowGift() {
        return true;
    }

    private void initGame() {
        if (roomConfig.isSudGame) {
            gameViewModel.switchGame(this, getGameRoomId(), roomInfoModel.gameId);
        }
        updateGameNumber();
    }

    /** 获取游戏房间的id */
    public long getGameRoomId() {
        return roomInfoModel.roomId;
    }

    // 切换游戏之后，更新页面样式
    protected void updatePageStyle() {
        if (isSupportASR(playingGameId) && roomConfig.isShowASRTopHint) {
            tvASRHint.setVisibility(View.VISIBLE);
        } else {
            tvASRHint.setVisibility(View.GONE);
        }
//        if (roomConfig.isSupportAddRobot && playingGameId > 0 && roomInfoModel.roleType == RoleType.OWNER) {
//            tvAddRobot.setVisibility(View.VISIBLE);
//        } else {
//            tvAddRobot.setVisibility(View.GONE);
//        }
    }

    protected void updateStatusBar() {
        if (roomConfig.isSudGame && roomInfoModel != null && roomInfoModel.gameId > 0) { // 玩着游戏
            ImmersionBar.with(this).statusBarColor(R.color.transparent).fullScreen(true).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.transparent).fullScreen(false).hideBar(BarHide.FLAG_SHOW_BAR).init();
        }
    }

    /**
     * 绑定后台服务
     */
    private void bindService() {
        Intent intent = new Intent(this, SceneRoomService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //Android8后需要开启前台服务才可以
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
            // 系统会在与服务的连接意外中断（或者随着activity 的生命周期stop）时调用该方法，当客户端取消绑定的时候，不会回调该方法
            finish();
        }
    };


    // region service回调
    @Override
    public void onEnterRoomSuccess() {
        checkAddDefaultRobot();
        businessAutoUpMic();
    }

    /** 检查是否要添加默认的机器人 */
    protected void checkAddDefaultRobot() {
        if (!roomConfig.isSupportAddRobot) {
            return;
        }
        boolean existsMicUser = false;
        if (binder != null) {
            for (AudioRoomMicModel audioRoomMicModel : binder.getMicList()) {
                if (audioRoomMicModel.hasUser()) {
                    existsMicUser = true;
                    break;
                }
            }
        }
        // 麦位上有人了，不再添加默认机器人
        if (existsMicUser) {
            return;
        }
        // 不存在，默认添加三个机器人
        RoomRepository.robotList(this, 30, new RxCallback<RobotListResp>() {
            @Override
            public void onSuccess(RobotListResp resp) {
                super.onSuccess(resp);
                if (resp == null || resp.robotList == null || resp.robotList.size() == 0) {
                    return;
                }
                if (binder == null) {
                    return;
                }
                // 修改逻辑为，随机选三个机器人上麦位
                int addRobotCount = 3;
                if (resp.robotList.size() <= addRobotCount) {
                    for (int i = 0; i < resp.robotList.size(); i++) {
                        if (i < addRobotCount) {
                            binder.robotUpMicLocation(UserInfoRespConverter.conver(resp.robotList.get(i)), i + 1);
                        }
                    }
                } else {
                    List<AIPlayers> list = new ArrayList<>();
                    Random random = new Random();
                    while (list.size() < addRobotCount) {
                        int position = random.nextInt(resp.robotList.size());
                        AIPlayers aiPlayers = resp.robotList.get(position);
                        if (!list.contains(aiPlayers)) {
                            list.add(aiPlayers);
                        }
                    }
                    for (int i = 0; i < list.size(); i++) {
                        binder.robotUpMicLocation(UserInfoRespConverter.conver(list.get(i)), i + 1);
                    }
                }
            }
        });
    }

    /** 业务自动上麦 */
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
    public void onMicChange(int micIndex, UserInfo userInfo, boolean isUp) {
        if (isUp) {
            if (userInfo.isAi) {
                List<AIPlayers> aiPlayers = new ArrayList<>();
                aiPlayers.add(AIPlayersConverter.conver(userInfo));
                gameViewModel.sudFSTAPPDecorator.notifyAPPCommonGameAddAIPlayers(aiPlayers, 1);
            }
        } else {
            if (userInfo.isAi) {
                kickUserFromGame(userInfo.userID);
            }
        }
    }

    @Override
    public void notifyMicItemChange(int micIndex, AudioRoomMicModel model) {
        micView.notifyItemChange(micIndex, model);
        updateGiftDialogMicUsers(model);
    }

    /** 更新礼物弹窗上面的麦位数据 */
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
            // 下面这个逻辑处理的原因是在于
            // 例如狼人杀游戏，用户点击加入游戏后，游戏会短时间内发送mg_common_player_in以及mg_common_self_microphone
            // app会处理上麦以及开麦的逻辑，但上麦是有延迟的，并且开麦依赖于上麦成功之后的streamId
            // 所以这里在上麦成功之后，再判断一下是否要执行开麦的逻辑
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

    @Override
    public void onKickOutRoom(String userId) {
        processOnKickOutRoom(userId);
    }

    // endregion service回调

    /** 处理踢出房间的逻辑 */
    private void processOnKickOutRoom(String userId) {
        // 自己的话，执行退出房间
        if ((HSUserInfo.userId + "").equals(userId)) {
            delayExitRoom();
            return;
        }
        // 如果是别人，判断自己是不是队长，是队长就把他踢出游戏
        kickUserFromGame(userId);
    }

    /** 把该用户从游戏当中踢出 */
    protected void kickUserFromGame(String userId) {
        if (playingGameId > 0
                && gameViewModel.isCaptain(HSUserInfo.userId)
                && gameViewModel.getGameState() != SudMGPMGState.MGCommonGameState.PLAYING
                && gameViewModel.getGameState() != SudMGPMGState.MGCommonGameState.LOADING) {
            gameViewModel.notifyAPPCommonSelfKick(userId);
        }
    }

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

    // 释放绑定的服务
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

    public SceneRoomService.MyBinder getBinder() {
        return binder;
    }

    public RoomInfoModel getRoomInfoModel() {
        return roomInfoModel;
    }

    public long getPlayingGameId() {
        return playingGameId;
    }
}
