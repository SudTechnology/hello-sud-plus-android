package tech.sud.mgp.audio.example.manager;

import tech.sud.mgp.audio.example.model.AudioRoomData;

/**
 * 房间主要业务逻辑
 */
public class AudioRoomServiceManager extends BaseServiceManager {

    public AudioRoomData audioRoomData;

    public AudioEngineManager audioEngineManager;
    public AudioChatManager audioChatManager;
    public AudioMicManager audioMicManager;
    public AudioStreamManager audioStreamManager;

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
}
