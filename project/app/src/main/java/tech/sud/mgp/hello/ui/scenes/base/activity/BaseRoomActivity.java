package tech.sud.mgp.hello.ui.scenes.base.activity;

import android.Manifest;
import android.view.View;
import android.view.ViewGroup;
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

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.permission.PermissionFragment;
import tech.sud.mgp.hello.common.permission.SudPermissionUtils;
import tech.sud.mgp.hello.common.widget.dialog.BottomOptionDialog;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomService;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.GameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.SceneRoomViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.dialog.GameModeDialog;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.SceneRoomBottomView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.SceneRoomTopView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.RoomInputMsgView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.OnMicItemClickListener;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.SceneRoomMicWrapView;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.GiftSendClickListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftNotifyDetailodel;
import tech.sud.mgp.hello.ui.scenes.common.gift.view.GiftEffectView;
import tech.sud.mgp.hello.ui.scenes.common.gift.view.RoomGiftDialog;

/**
 * 场景房间的基类
 */
public abstract class BaseRoomActivity<T extends GameViewModel> extends BaseActivity implements SceneRoomServiceCallback {

    protected RoomInfoModel roomInfoModel; // 房间信息
    protected long playingGameId; // 当前正在玩的游戏id
    protected boolean needEnterRoom = true; // 标识是否需要进入房间

    protected SceneRoomTopView topView;
    protected SceneRoomMicWrapView micView;
    protected SceneRoomChatView chatView;
    protected SceneRoomBottomView bottomView;
    protected FrameLayout giftContainer;
    protected GiftEffectView effectView;
    protected RoomInputMsgView inputMsgView;
    protected FrameLayout gameContainer;
    protected TextView tvGameNumber;

    private boolean closeing; // 标识是否正在关闭房间
    protected final SceneRoomService sceneRoomService = new SceneRoomService();
    protected final SceneRoomService.MyBinder binder = sceneRoomService.getBinder();
    protected final SceneRoomViewModel viewModel = new SceneRoomViewModel();
    protected final T gameViewModel = initGameViewModel();

    // 初始化游戏业务模型
    protected abstract T initGameViewModel();

    private RoomGiftDialog roomGiftDialog;

    /**
     * 场景配置，子类可对其进行修改进行定制化需求
     */
    protected final SceneConfig sceneConfig = new SceneConfig();

