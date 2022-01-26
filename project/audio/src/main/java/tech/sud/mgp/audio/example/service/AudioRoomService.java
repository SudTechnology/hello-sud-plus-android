package tech.sud.mgp.audio.example.service;

import tech.sud.mgp.audio.example.manager.AudioRoomServiceManager;

/**
 * 房间服务
 */
public class AudioRoomService {

    private final AudioRoomServiceManager mServiceManager = new AudioRoomServiceManager();

    public void onCreate() {
        mServiceManager.onCreate();
    }

    public void onDestroy() {
        mServiceManager.onDestroy();
    }

}
