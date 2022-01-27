package tech.sud.mgp.audio.example.activity;

import android.view.View;
import android.widget.FrameLayout;

import java.io.Serializable;
import java.util.List;

import tech.sud.mgp.audio.BuildConfig;
import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.service.AudioRoomService;
import tech.sud.mgp.audio.example.service.AudioRoomServiceCallback;
import tech.sud.mgp.audio.example.viewmodel.AudioRoomViewModel;
import tech.sud.mgp.audio.example.widget.view.AudioRoomBottomView;
import tech.sud.mgp.audio.example.widget.view.AudioRoomTopView;
import tech.sud.mgp.audio.example.widget.view.chat.AudioRoomChatView;
import tech.sud.mgp.audio.example.widget.view.mic.AudioRoomMicWrapView;
import tech.sud.mgp.audio.example.widget.view.mic.OnMicItemClickListener;
import tech.sud.mgp.audio.gift.manager.GiftHelper;
import tech.sud.mgp.audio.gift.view.GiftEffectView;
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
                        binder.micSwitch(position, HSUserInfo.userId, true);
                    } else if (model.userId == HSUserInfo.userId) {
                        binder.micSwitch(position, model.userId, false);
                    }
                }
            }
        });
        bottomView.setGiftClickListener(v -> {
            if (BuildConfig.DEBUG) {
                if (effectView == null) {
                    effectView = new GiftEffectView(AudioRoomActivity.this);
                    effectView.addLifecycleObserver(AudioRoomActivity.this);
                    giftContainer.addView(effectView);
                }
                effectView.showEffect(GiftHelper.getInstance().getGift());
            }
        });
        bottomView.setGotMicClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binder.autoUpMic();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioRoomService.onDestroy();
        binder.setCallback(null);
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

    };

}
