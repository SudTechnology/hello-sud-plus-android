package tech.sud.mgp.hello.ui.scenes.base.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import tech.sud.mgp.hello.ui.common.utils.channel.NotifyId;
import tech.sud.mgp.hello.ui.scenes.base.activity.SceneConfig;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneFloatingManager;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneRoomServiceManager;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.SceneRoomData;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.SceneRoomNotificationHelper;

/**
 * 房间服务
 */
public class SceneRoomService extends Service {

    private SceneRoomServiceManager serviceManager = new SceneRoomServiceManager();
    private SceneFloatingManager floatingManager = new SceneFloatingManager();
    private final MyBinder binder = new MyBinder();
    private SceneRoomNotificationHelper notificationHelper;
    private Context context = this;

    /** 房间数据 */
    private static SceneRoomData sceneRoomData;

    @Override
    public void onCreate() {
        super.onCreate();
        sceneRoomData = new SceneRoomData();
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

    public class MyBinder extends Binder {

        /** 设置回调 */
        public void setCallback(SceneRoomServiceCallback callback) {
            serviceManager.setCallback(callback);
        }

        /**
         * 进入房间
         *
         * @param model 进入的房间信息
         */
        public void enterRoom(SceneConfig config, Class<? extends Activity> startClass, RoomInfoModel model) {
            notificationHelper.setData(model.roomName, startClass);

            if (sceneRoomData.roomInfoModel != null && sceneRoomData.roomInfoModel.roomId != null) {
                if (sceneRoomData.roomInfoModel.roomId.equals(model.roomId)) {
                    // 1.不是第一次创建，只是恢复页面显示，回调数据给页面
                    serviceManager.callbackPageData();
                    return;
                } else {
                    // 2.切换房间，销毁原有房间数据，然后再执行进入房间
                    SceneRoomServiceCallback callback = serviceManager.getCallback();
                    serviceManager.exitRoom();
                    serviceManager.onDestroy();
                    serviceManager = new SceneRoomServiceManager();
                    serviceManager.setCallback(callback);
                }
            }

            // 3.首次进入
            sceneRoomData.roomInfoModel = model;
            serviceManager.enterRoom(config, model);
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

        /** 自动上麦 */
        public void autoUpMic(OperateMicType type) {
            serviceManager.sceneMicManager.autoUpMic(type);
        }

        /**
         * 发送公屏消息
         *
         * @param msg 消息
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

        /** 获取麦位列表 */
        public List<AudioRoomMicModel> getMicList() {
            return serviceManager.sceneMicManager.getMicList();
        }

        /** 游戏切换 */
        public void switchGame(long gameId, boolean selfSwitch) {
            serviceManager.switchGame(gameId, selfSwitch);
        }

        /** 添加一条公屏消息 */
        public void addChatMsg(Object obj) {
            serviceManager.sceneChatManager.addMsg(obj);
        }

        /** 更新麦位 */
        public void updateMicList() {
            serviceManager.sceneMicManager.notifyDataSetChange();
        }

        /** 设置ASR */
        public void setASROpen(boolean isOpen) {
            serviceManager.sceneGameManager.setASROpen(isOpen);
        }

        /** 设置RTC拉流 */
        public void setRTCPlay(boolean isOn) {
            serviceManager.sceneGameManager.setRTCPlay(isOn);
        }

        /** 退出房间 */
        public void exitRoom() {
            serviceManager.exitRoom();
            stopSelf();
        }

        /** 当前是否是开麦的 */
        public boolean isOpenedMic() {
            return serviceManager.sceneStreamManager.isPublishingStream();
        }

        /** 显示悬浮窗 */
        public void showFloating(RoomInfoModel model, Class<? extends Activity> startClass) {
            floatingManager.showFloating(context, model, startClass, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitRoom();
                }
            });
        }

        /** 隐藏悬浮窗 */
        public void dismissFloating() {
            floatingManager.dismissFloating();
        }

        /** 发起点单广播*/
        public void broadcastOrder(long orderId, long orderTimeMillis, List<Integer> toUsers){serviceManager.sceneOrderManager.broadcastOrder(orderId, orderTimeMillis, toUsers);}
    }

    /** 获取当前使用的房间基本数据 */
    public static RoomInfoModel getRoomInfoModel() {
        if (sceneRoomData != null) {
            return sceneRoomData.roomInfoModel;
        }
        return null;
    }

    /** 获取房间数据 */
    public static SceneRoomData getSceneRoomData() {
        return sceneRoomData;
    }

    /** 是否已在房间里 */
    public static boolean isRunning() {
        return sceneRoomData != null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        floatingManager.dismissFloating();
        sceneRoomData = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //android8.0及以后需要开启前台服务，这里关闭服务
            stopForeground(true);
        } else {
            notificationHelper.hide();
        }
        serviceManager.onDestroy();
    }
}
