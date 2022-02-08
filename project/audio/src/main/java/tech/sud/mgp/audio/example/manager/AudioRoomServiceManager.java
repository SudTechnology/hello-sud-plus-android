package tech.sud.mgp.audio.example.manager;

import tech.sud.mgp.audio.example.model.AudioRoomData;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.service.AudioRoomServiceCallback;

/**
 * 房间主要业务逻辑
 */
public class AudioRoomServiceManager extends BaseServiceManager {

    private AudioRoomServiceCallback audioRoomServiceCallback;
    private RoomInfoModel roomInfoModel;

    public final AudioRoomData audioRoomData = new AudioRoomData();
    public final AudioEngineManager audioEngineManager = new AudioEngineManager(this);
    public final AudioChatManager audioChatManager = new AudioChatManager(this);
    public final AudioMicManager audioMicManager = new AudioMicManager(this);
    public final AudioStreamManager audioStreamManager = new AudioStreamManager(this);
    public final AudioGiftManager audioGiftManager = new AudioGiftManager(this);

    @Override
    public void onCreate() {
        super.onCreate();
        audioEngineManager.onCreate();
        audioChatManager.onCreate();
        audioMicManager.onCreate();
        audioStreamManager.onCreate();
        audioGiftManager.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioEngineManager.onDestroy();
        audioChatManager.onDestroy();
        audioMicManager.onDestroy();
        audioStreamManager.onDestroy();
        audioGiftManager.onDestroy();
    }

    public void setCallback(AudioRoomServiceCallback callback) {
        audioRoomServiceCallback = callback;
    }

    public AudioRoomServiceCallback getCallback() {
        return audioRoomServiceCallback;
    }

    public long getRoomId() {
        RoomInfoModel model = roomInfoModel;
        if (model == null || model.roomId == null) {
            return 0;
        } else {
            return model.roomId;
        }
    }

    public void enterRoom(RoomInfoModel model) {
        roomInfoModel = model;
        audioEngineManager.enterRoom(model);
        audioMicManager.enterRoom(model);
    }

    public void setMicState(boolean isOpen) {
        if (isOpen) {
            String streamId = null;
            AudioRoomMicModel selfMicModel = audioMicManager.findSelfMicModel();
            if (selfMicModel != null) {
                streamId = selfMicModel.streamId;
            }
            audioStreamManager.openMic(streamId);
        } else {
            audioStreamManager.closeMic();
        }
    }

}
