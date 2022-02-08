package tech.sud.mgp.audio.example.activity;

import android.view.View;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.KeyboardUtils;

import java.io.Serializable;
import java.util.List;

import tech.sud.mgp.audio.BuildConfig;
import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.model.UserInfo;
import tech.sud.mgp.audio.example.service.AudioRoomService;
import tech.sud.mgp.audio.example.service.AudioRoomServiceCallback;
import tech.sud.mgp.audio.example.viewmodel.AudioRoomViewModel;
import tech.sud.mgp.audio.example.widget.view.AudioRoomBottomView;
import tech.sud.mgp.audio.example.widget.view.AudioRoomTopView;
import tech.sud.mgp.audio.example.widget.view.chat.AudioRoomChatView;
import tech.sud.mgp.audio.example.widget.view.chat.RoomInputMsgView;
import tech.sud.mgp.audio.example.widget.view.mic.AudioRoomMicWrapView;
import tech.sud.mgp.audio.example.widget.view.mic.OnMicItemClickListener;
import tech.sud.mgp.audio.gift.callback.GiftSendClickCallback;
import tech.sud.mgp.audio.gift.manager.GiftHelper;
import tech.sud.mgp.audio.gift.model.GiftModel;
import tech.sud.mgp.audio.gift.view.GiftEffectView;
import tech.sud.mgp.audio.gift.view.RoomGiftDialog;
import tech.sud.mgp.common.base.BaseActivity;
import tech.sud.mgp.common.model.HSUserInfo;

public class AudioRoomActivity extends BaseActivity {

    public RoomInfoModel roomInfoModel;
    private AudioRoomTopView topView;
    private AudioRoomMicWrapView micView;
    private AudioRoomChatView chatView;
    private AudioRoomBottomView bottomView;
    private FrameLayout giftContainer;
    private GiftEffectView effectView;
    private RoomInputMsgView inputMsgView;

    private final AudioRoomService audioRoomService = new AudioRoomService();
    private final AudioRoomService.MyBinder binder = audioRoomService.getBinder();
    private final AudioRoomViewModel viewModel = new AudioRoomViewModel();

    @Override
    protected boolean beforeSetContentView() {
        Serializable roomInfoModel = getIntent().getSerializableExtra("RoomInfoModel");
        if (roomInfoModel instanceof RoomInfoModel) {
            this.roomInfoModel = (RoomInfoModel) roomInfoModel;
        }
        if (this.roomInfoModel == null || this.roomInfoModel.roomId == 0) {
            return true;
        }
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
        inputMsgView.hide();
    }

    @Override
    protected void initData() {
        super.initData();
        topView.setName(roomInfoModel.roomName);
        topView.setId(roomInfoModel.roomId + "");
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
            RoomGiftDialog dialog = new RoomGiftDialog();
            dialog.clickCallback = new GiftSendClickCallback() {
                @Override
                public void sendClick(int giftId, int giftCount, List<UserInfo> toUsers) {
                    if (toUsers != null && toUsers.size() > 0) {
                        for (UserInfo user : toUsers) {
                            binder.sendGift(giftId, giftCount, user);
                        }
                    }
                    if (BuildConfig.DEBUG) {
                        showGift(GiftHelper.getInstance().getGift(giftId));
                    }
                }
            };
            dialog.show(getSupportFragmentManager(), "gift");
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
    }

    private void showGift(GiftModel giftModel) {
        if (effectView == null) {
            effectView = new GiftEffectView(AudioRoomActivity.this);
            effectView.addLifecycleObserver(AudioRoomActivity.this);
            giftContainer.addView(effectView);
        }
        effectView.showEffect(giftModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioRoomService.onDestroy();
        binder.setCallback(null);
        if (effectView != null) {
            effectView.onDestory();
        }
    }

    private final AudioRoomServiceCallback callback = new AudioRoomServiceCallback() {

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
        public void sendGiftsNotify(int giftId, int giftCount) {
            showGift(GiftHelper.getInstance().getGift(giftId));
        }
    };
}