package tech.sud.mgp.audio.example.service;

import java.util.List;
import java.util.Map;

import tech.sud.mgp.audio.example.manager.AudioRoomServiceManager;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.model.UserInfo;
import tech.sud.mgp.game.middle.model.GameMessageModel;

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

        /**
         * 设置回调
         */
        public void setCallback(AudioRoomServiceCallback callback) {
            serviceManager.setCallback(callback);
        }

        /**
         * 进入房间
         *
         * @param model
         */
        public void enterRoom(RoomInfoModel model) {
            serviceManager.enterRoom(model);
        }

        /**
         * 设置麦位
         *
         * @param micIndex 麦位索引
         * @param userId   用户id
         * @param operate  true为上麦位 false为下麦位
         */
        public void micLocationSwitch(int micIndex, long userId, boolean operate) {
            serviceManager.audioMicManager.micLocationSwitch(micIndex, userId, operate);
        }

        /**
         * 自动上麦
         */
        public void autoUpMic() {
            serviceManager.audioMicManager.autoUpMic();
        }

        /**
         * 发送公屏消息
         *
         * @param msg
         */
        public void sendPublicMsg(CharSequence msg) {
            serviceManager.audioChatManager.sendPublicMsg(msg);
        }

        public void sendGift(int giftID,
                             int giftCount,
                             UserInfo toUser) {
            serviceManager.audioGiftManager.sendGift(giftID, giftCount, toUser);
        }

        /**
         * 设置麦克风开关
         *
         * @param isOpen true为开 false为关
         */
        public void setMicState(boolean isOpen) {
            serviceManager.setMicState(isOpen);
        }

        /**
         * 获取麦位列表
         */
        public List<AudioRoomMicModel> getMicList() {
            return serviceManager.audioMicManager.getMicList();
        }

        /**
         * 游戏切换
         */
        public void switchGame(long gameId, boolean selfSwitch) {
            serviceManager.switchGame(gameId, selfSwitch);
        }

        /**
         * 更新礼物icon显示
         */
        public void updateGiftIcon(Map<Long, Boolean> userState) {
            serviceManager.audioMicManager.updateGiftIcon(userState);

        }

        /**
         * 添加一条公屏消息
         *
         * @param gameMessageModel
         */
        public void addChatMsg(GameMessageModel gameMessageModel) {
            serviceManager.audioChatManager.addMsg(gameMessageModel);
        }

        /**
         * 队长用户变化了
         *
         * @param captainUserId 队长用户id
         */
        public void captainChange(long captainUserId) {
            serviceManager.audioMicManager.captainChange(captainUserId);
        }
    }
}
