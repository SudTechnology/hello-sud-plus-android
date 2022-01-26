package tech.sud.mgp.audio.example.service;

import tech.sud.mgp.audio.example.manager.AudioRoomServiceManager;
import tech.sud.mgp.audio.example.model.RoomInfoModel;

/**
 * 房间服务
 */
public class AudioRoomService {

    private final AudioRoomServiceManager serviceManager = new AudioRoomServiceManager();
    private final MyBinder binder = new MyBinder();

    public void onCreate() {
        serviceManager.onCreate();
    }

    public void onDestroy() {
        serviceManager.onDestroy();
    }

    public MyBinder getBinder() {
        return binder;
    }

    public class MyBinder {
        public void setCallback(AudioRoomServiceCallback callback) {
            serviceManager.setCallback(callback);
        }

        public void enterRoom(RoomInfoModel model) {
            serviceManager.enterRoom(model);
        }
    }

}
