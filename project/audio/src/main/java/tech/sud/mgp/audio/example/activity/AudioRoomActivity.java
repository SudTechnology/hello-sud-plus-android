package tech.sud.mgp.audio.example.activity;

import android.Manifest;
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.KeyboardUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.model.UserInfo;
import tech.sud.mgp.audio.example.service.AudioRoomService;
import tech.sud.mgp.audio.example.service.AudioRoomServiceCallback;
import tech.sud.mgp.audio.example.viewmodel.AudioRoomViewModel;
import tech.sud.mgp.audio.example.widget.dialog.GameModeDialog;
import tech.sud.mgp.audio.example.widget.view.AudioRoomBottomView;
import tech.sud.mgp.audio.example.widget.view.AudioRoomTopView;
import tech.sud.mgp.audio.example.widget.view.chat.AudioRoomChatView;
import tech.sud.mgp.audio.example.widget.view.chat.RoomInputMsgView;
import tech.sud.mgp.audio.example.widget.view.mic.AudioRoomMicWrapView;
import tech.sud.mgp.audio.example.widget.view.mic.OnMicItemClickListener;
import tech.sud.mgp.audio.gift.listener.GiftSendClickListener;
import tech.sud.mgp.audio.gift.manager.GiftHelper;
import tech.sud.mgp.audio.gift.model.GiftModel;
import tech.sud.mgp.audio.gift.model.GiftNotifyDetailodel;
import tech.sud.mgp.audio.gift.view.GiftEffectView;
import tech.sud.mgp.audio.gift.view.RoomGiftDialog;
import tech.sud.mgp.common.base.BaseActivity;
import tech.sud.mgp.common.base.BaseDialogFragment;
import tech.sud.mgp.common.model.HSUserInfo;
import tech.sud.mgp.common.permission.PermissionFragment;
import tech.sud.mgp.common.permission.SudPermissionUtils;
import tech.sud.mgp.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.game.example.viewmodel.GameViewModel;

public class AudioRoomActivity extends BaseActivity {

    public RoomInfoModel roomInfoModel; // 房间信息
    private long playingGameId; // 当前正在玩的游戏id

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
        return R.layout.audio_activity_room;
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
        enterRoom();
    }

    private void enterRoom() {
        audioRoomService.onCreate();
        binder.setCallback(callback);
        binder.enterRoom(roomInfoModel);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        micView.setOnMicItemClickListener(new OnMicItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AudioRoomMicModel model = micView.getItem(position);
                if (model != null) {
                    if (model.userId == 0) {
                        binder.micLocationSwitch(position, HSUserInfo.userId, true);
                    } else if (model.userId == HSUserInfo.userId) {
                        binder.micLocationSwitch(position, model.userId, false);
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
        KeyboardUtils.registerSoftInputChangedListener(this, new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChanged(int height) {
                inputMsgView.onSoftInputChanged(height);
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
        topView.setCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentClose();
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
                getString(R.string.common_confirm));
        dialog.show();
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    finish();
                } else {
                    dialog.dismiss();
                }
            }
        });
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
        playingGameId = gameId;
        roomInfoModel.gameId = gameId;
        gameViewModel.switchGame(this, gameId);
        binder.switchGame(gameId, selfSwitch);
    }

    private void openMic() {
        SudPermissionUtils.requirePermission(this, getSupportFragmentManager(),
                new String[]{Manifest.permission.RECORD_AUDIO},
                new PermissionFragment.OnPermissionListener() {
                    @Override
                    public void onPermission(boolean success) {
                        binder.setMicState(true);
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
        RoomGiftDialog dialog = new RoomGiftDialog();
        if (underUser == null) {
            dialog.setMicUsers(binder.getMicList(), index);
        } else {
            dialog.setToUser(underUser);
        }
        dialog.giftSendClickListener = new GiftSendClickListener() {
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
                binder.updateGiftIcon(userState);
            }
        };
        dialog.show(getSupportFragmentManager(), "gift");
        dialog.setOnDestroyListener(new BaseDialogFragment.OnDestroyListener() {
            @Override
            public void onDestroy() {
                binder.updateGiftIcon(new HashMap<Long, Boolean>());
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
        }

        @Override
        public void notifyMicItemChange(int micIndex, AudioRoomMicModel model) {
            micView.notifyItemChange(micIndex, model);
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

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioRoomService.onDestroy();
        binder.setCallback(null);
        if (effectView != null) {
            effectView.onDestory();
        }
    }
}