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
import androidx.lifecycle.Observer;

import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.hello.common.event.ChangeRTCEvent;
import tech.sud.mgp.hello.common.event.EnterRoomEvent;
import tech.sud.mgp.hello.common.event.LiveEventBusKey;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.ui.common.utils.channel.NotifyId;
import tech.sud.mgp.hello.ui.main.home.model.RoomItemModel;
import tech.sud.mgp.hello.ui.scenes.base.activity.RoomConfig;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneDiscoManager;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneRoomServiceManager;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.SceneRoomData;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.SceneRoomNotificationHelper;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.DanceModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.quiz.QuizBetModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.disco.model.DiscoInteractionModel;

/**
 * 房间服务
 */
public class SceneRoomService extends Service {

    private SceneRoomServiceManager serviceManager = new SceneRoomServiceManager();
    private final MyBinder binder = new MyBinder();
    private SceneRoomNotificationHelper notificationHelper;
    private Context context = this;
    private Observer<ChangeRTCEvent> changeRTCEventObserver;
    private Observer<EnterRoomEvent> enterRoomEventObserver;

    /** 房间数据 */
    private static SceneRoomData sceneRoomData;

    @Override
    public void onCreate() {
        super.onCreate();
        sceneRoomData = new SceneRoomData();

        // 通知栏处理
        notificationHelper = new SceneRoomNotificationHelper(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //android8.0及以后需要开启前台服务
            startForeground(NotifyId.SCENE_ROOM_NOTIFY_ID.getValue(), notificationHelper.createNotification());
        } else {
            notificationHelper.show();
        }

        // 事件监听
        setListeners();

        serviceManager.onCreate();
    }

    private void setListeners() {
        changeRTCEventObserver = new Observer<ChangeRTCEvent>() {
            @Override
            public void onChanged(ChangeRTCEvent event) {
                binder.exitRoom();
            }
        };
        LiveEventBus.<ChangeRTCEvent>get(LiveEventBusKey.KEY_CHANGE_RTC).observeForever(changeRTCEventObserver);
        enterRoomEventObserver = new Observer<EnterRoomEvent>() {
            @Override
            public void onChanged(EnterRoomEvent event) {
                if (getRoomId() != event.roomId && serviceManager.getCallback() == null) {
                    binder.exitRoom();
                }
            }
        };
        LiveEventBus.<EnterRoomEvent>get(LiveEventBusKey.KEY_ENTER_ROOM).observeForever(enterRoomEventObserver);
    }

    private long getRoomId() {
        if (sceneRoomData.roomInfoModel != null && sceneRoomData.roomInfoModel.roomId != null) {
            return sceneRoomData.roomInfoModel.roomId;
        }
        return 0;
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

        /** 移除回调 */
        public void removeCallback(SceneRoomServiceCallback callback) {
            serviceManager.removeCallback(callback);
        }

        /**
         * 进入房间
         *
         * @param model 进入的房间信息
         */
        public void enterRoom(RoomConfig config, Class<? extends Activity> startClass, RoomInfoModel model) {
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
                    serviceManager.onCreate();
                    serviceManager.setCallback(callback);
                }
            }

            // 3.首次进入
            sceneRoomData.roomInfoModel = model;
            serviceManager.enterRoom(config, startClass, model);

            // 重新进房之后，关闭自动猜自己赢
            AppData.getInstance().setQuizAutoGuessIWin(false);
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
         * 让机器人上麦
         *
         * @param aiPlayers 机器人数据
         * @param micIndex  位置
         */
        public void robotUpMicLocation(SudMGPAPPState.AIPlayers aiPlayers, int micIndex) {
            serviceManager.sceneMicManager.robotUpMicLocation(aiPlayers, micIndex);
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
            if (serviceManager.sceneDiscoManager != null) {
                if (msg != null) {
                    serviceManager.sceneDiscoManager.sendPublicMsg(msg.toString());
                }
            }
        }

