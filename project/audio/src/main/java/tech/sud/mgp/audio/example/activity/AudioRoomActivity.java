package tech.sud.mgp.audio.example.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.io.Serializable;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.service.AudioRoomService;
import tech.sud.mgp.audio.example.viewmodel.AudioRoomViewModel;
import tech.sud.mgp.audio.example.widget.view.AudioRoomBottomView;
import tech.sud.mgp.audio.example.widget.view.AudioRoomTopView;
import tech.sud.mgp.audio.example.widget.view.chat.AudioRoomChatView;
import tech.sud.mgp.audio.example.widget.view.mic.AudioRoomMicWrapView;
import tech.sud.mgp.common.base.BaseActivity;

public class AudioRoomActivity extends BaseActivity {

    public RoomInfoModel mRoomInfoModel;
    private AudioRoomTopView mTopView;
    private AudioRoomMicWrapView mMicView;
    private AudioRoomChatView mChatView;
    private AudioRoomBottomView mBottomView;

    private final AudioRoomService mAudioRoomService = new AudioRoomService();
    private final AudioRoomViewModel mViewModel = new AudioRoomViewModel();

    @Override
    protected boolean beforeSetContentView() {
        Serializable roomInfoModel = getIntent().getSerializableExtra("RoomInfoModel");
        if (roomInfoModel instanceof RoomInfoModel) {
            mRoomInfoModel = (RoomInfoModel) roomInfoModel;
        }
        if (mRoomInfoModel == null || mRoomInfoModel.roomId == 0) {
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
        mTopView = findViewById(R.id.room_top_view);
        mMicView = findViewById(R.id.room_mic_view);
        mChatView = findViewById(R.id.room_chat_view);
        mBottomView = findViewById(R.id.room_bottom_view);
    }

    @Override
    protected void initData() {
        super.initData();
        mTopView.setName(mRoomInfoModel.roomName);
        mTopView.setId(mRoomInfoModel.roomId + "");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioRoomService.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioRoomService.onDestroy();
    }
}
