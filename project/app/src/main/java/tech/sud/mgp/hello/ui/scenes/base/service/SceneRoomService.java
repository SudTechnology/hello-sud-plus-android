package tech.sud.mgp.hello.ui.scenes.base.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.List;

import tech.sud.mgp.hello.ui.common.utils.channel.NotifyId;
import tech.sud.mgp.hello.ui.scenes.base.activity.SceneConfig;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneRoomServiceManager;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.SceneRoomNotificationHelper;

/**
 * 房间服务
 */
public class SceneRoomService extends Service {

    private final SceneRoomServiceManager serviceManager = new SceneRoomServiceManager();
    private final MyBinder binder = new MyBinder();
    private SceneRoomNotificationHelper notificationHelper;

    private static RoomInfoModel roomInfoModel;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new SceneRoomNotificationHelper(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //android8.0及以后需要开启前台服务
            startForeground(NotifyId.SCENE_ROOM_NOTIFY_ID.getValue(), notificationHelper.createNotification());
        } else {
            notificationHelper.show();
        }
        serviceManager.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public MyBinder getBinder() {
        return binder;
    }

    public class MyBinder extends Binder {

        /**
         * 初始化
         */
        public void init(SceneConfig config, Class<? extends Activity> startClass) {
            serviceManager.sceneMicManager.init(config);
            notificationHelper.setStartClass(startClass);
        }

        /**
         * 设置回调
         */
        public void setCallback(SceneRoomServiceCallback callback) {
            serviceManager.setCallback(callback);
        }

        /**
         * 进入房间
         *
         * @param model 进入的房间信息
         */
        public void enterRoom(RoomInfoModel model) {
            roomInfoModel = model;
            serviceManager.enterRoom(model);
            notificationHelper.setRoomName(model.roomName);
        }

        /**
         * 自己上下麦
         *
         * @param micIndex 麦位索引
         * @param operate  true为上麦位 false为下麦位
         */
        public void micLocationSwitch(int micIndex, boolean operate, OperateMicType type) {
            serviceManager.sceneMicManager.micLocationSwitch(micIndex, operate, type);
        }

        /**
         * 自动上麦
         */
        public void autoUpMic(OperateMicType type) {
            serviceManager.sceneMicManager.autoUpMic(type);
        }

        /**
         * 发送公屏消息
         *
         * @param msg
         */
        public void sendPublicMsg(CharSequence msg) {
            serviceManager.sceneChatManager.sendPublicMsg(msg);
        }

        public void sendGift(int giftID, int giftCount, UserInfo toUser) {
            serviceManager.sceneGiftManager.sendGift(giftID, giftCount, toUser);
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
            return serviceManager.sceneMicManager.getMicList();
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
            serviceManager.sceneChatManager.addMsg(obj);
        }

        /**
         * 更新麦位
         */
        public void updateMicList() {
            serviceManager.sceneMicManager.notifyDataSetChange();
        }

        /**
         * 设置ASR
         */
        public void setASROpen(boolean isOpen) {
            serviceManager.sceneGameManager.setASROpen(isOpen);
        }

        /**
         * 设置RTC拉流
         */
        public void setRTCPlay(boolean isOn) {
            serviceManager.sceneGameManager.setRTCPlay(isOn);
        }

        /**
         * 退出房间
         */
        public void exitRoom() {
            serviceManager.exitRoom();
            stopSelf();
        }

        /**
         * 当前是否是开麦的
         */
        public boolean isOpenedMic() {
            return serviceManager.sceneStreamManager.isPublishingStream();
        }

    }

    // 获取当前使用的房间信息
    public static RoomInfoModel getRoomInfoModel() {
        return roomInfoModel;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        roomInfoModel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //android8.0及以后需要开启前台服务，这里关闭服务
            stopForeground(true);
        } else {
            notificationHelper.hide();
        }
        serviceManager.onDestroy();
    }
}