        /** 发送礼物 */
        public void sendGift(GiftModel giftModel, int giftCount, UserInfo toUser) {
            serviceManager.sendGift(giftModel, giftCount, toUser);
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
        public void switchGame(long gameId) {
            serviceManager.switchGame(gameId);
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
            serviceManager.floatingManager.showFloating(context, model, startClass, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitRoom();
                }
            });
        }

        /** 隐藏悬浮窗 */
        public void dismissFloating() {
            serviceManager.floatingManager.dismissFloating();
        }

        /** 发起点单广播 */
        public void broadcastOrder(long orderId, long gameId, String gameName, List<UserInfo> toUsers) {
            serviceManager.sceneOrderManager.broadcastOrder(orderId, gameId, gameName, toUsers);
        }

        /** 跨房pk，开启匹配或者关闭Pk了 */
        public void roomPkSwitch(boolean pkSwitch) {
            serviceManager.sceneRoomPkManager.roomPkSwitch(pkSwitch);
        }

        /** 跨房pk，开始 */
        public void roomPkStart(int minute) {
            serviceManager.sceneRoomPkManager.roomPkStart(minute);
        }

        /** 跨房pk，发送邀请 */
        public void roomPkInvite(RoomItemModel model) {
            serviceManager.sceneRoomPkManager.roomPkInvite(model);
        }

        /** 同步自己的游戏给对方房间 */
        public void roomPkSyncGame() {
            serviceManager.sceneRoomPkManager.syncGame();
        }

        /**
         * 跨房pk，重新设置时长
         *
         * @param minute 分钟数
         */
        public void roomPkSettings(int minute) {
            serviceManager.sceneRoomPkManager.roomPkSettings(minute);
        }

        /** 移除pk对手 */
        public void removePkRival() {
            serviceManager.sceneRoomPkManager.removePkRival();
        }

        /** 刷新房间pk信息 */
        public void refreshRoomPkInfo() {
            serviceManager.sceneRoomPkManager.refreshRoomPkInfo();
        }

        /** 竞猜下注，进行通知 */
        public void notifyQuizBet(List<UserInfo> recUser) {
            QuizBetModel command = new QuizBetModel(RoomCmdModelUtils.getSendUser());
            command.recUser = recUser;
            serviceManager.sceneEngineManager.sendCommand(command.toJson());
            serviceManager.sceneQuizManager.addQuizBetChatMsg(command);
        }

        /** 开始拉视频流 */
        public void startVideo(String streamID, View view) {
            serviceManager.sceneEngineManager.startVideo(streamID, view);
        }

        /** 停止视频流 */
        public void stopVideo(String streamID, View view) {
            serviceManager.sceneEngineManager.stopVideo(streamID, view);
        }

        /** 获取跳舞集合 */
        public List<DanceModel> getDanceList() {
            return serviceManager.getDanceList();
        }

        /** 获取蹦迪贡献榜 */
        public List<ContributionModel> getDiscoContribution() {
            return serviceManager.getDiscoContribution();
        }

        /** 执行蹦迪动作 */
        public void exeDiscoAction(Long roomId, DiscoInteractionModel model, SceneDiscoManager.ActionListener actionListener) {
            if (serviceManager.sceneDiscoManager != null) {
                serviceManager.sceneDiscoManager.exeDiscoAction(roomId, model, actionListener);
            }
        }
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
        sceneRoomData = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //android8.0及以后需要开启前台服务，这里关闭服务
            stopForeground(true);
        } else {
            notificationHelper.hide();
        }
        serviceManager.onDestroy();
        if (changeRTCEventObserver != null) {
            LiveEventBus.<ChangeRTCEvent>get(LiveEventBusKey.KEY_CHANGE_RTC).removeObserver(changeRTCEventObserver);
            changeRTCEventObserver = null;
        }
        if (enterRoomEventObserver != null) {
            LiveEventBus.<EnterRoomEvent>get(LiveEventBusKey.KEY_ENTER_ROOM).removeObserver(enterRoomEventObserver);
            enterRoomEventObserver = null;
        }
    }
}
