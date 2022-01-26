package tech.sud.mgp.audio.example.activity;

import java.io.Serializable;
import java.util.List;

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
import tech.sud.mgp.common.base.BaseActivity;

public class AudioRoomActivity extends BaseActivity {

    public RoomInfoModel roomInfoModel;
    private AudioRoomTopView topView;
    private AudioRoomMicWrapView micView;
    private AudioRoomChatView chatView;
    private AudioRoomBottomView bottomView;

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
    };

}
