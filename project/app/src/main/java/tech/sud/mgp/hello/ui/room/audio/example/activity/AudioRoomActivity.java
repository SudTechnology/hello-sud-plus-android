package tech.sud.mgp.hello.ui.room.audio.example.activity;

import android.Manifest;
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.permission.PermissionFragment;
import tech.sud.mgp.hello.common.permission.SudPermissionUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.ui.game.middle.model.GameMessageModel;
import tech.sud.mgp.hello.ui.game.middle.state.mg.common.CommonGameState;
import tech.sud.mgp.hello.ui.room.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.room.audio.example.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.room.audio.example.model.UserInfo;
import tech.sud.mgp.hello.ui.room.audio.example.service.AudioRoomService;
import tech.sud.mgp.hello.ui.room.audio.example.service.AudioRoomServiceCallback;
import tech.sud.mgp.hello.ui.room.audio.example.viewmodel.AudioRoomViewModel;
import tech.sud.mgp.hello.ui.room.audio.example.viewmodel.GameViewModel;
import tech.sud.mgp.hello.ui.room.audio.example.widget.dialog.BottomOptionDialog;
import tech.sud.mgp.hello.ui.room.audio.example.widget.dialog.GameModeDialog;
import tech.sud.mgp.hello.ui.room.audio.example.widget.view.AudioRoomBottomView;
import tech.sud.mgp.hello.ui.room.audio.example.widget.view.AudioRoomTopView;
import tech.sud.mgp.hello.ui.room.audio.example.widget.view.chat.AudioRoomChatView;
import tech.sud.mgp.hello.ui.room.audio.example.widget.view.chat.RoomInputMsgView;
import tech.sud.mgp.hello.ui.room.audio.example.widget.view.mic.AudioRoomMicWrapView;
import tech.sud.mgp.hello.ui.room.audio.example.widget.view.mic.OnMicItemClickListener;
import tech.sud.mgp.hello.ui.room.audio.gift.listener.GiftSendClickListener;
import tech.sud.mgp.hello.ui.room.audio.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.room.audio.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.room.audio.gift.model.GiftNotifyDetailodel;
import tech.sud.mgp.hello.ui.room.audio.gift.view.GiftEffectView;
import tech.sud.mgp.hello.ui.room.audio.gift.view.RoomGiftDialog;

public class AudioRoomActivity extends BaseActivity {

    public RoomInfoModel roomInfoModel; // 房间信息
    private long playingGameId; // 当前正在玩的游戏id
    private boolean needEnterRoom = true; // 标识是否需要进入房间

    private AudioRoomTopView topView;
    private AudioRoomMicWrapView micView;
    private AudioRoomChatView chatView;
    private AudioRoomBottomView bottomView;
    private FrameLayout giftContainer;
    private GiftEffectView effectView;
    private RoomInputMsgView inputMsgView;
    private FrameLayout gameContainer;

    private final AudioRoomService audioRoomService = new AudioRoomService();
    private final AudioRoomService.MyBinder binder = audioRoomService.getBinder();
    private final AudioRoomViewModel viewModel = new AudioRoomViewModel();
    private final GameViewModel gameViewModel = new GameViewModel();
    private RoomGiftDialog roomGiftDialog;

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
        return R.layout.activity_audio_room;
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
        inputMsgView.hide();

        // TODO: 2022/2/9 现在为了调试，把下面的权限判断代码给注释掉了
//        if (roomInfoModel.roleType == RoleType.OWNER) {
//            topView.setSelectModeVisibility(View.VISIBLE);
//        } else {
//            topView.setSelectModeVisibility(View.GONE);
//        }
    }

    @Override
    protected void initData() {
        super.initData();
        topView.setName(roomInfoModel.roomName);
        topView.setId(getString(R.string.audio_room_number) + " " + roomInfoModel.roomId);
    }

    private void enterRoom() {
        audioRoomService.onCreate();
        binder.setCallback(callback);
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
            }
        });
        micView.setOnMicItemClickListener(new OnMicItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AudioRoomMicModel model = micView.getItem(position);
                if (model != null) {
                    if (model.userId == 0) {
                        binder.micLocationSwitch(position, true);
                    } else if (model.userId == HSUserInfo.userId) {
                        clickSelfMicLocation(position);
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
                binder.autoUpMic();
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
                boolean micOpened = bottomView.isMicOpened();
                if (micOpened) {
                    closeMic();
                } else {
                    openMic();
                }
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
        topView.setSelectModeClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameModeDialog dialog = new GameModeDialog();
                dialog.setPlayingGameId(playingGameId);
                dialog.show(getSupportFragmentManager(), null);
                dialog.setSelectGameModeListener(new GameModeDialog.SelectGameModeListener() {
                    @Override
                    public void onSelectGameMode(long gameId) {
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
        gameViewModel.gameMessageLiveData.observe(this, new Observer<GameMessageModel>() {
            @Override
            public void onChanged(GameMessageModel gameMessageModel) {
                if (gameMessageModel != null) {
                    binder.addChatMsg(gameMessageModel.msg);
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
                binder.autoUpMic();
            }
        });
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
                    binder.micLocationSwitch(position, false); // 执行下麦
                }
            }
        });
        dialog.show();
    }

    /**
     * 意图退出房间
     */
    private void intentClose() {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this,
                getString(R.string.audio_close_room_title),
                getString(R.string.audio_cancle),
                getString(R.string.common_confirm));
        dialog.show();
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    exitRoom();
                } else {
                    dialog.dismiss();
                }
            }
        });
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
            if (gameState == CommonGameState.LOADING || gameState == CommonGameState.PLAYING) {
                ToastUtils.showLong(R.string.audio_switch_game_warn);
                return;
            }
        }
        playingGameId = gameId;
        roomInfoModel.gameId = gameId;
        gameViewModel.switchGame(this, gameId);
        binder.switchGame(gameId, selfSwitch);
        updatePageStyle();
    }

    private void openMic() {
        SudPermissionUtils.requirePermission(this, getSupportFragmentManager(),
                new String[]{Manifest.permission.RECORD_AUDIO},
                new PermissionFragment.OnPermissionListener() {
                    @Override
                    public void onPermission(boolean success) {
                        if (success) {
                            binder.setMicState(true);
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
            effectView = new GiftEffectView(AudioRoomActivity.this);
            effectView.addLifecycleObserver(AudioRoomActivity.this);
            giftContainer.addView(effectView);
        }
        effectView.showEffect(giftModel);
    }

    private void initGame() {
        gameViewModel.setRoomId(roomInfoModel.roomId);
        gameViewModel.switchGame(this, roomInfoModel.gameId);
        updatePageStyle();
    }

    /**
     * 切换游戏之后，更新页面样式
     */
    private void updatePageStyle() {
        if (roomInfoModel.gameId > 0) {
            micView.setMicStyle(AudioRoomMicWrapView.AudioRoomMicStyle.GAME);
            chatView.setChatStyle(AudioRoomChatView.AudioRoomChatStyle.GAME);
        } else {
            micView.setMicStyle(AudioRoomMicWrapView.AudioRoomMicStyle.NORMAL);
            chatView.setChatStyle(AudioRoomChatView.AudioRoomChatStyle.NORMAL);
        }
    }

    private final AudioRoomServiceCallback callback = new AudioRoomServiceCallback() {

        @Override
        public void onEnterRoomSuccess() {
            initGame();
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
    };

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
        audioRoomService.onDestroy();
        binder.setCallback(null);
        if (effectView != null) {
            effectView.onDestory();
        }
        gameViewModel.destroyMG();
    }
}