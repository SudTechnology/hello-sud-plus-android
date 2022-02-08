package tech.sud.mgp.audio.example.service;

import tech.sud.mgp.audio.example.manager.AudioRoomServiceManager;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.model.UserInfo;

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

        public void micLocationSwitch(int micIndex, long userId, boolean operate) {
            serviceManager.audioMicManager.micLocationSwitch(micIndex, userId, operate);
        }

        public void autoUpMic() {
            serviceManager.audioMicManager.autoUpMic();
        }

        public void sendPublicMsg(CharSequence msg) {
            serviceManager.audioChatManager.sendPublicMsg(msg);
        }

        public void sendGift(int giftID,
                             int giftCount,
                             UserInfo toUser) {
            serviceManager.audioGiftManager.sendGift(giftID, giftCount, toUser);
        }
    }

}
