package tech.sud.mgp.audio.example.manager;

import tech.sud.mgp.audio.example.model.AudioRoomData;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.service.AudioRoomServiceCallback;

/**
 * 房间主要业务逻辑
 */
public class AudioRoomServiceManager extends BaseServiceManager {

    private AudioRoomServiceCallback audioRoomServiceCallback;

    public final AudioRoomData audioRoomData = new AudioRoomData();
    public final AudioEngineManager audioEngineManager = new AudioEngineManager(this);
    public final AudioChatManager audioChatManager = new AudioChatManager(this);
    public final AudioMicManager audioMicManager = new AudioMicManager(this);
    public final AudioStreamManager audioStreamManager = new AudioStreamManager(this);

    @Override
    public void onCreate() {
        super.onCreate();
        audioEngineManager.onCreate();
        audioChatManager.onCreate();
        audioMicManager.onCreate();
        audioStreamManager.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioEngineManager.onDestroy();
        audioChatManager.onDestroy();
        audioMicManager.onDestroy();
        audioStreamManager.onDestroy();
    }

    public void setCallback(AudioRoomServiceCallback callback) {
        audioRoomServiceCallback = callback;
    }

    public AudioRoomServiceCallback getCallback() {
        return audioRoomServiceCallback;
    }

    public void enterRoom(RoomInfoModel model) {
        audioEngineManager.enterRoom(model);
        audioMicManager.enterRoom(model);
    }

}