    @Override
    protected boolean beforeSetContentView() {
        Serializable roomInfoModel = getIntent().getSerializableExtra("RoomInfoModel");
        if (roomInfoModel instanceof RoomInfoModel) {
            this.roomInfoModel = (RoomInfoModel) roomInfoModel;
        }
        if (this.roomInfoModel == null || this.roomInfoModel.roomId == 0) {
            return true;
        }
        playingGameId = this.roomInfoModel.gameId;
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_scene;
    }

    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.transparent).init();
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

        // 设置沉浸式状态栏时，顶部view的间距
        ViewGroup.LayoutParams topViewParams = topView.getLayoutParams();
        if (topViewParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) topViewParams;
            marginLayoutParams.topMargin = marginLayoutParams.topMargin + ImmersionBar.getStatusBarHeight(this);
            topView.setLayoutParams(marginLayoutParams);
        }

        topView.setFinishGameVisible(false);
        topView.setSelectGameVisible(roomInfoModel.roleType == RoleType.OWNER);
    }

    @Override
    protected void initData() {
        super.initData();
        topView.setName(roomInfoModel.roomName);
        topView.setId(getString(R.string.audio_room_number) + " " + roomInfoModel.roomId);
        viewModel.initData();
        initGame();
    }

    private void enterRoom() {
        sceneRoomService.onCreate();
        binder.setCallback(this);
        binder.init(sceneConfig);
        updatePageStyle();
        binder.enterRoom(roomInfoModel);
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
                        binder.micLocationSwitch(position, true, OperateMicType.USER);
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
                binder.autoUpMic(OperateMicType.USER);
            }
        });
        bottomView.setInputClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                binder.sendPublicMsg(msg);
                inputMsgView.hide();
                inputMsgView.clearInput();
            }
        });
        topView.setSelectGameClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameModeDialog dialog = new GameModeDialog();
                dialog.setPlayingGameId(playingGameId);
                dialog.show(getSupportFragmentManager(), null);
                dialog.setSelectGameListener(new GameModeDialog.SelectGameListener() {
                    @Override
                    public void onSelectGame(long gameId) {
                        switchGame(gameId, true);
                    }
                });
            }
        });
        topView.setCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentClose();
            }
        });
        topView.setFinishGameOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFinishGame();
            }
        });
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
        gameViewModel.gameMessageLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String msg) {
                if (msg != null) {
                    binder.addChatMsg(msg);
                }
            }
        });
        gameViewModel.updateMicLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object obj) {
                binder.updateMicList();
            }
        });
        gameViewModel.autoUpMicLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                binder.autoUpMic(OperateMicType.GAME_AUTO);
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
                binder.setRTCPlay(aBoolean);
            }
        });
        gameViewModel.showFinishGameBtnLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isShow) {
                topView.setFinishGameVisible(isShow != null && isShow);
            }
        });
        gameViewModel.playerInLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                updateGameNumber();
            }
        });
    }

    // asr的开启与关闭
    protected void onASRChanged(boolean open) {
        binder.setASROpen(open);
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
                        binder.micLocationSwitch(position, false, OperateMicType.USER); // 执行下麦
                        gameViewModel.exitGame();
                    }
                }
            }
        });
        dialog.show();
    }

    // 点击了其他人的麦位
    private void clickOtherMicLocation(int position, AudioRoomMicModel model) {
        long userId = model.userId;
        long selfUserId = HSUserInfo.userId;

        BottomOptionDialog dialog = new BottomOptionDialog(this);
        HashMap<Integer, String> options = new HashMap<>();

        // 1,加载了游戏
        // 2,自己是队长（当前app业务如此设计，你可以自由定义权限控制或没有权限控制）
        // 3,并且该用户也在游戏当中，可以转让队长
        int transferCaptainKey = 1;
        if (playingGameId > 0 && gameViewModel.isCaptain(selfUserId) && gameViewModel.playerIsIn(userId)) {
            options.put(transferCaptainKey, getString(R.string.transfer_captain));
        }

        // 1,加载了游戏
        // 2,游戏未开始
        // 3,自己是队长（当前app业务如此设计，你可以自由定义权限控制或没有权限控制）
        // 4,并且该用户加入了游戏，可以将他踢出游戏
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
                    binder.micLocationSwitch(position, false, OperateMicType.USER); // 执行下麦
                    gameViewModel.exitGame();
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 意图退出房间
     */
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
    private void delayExitRoom() {
        if (closeing) return;
        closeing = true;
        if (playingGameId > 0) {
            // 在游戏时才需要
            gameViewModel.exitGame();
            topView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitRoom();
                }
            }, 500);
        } else {
            exitRoom();
        }
    }

    // 退出房间
    private void exitRoom() {
        binder.exitRoom();
        viewModel.exitRoom(roomInfoModel.roomId);
        finish();
    }

    @Override
    public void onBackPressed() {
        intentClose();
    }

    /**
     * 切换游戏
     *
     * @param gameId
     * @param selfSwitch 标识是否是自己切换的
     */
    private void switchGame(long gameId, boolean selfSwitch) {
        if (playingGameId == gameId) {
            return;
        }
        if (selfSwitch) { // 自己主动切换时，如果游戏正在进行中，则不进行切换
            int gameState = gameViewModel.getGameState();
            if (gameState == SudMGPMGState.MGCommonGameState.LOADING || gameState == SudMGPMGState.MGCommonGameState.PLAYING) {
                ToastUtils.showLong(R.string.audio_switch_game_warn);
                return;
            }
        }
        playingGameId = gameId;
        roomInfoModel.gameId = gameId;
        gameViewModel.switchGame(this, gameId);
        binder.updateMicList();
        binder.switchGame(gameId, selfSwitch);
        updatePageStyle();
        updateGameNumber();
    }

    private void updateGameNumber() {
        long gameId = playingGameId;
        if (gameId <= 0) {
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
                            binder.setMicState(true);
                            boolean asrIsOpen = gameViewModel.gameASRLiveData.getValue() != null && gameViewModel.gameASRLiveData.getValue();
                            binder.setASROpen(asrIsOpen);
                        }
                    }
                });
    }

    private void closeMic() {
        binder.setMicState(false);
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
        roomGiftDialog = new RoomGiftDialog();
        if (underUser == null) {
            roomGiftDialog.setMicUsers(binder.getMicList(), index);
        } else {
            roomGiftDialog.setToUser(underUser);
        }
        roomGiftDialog.giftSendClickListener = new GiftSendClickListener() {
            @Override
            public void onSendClick(int giftId, int giftCount, List<UserInfo> toUsers) {
                if (toUsers != null && toUsers.size() > 0) {
                    for (UserInfo user : toUsers) {
                        binder.sendGift(giftId, giftCount, user);
                    }
                }
                showGift(GiftHelper.getInstance().getGift(giftId));
            }

            @Override
            public void onNotify(Map<Long, Boolean> userState) {
                binder.updateMicList();
            }
        };
        roomGiftDialog.show(getSupportFragmentManager(), "gift");
        roomGiftDialog.setOnDestroyListener(new BaseDialogFragment.OnDestroyListener() {
            @Override
            public void onDestroy() {
                roomGiftDialog = null;
                binder.updateMicList();
            }
        });
    }

    private void showGift(GiftModel giftModel) {
        if (effectView == null) {
            effectView = new GiftEffectView(this);
            effectView.addLifecycleObserver(this);
            giftContainer.addView(effectView);
        }
        effectView.showEffect(giftModel);
    }

    private void initGame() {
        gameViewModel.setRoomId(roomInfoModel.roomId);
        gameViewModel.switchGame(this, roomInfoModel.gameId);
        updateGameNumber();
    }

    // 切换游戏之后，更新页面样式
    protected void updatePageStyle() {
    }

    private void updateStatusBar() {
        if (roomInfoModel != null && roomInfoModel.gameId > 0) { // 玩着游戏
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_SHOW_BAR).init();
        }
    }

    // region service回调
    @Override
    public void onEnterRoomSuccess() {
        binder.autoUpMic(OperateMicType.USER);
    }

    @Override
    public void setMicList(List<AudioRoomMicModel> list) {
        micView.setList(list);
        if (roomGiftDialog != null) {
            roomGiftDialog.updateMicUsers(list);
        }
    }

    @Override
    public void notifyMicItemChange(int micIndex, AudioRoomMicModel model) {
        micView.notifyItemChange(micIndex, model);
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
    public void sendGiftsNotify(GiftNotifyDetailodel notify) {
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
    public void onRoomOnlineUserCountUpdate(String roomID, int count) {
        topView.setNumber(count + "");
    }

    @Override
    public void onGameChange(long gameId) {
        switchGame(gameId, false);
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
    // endregion service回调

    @Override
    protected void onStart() {
        super.onStart();
        gameViewModel.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameViewModel.onResume();
        if (needEnterRoom) {
            needEnterRoom = false;
            enterRoom();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sceneRoomService.onDestroy();
        binder.setCallback(null);
        if (effectView != null) {
            effectView.onDestory();
        }
        gameViewModel.destroyMG();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateStatusBar();
        }
    }
}