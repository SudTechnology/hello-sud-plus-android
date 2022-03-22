package tech.sud.mgp.hello.ui.scenes.base.service;

import java.util.List;

import tech.sud.mgp.hello.ui.scenes.audio.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.activity.SceneConfig;
import tech.sud.mgp.hello.ui.scenes.base.manager.AudioRoomServiceManager;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

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
         * 初始化
         */
        public void init(SceneConfig config) {
            serviceManager.audioMicManager.init(config);
        }

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
         * 自己上下麦
         *
         * @param micIndex 麦位索引
         * @param operate  true为上麦位 false为下麦位
         */
        public void micLocationSwitch(int micIndex, boolean operate, OperateMicType type) {
            serviceManager.audioMicManager.micLocationSwitch(micIndex, operate, type);
        }

        /**
         * 自动上麦
         */
        public void autoUpMic(OperateMicType type) {
            serviceManager.audioMicManager.autoUpMic(type);
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
         * 添加一条公屏消息
         */
        public void addChatMsg(Object obj) {
            serviceManager.audioChatManager.addMsg(obj);
        }

        /**
         * 更新麦位
         */
        public void updateMicList() {
            serviceManager.audioMicManager.notifyDataSetChange();
        }

        /**
         * 设置ASR
         */
        public void setASROpen(boolean isOpen) {
            serviceManager.audioGameManager.setASROpen(isOpen);
        }

        /**
         * 设置RTC拉流
         */
        public void setRTCPlay(boolean isOn) {
            serviceManager.audioGameManager.setRTCPlay(isOn);
        }

        /**
         * 退出房间
         */
        public void exitRoom() {
            serviceManager.exitRoom();
        }
    }
}
